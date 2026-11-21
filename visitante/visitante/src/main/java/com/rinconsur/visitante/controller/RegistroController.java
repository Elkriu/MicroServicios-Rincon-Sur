package com.rinconsur.visitante.controller;

import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroDetalleResponse;
import com.rinconsur.visitante.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Registros", description = "Consulta, edición y eliminación de los registros de conteo de todas las sesiones")
@RestController
@RequestMapping("/api/registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroService registroService;

    @Operation(summary = "Listar registros",
            description = "Devuelve los registros de conteo con los datos de su sesión. Los dos filtros son opcionales y se pueden combinar.")
    @ApiResponse(responseCode = "200", description = "Lista de registros (puede venir vacía)")
    @GetMapping
    public List<RegistroDetalleResponse> listar(
            @Parameter(description = "Fecha de la sesión en formato dd-MM-yyyy", example = "01-11-2026")
            @RequestParam(required = false) String fecha,
            @Parameter(description = "Nombre del acceso donde se hizo el conteo", example = "Acceso A1")
            @RequestParam(required = false) String acceso) {
        return registroService.listar(fecha, acceso);
    }

    @Operation(summary = "Listar fechas con registros",
            description = "Devuelve las fechas (dd-MM-yyyy) en las que hay al menos un registro, de la más reciente a la más antigua. "
                    + "Sirve para llenar el selector de fecha antes de consultar los registros.")
    @ApiResponse(responseCode = "200", description = "Lista de fechas (puede venir vacía)")
    @GetMapping("/fechas")
    public List<String> fechas() {
        return registroService.fechas();
    }

    @Operation(summary = "Editar un registro",
            description = "Clasificación, rango etario y compañía son obligatorios. Reemplaza los datos del registro con lo enviado: "
                    + "si no se envía el destino, se borra. La hora solo cambia si se envía.")
    @ApiResponse(responseCode = "200", description = "Registro actualizado")
    @ApiResponse(responseCode = "400", description = "Falta la clasificación, el rango etario o la compañía")
    @ApiResponse(responseCode = "404", description = "El registro no existe")
    @PutMapping("/{id}")
    public RegistroDetalleResponse editar(
            @Parameter(description = "Id del registro", example = "1") @PathVariable Long id,
            @RequestBody GuardarRegistroRequest solicitud) {
        return registroService.editar(id, solicitud);
    }

    @Operation(summary = "Eliminar un registro")
    @ApiResponse(responseCode = "204", description = "Registro eliminado (sin contenido en la respuesta)")
    @ApiResponse(responseCode = "404", description = "El registro no existe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del registro", example = "1") @PathVariable Long id) {
        registroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}