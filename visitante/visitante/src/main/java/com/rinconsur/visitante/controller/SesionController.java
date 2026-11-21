package com.rinconsur.visitante.controller;

import com.rinconsur.visitante.dto.CrearSesionRequest;
import com.rinconsur.visitante.dto.EstadisticasResponse;
import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroResponse;
import com.rinconsur.visitante.dto.SesionResponse;
import com.rinconsur.visitante.service.SesionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sesiones", description = "Sesiones de conteo de un observador en un acceso y los registros de cada sesión")
@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @Operation(summary = "Listar las sesiones de un observador",
            description = "Devuelve todas las sesiones del observador indicado, de la más reciente a la más antigua.")
    @ApiResponse(responseCode = "200", description = "Lista de sesiones (puede venir vacía)")
    @GetMapping
    public List<SesionResponse> listar(
            @Parameter(description = "Id del observador (usuario del servicio de usuarios)", example = "1", required = true)
            @RequestParam Long usuarioId) {
        return sesionService.listarPorUsuario(usuarioId);
    }

    @Operation(summary = "Estadísticas de un observador",
            description = "Cuenta las sesiones totales, las finalizadas y los registros de conteo del observador. "
                    + "Si el observador no tiene sesiones, todos los valores son 0.")
    @ApiResponse(responseCode = "200", description = "Estadísticas del observador")
    @GetMapping("/estadisticas")
    public EstadisticasResponse estadisticas(
            @Parameter(description = "Id del observador (usuario del servicio de usuarios)", example = "1", required = true)
            @RequestParam Long usuarioId) {
        return sesionService.estadisticas(usuarioId);
    }

    @Operation(summary = "Obtener una sesión por id")
    @ApiResponse(responseCode = "200", description = "Sesión encontrada")
    @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    @GetMapping("/{id}")
    public SesionResponse obtener(
            @Parameter(description = "Id de la sesión", example = "1") @PathVariable Long id) {
        return sesionService.obtener(id);
    }

    @Operation(summary = "Listar las sesiones activas",
            description = "Devuelve las sesiones que todavía no están finalizadas, de todos los observadores. "
                    + "Sirve para saber qué accesos y qué observadores están ocupados.")
    @ApiResponse(responseCode = "200", description = "Lista de sesiones activas (puede venir vacía)")
    @GetMapping("/activas")
    public List<SesionResponse> listarActivas() {
        return sesionService.listarActivas();
    }

    @Operation(summary = "Iniciar una sesión de conteo",
            description = "Consulta al servicio de usuarios que el observador exista, tenga rol Observador de Pasillo y la cuenta "
                    + "activa, y al servicio de administración que el acceso exista y esté disponible. Además, un observador "
                    + "solo puede tener una sesión activa y un acceso solo puede estar en uso por una sesión a la vez. "
                    + "La sesión queda EN_CURSO con la fecha de hoy y un código generado.")
    @ApiResponse(responseCode = "201", description = "Sesión creada")
    @ApiResponse(responseCode = "400", description = "Falta el observador o el acceso, o el usuario indicado no es Observador")
    @ApiResponse(responseCode = "403", description = "La cuenta del observador está suspendida")
    @ApiResponse(responseCode = "404", description = "El observador o el acceso no existen")
    @ApiResponse(responseCode = "409", description = "El acceso está cerrado o en uso, o el observador ya tiene una sesión activa")
    @ApiResponse(responseCode = "502", description = "El servicio de usuarios o el de administración respondió con un error")
    @ApiResponse(responseCode = "503", description = "No se pudo contactar con el servicio de usuarios o con el de administración")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SesionResponse crear(@RequestBody CrearSesionRequest datos) {
        return sesionService.crear(datos);
    }

    @Operation(summary = "Finalizar una sesión",
            description = "Cambia el estado a FINALIZADA, con lo que el observador y el acceso quedan libres. "
                    + "Si la sesión ya estaba finalizada, no cambia nada.")
    @ApiResponse(responseCode = "200", description = "Sesión finalizada")
    @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    @PutMapping("/{id}/finalizar")
    public SesionResponse finalizar(
            @Parameter(description = "Id de la sesión", example = "3") @PathVariable Long id) {
        return sesionService.finalizar(id);
    }

    @Operation(summary = "Listar los registros de una sesión",
            description = "Devuelve los registros de conteo de la sesión, del más antiguo al más reciente.")
    @ApiResponse(responseCode = "200", description = "Lista de registros (puede venir vacía)")
    @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    @GetMapping("/{id}/registros")
    public List<RegistroResponse> listarRegistros(
            @Parameter(description = "Id de la sesión", example = "1") @PathVariable Long id) {
        return sesionService.listarRegistros(id);
    }

    @Operation(summary = "Guardar un registro en una sesión",
            description = "Agrega un registro de conteo. Clasificación, rango etario, compañía y hora son obligatorios. "
                    + "No se pueden agregar registros a una sesión finalizada.")
    @ApiResponse(responseCode = "201", description = "Registro guardado")
    @ApiResponse(responseCode = "400", description = "Faltan datos del registro")
    @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    @ApiResponse(responseCode = "409", description = "La sesión ya está finalizada")
    @PostMapping("/{id}/registros")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse guardarRegistro(
            @Parameter(description = "Id de la sesión", example = "3") @PathVariable Long id,
            @RequestBody GuardarRegistroRequest datos) {
        return sesionService.guardarRegistro(id, datos);
    }

    @Operation(summary = "Anular el último registro de una sesión",
            description = "Elimina el registro más reciente de la sesión. Sirve para deshacer un conteo hecho por error.")
    @ApiResponse(responseCode = "204", description = "Registro eliminado (sin contenido en la respuesta)")
    @ApiResponse(responseCode = "404", description = "La sesión no existe o no tiene registros")
    @DeleteMapping("/{id}/registros/ultimo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anularUltimoRegistro(
            @Parameter(description = "Id de la sesión", example = "3") @PathVariable Long id) {
        sesionService.anularUltimoRegistro(id);
    }
}