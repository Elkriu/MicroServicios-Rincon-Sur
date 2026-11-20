package com.rinconsur.administracion.controller;

import com.rinconsur.administracion.dto.AccesoResponse;
import com.rinconsur.administracion.dto.CambiarDisponibilidadRequest;
import com.rinconsur.administracion.service.AccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accesos")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    @GetMapping
    public List<AccesoResponse> listar(@RequestParam(required = false) Boolean disponible) {
        return accesoService.listar(disponible);
    }

    @GetMapping("/{nombre}")
    public AccesoResponse obtener(@PathVariable String nombre) {
        return accesoService.obtener(nombre);
    }

    @PutMapping("/{nombre}/disponibilidad")
    public AccesoResponse cambiarDisponibilidad(@PathVariable String nombre,
                                                @RequestBody CambiarDisponibilidadRequest datos) {
        return accesoService.cambiarDisponibilidad(nombre, datos);
    }
}