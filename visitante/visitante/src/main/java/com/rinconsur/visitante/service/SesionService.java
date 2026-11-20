package com.rinconsur.visitante.service;

import com.rinconsur.visitante.client.UsuarioClient;
import com.rinconsur.visitante.dto.CrearSesionRequest;
import com.rinconsur.visitante.dto.EstadisticasResponse;
import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroResponse;
import com.rinconsur.visitante.dto.SesionResponse;
import com.rinconsur.visitante.dto.UsuarioResponse;
import com.rinconsur.visitante.model.RegistroConteo;
import com.rinconsur.visitante.model.Sesion;
import com.rinconsur.visitante.repository.RegistroConteoRepository;
import com.rinconsur.visitante.repository.SesionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SesionService {

    private static final long ROL_OBSERVADOR = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final SesionRepository sesionRepository;
    private final RegistroConteoRepository registroRepository;
    private final UsuarioClient usuarioClient;

    public List<SesionResponse> listarActivas() {
        return sesionRepository.findByEstadoNot(Sesion.FINALIZADA).stream()
                .map(this::aRespuesta)
                .toList();
    }

    public List<SesionResponse> listarPorUsuario(Long usuarioId) {
        return sesionRepository.findByUsuarioIdOrderByIdDesc(usuarioId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    public SesionResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    public EstadisticasResponse estadisticas(Long usuarioId) {
        return new EstadisticasResponse(
                sesionRepository.countByUsuarioId(usuarioId),
                sesionRepository.countByUsuarioIdAndEstado(usuarioId, Sesion.FINALIZADA),
                registroRepository.countBySesionUsuarioId(usuarioId)
        );
    }

    @Transactional
    public SesionResponse crear(CrearSesionRequest datos) {
        if (datos.usuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar el observador");
        }
        if (datos.acceso() == null || datos.acceso().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar el acceso");
        }
        String acceso = datos.acceso().trim();

        UsuarioResponse observador = usuarioClient.obtener(datos.usuarioId());
        if (observador.rolId() == null || observador.rolId() != ROL_OBSERVADOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario seleccionado no es Observador");
        }
        if (!observador.activo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta del observador está suspendida");
        }
        if (sesionRepository.existsByUsuarioIdAndEstadoNot(datos.usuarioId(), Sesion.FINALIZADA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El observador ya tiene una sesión activa");
        }
        if (sesionRepository.existsByAccesoAndEstadoNot(acceso, Sesion.FINALIZADA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese acceso ya está en uso");
        }

        Sesion nueva = Sesion.builder()
                .codigo(generarCodigo())
                .acceso(acceso)
                .fecha(LocalDate.now().format(FORMATO_FECHA))
                .estado(Sesion.EN_CURSO)
                .usuarioId(datos.usuarioId())
                .build();
        return aRespuesta(sesionRepository.save(nueva));
    }

    @Transactional
    public SesionResponse finalizar(Long id) {
        Sesion sesion = buscar(id);
        sesion.setEstado(Sesion.FINALIZADA);
        return aRespuesta(sesionRepository.save(sesion));
    }

    public List<RegistroResponse> listarRegistros(Long sesionId) {
        buscar(sesionId);
        return registroRepository.findBySesionIdOrderByIdAsc(sesionId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional
    public RegistroResponse guardarRegistro(Long sesionId, GuardarRegistroRequest datos) {
        Sesion sesion = buscar(sesionId);
        if (Sesion.FINALIZADA.equals(sesion.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sesión ya está finalizada");
        }
        if (vacio(datos.clasificacion()) || vacio(datos.rangoEtario()) || vacio(datos.compania()) || vacio(datos.hora())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos del registro");
        }

        RegistroConteo registro = RegistroConteo.builder()
                .sesion(sesion)
                .clasificacion(datos.clasificacion())
                .rangoEtario(datos.rangoEtario())
                .compania(datos.compania())
                .bolsas(datos.bolsas())
                .destino(datos.destino())
                .hora(datos.hora())
                .build();
        return aRespuesta(registroRepository.save(registro));
    }

    @Transactional
    public void anularUltimoRegistro(Long sesionId) {
        buscar(sesionId);
        RegistroConteo ultimo = registroRepository.findFirstBySesionIdOrderByIdDesc(sesionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La sesión no tiene registros"));
        registroRepository.delete(ultimo);
    }

    private Sesion buscar(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
    }

    private String generarCodigo() {
        String codigo;
        do {
            codigo = "SES-" + ThreadLocalRandom.current().nextInt(100, 1000);
        } while (sesionRepository.existsByCodigo(codigo));
        return codigo;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private SesionResponse aRespuesta(Sesion s) {
        return new SesionResponse(
                s.getId(), s.getCodigo(), s.getAcceso(), s.getFecha(), s.getEstado(), s.getUsuarioId(),
                (int) registroRepository.countBySesionId(s.getId())
        );
    }

    private RegistroResponse aRespuesta(RegistroConteo r) {
        return new RegistroResponse(
                r.getId(), r.getHora(), r.getClasificacion(), r.getRangoEtario(),
                r.getCompania(), r.isBolsas(), r.getDestino()
        );
    }
}