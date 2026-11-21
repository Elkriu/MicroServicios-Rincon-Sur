package com.rinconsur.visitante.service;

import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroDetalleResponse;
import com.rinconsur.visitante.model.RegistroConteo;
import com.rinconsur.visitante.repository.RegistroConteoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final RegistroConteoRepository registroRepository;

    @Transactional(readOnly = true)
    public List<RegistroDetalleResponse> listar(String fecha, String acceso) {
        return registroRepository.buscar(normalizar(fecha), normalizar(acceso)).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> fechas() {
        return registroRepository.fechasConRegistros().stream()
                .sorted(Comparator.comparing((String f) -> LocalDate.parse(f, FORMATO_FECHA)).reversed())
                .toList();
    }

    @Transactional
    public RegistroDetalleResponse editar(Long id, GuardarRegistroRequest solicitud) {
        RegistroConteo registro = registroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El registro no existe"));

        if (normalizar(solicitud.clasificacion()) == null
                || normalizar(solicitud.rangoEtario()) == null
                || normalizar(solicitud.compania()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Clasificación, rango etario y compañía son obligatorios");
        }

        registro.setClasificacion(solicitud.clasificacion().trim());
        registro.setRangoEtario(solicitud.rangoEtario().trim());
        registro.setCompania(solicitud.compania().trim());
        registro.setBolsas(solicitud.bolsas());
        registro.setDestino(normalizar(solicitud.destino()));
        if (normalizar(solicitud.hora()) != null) {
            registro.setHora(solicitud.hora().trim());
        }

        return aRespuesta(registroRepository.save(registro));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!registroRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El registro no existe");
        }
        registroRepository.deleteById(id);
    }

    private RegistroDetalleResponse aRespuesta(RegistroConteo r) {
        return new RegistroDetalleResponse(
                r.getId(),
                r.getSesion().getId(),
                r.getSesion().getAcceso(),
                r.getSesion().getFecha(),
                r.getHora(),
                r.getClasificacion(),
                r.getRangoEtario(),
                r.getCompania(),
                r.isBolsas(),
                r.getDestino()
        );
    }

    private String normalizar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}