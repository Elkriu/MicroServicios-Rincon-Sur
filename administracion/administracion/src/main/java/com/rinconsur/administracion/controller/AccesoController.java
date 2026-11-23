package com.rinconsur.administracion.controller;

import com.rinconsur.administracion.dto.AccesoResponse;
import com.rinconsur.administracion.dto.CambiarDisponibilidadRequest;
import com.rinconsur.administracion.service.AccesoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Accesos", description = "Gestión de los accesos (entradas) de la tienda y su disponibilidad")
@RestController
@RequestMapping("/api/accesos")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    @Operation(summary = "Listar accesos",
            description = "Devuelve los accesos ordenados por nombre. El filtro es opcional: "
                    + "sin filtro devuelve todos, con disponible=true solo los abiertos y con disponible=false solo los cerrados.")
    @ApiResponse(responseCode = "200", description = "Lista de accesos (puede venir vacía)")
    @GetMapping
    public List<AccesoResponse> listar(
            @Parameter(description = "true = solo accesos abiertos, false = solo accesos cerrados", example = "true")
            @RequestParam(required = false) Boolean disponible) {
        return accesoService.listar(disponible);
    }

    @Operation(summary = "Obtener un acceso por nombre",
            description = "El nombre es el identificador del acceso y puede llevar espacios, por ejemplo Acceso A1.")
    @ApiResponse(responseCode = "200", description = "Acceso encontrado")
    @ApiResponse(responseCode = "404", description = "Acceso no encontrado")
    @GetMapping("/{nombre}")
    public AccesoResponse obtener(
            @Parameter(description = "Nombre del acceso", example = "Acceso A1") @PathVariable String nombre) {
        return accesoService.obtener(nombre);
    }

    @Operation(summary = "Abrir o cerrar un acceso",
            description = "Con disponible=false el acceso queda cerrado y el servicio de visitantes no permite iniciar "
                    + "sesiones de conteo en él. Con disponible=true se vuelve a abrir.")
    @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada")
    @ApiResponse(responseCode = "400", description = "No se indicó el campo disponible")
    @ApiResponse(responseCode = "404", description = "Acceso no encontrado")
    @PutMapping("/{nombre}/disponibilidad")
    public AccesoResponse cambiarDisponibilidad(
            @Parameter(description = "Nombre del acceso", example = "Acceso A1") @PathVariable String nombre,
            @RequestBody CambiarDisponibilidadRequest datos) {
        return accesoService.cambiarDisponibilidad(nombre, datos);
    }
}