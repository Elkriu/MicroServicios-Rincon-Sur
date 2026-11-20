package com.rinconsur.administracion.service;

import com.rinconsur.administracion.dto.AccesoResponse;
import com.rinconsur.administracion.dto.CambiarDisponibilidadRequest;
import com.rinconsur.administracion.model.Acceso;
import com.rinconsur.administracion.repository.AccesoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final AccesoRepository accesoRepository;

    public List<AccesoResponse> listar(Boolean disponible) {
        List<Acceso> accesos = (disponible == null)
                ? accesoRepository.findAllByOrderByNombreAsc()
                : accesoRepository.findByDisponibleOrderByNombreAsc(disponible);

        return accesos.stream()
                .map(this::aRespuesta)
                .toList();
    }

    public AccesoResponse obtener(String nombre) {
        return aRespuesta(buscar(nombre));
    }

    public AccesoResponse cambiarDisponibilidad(String nombre, CambiarDisponibilidadRequest datos) {
        if (datos.disponible() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar si el acceso queda disponible");
        }
        Acceso acceso = buscar(nombre);
        acceso.setDisponible(datos.disponible());
        return aRespuesta(accesoRepository.save(acceso));
    }

    private Acceso buscar(String nombre) {
        return accesoRepository.findById(nombre)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Acceso no encontrado"));
    }

    private AccesoResponse aRespuesta(Acceso acceso) {
        return new AccesoResponse(acceso.getNombre(), acceso.isDisponible());
    }
}
