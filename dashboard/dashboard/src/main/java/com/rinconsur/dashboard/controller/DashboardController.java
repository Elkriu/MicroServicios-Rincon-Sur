package com.rinconsur.dashboard.controller;

import com.rinconsur.dashboard.dto.DistribucionResponse;
import com.rinconsur.dashboard.dto.RegistroDetalleResponse;
import com.rinconsur.dashboard.dto.ResumenDiarioResponse;
import com.rinconsur.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Dashboard", description = "Resúmenes y distribuciones del conteo de visitantes. "
        + "Los datos se piden al servicio de visitantes en cada consulta")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Listar fechas con registros",
            description = "Pide al servicio de visitantes las fechas (dd-MM-yyyy) en las que hay al menos un registro, "
                    + "de la más reciente a la más antigua. Sirve para llenar el selector de fecha del dashboard.")
    @ApiResponse(responseCode = "200", description = "Lista de fechas (puede venir vacía)")
    @ApiResponse(responseCode = "502", description = "El servicio de visitantes respondió con un error")
    @ApiResponse(responseCode = "503", description = "No se pudo contactar con el servicio de visitantes")
    @GetMapping("/fechas")
    public List<String> fechas() {
        return dashboardService.fechas();
    }

    @Operation(summary = "Resumen de un día",
            description = "Cuenta los registros del día, las sesiones que tuvieron registros y el flujo por acceso. "
                    + "Si ese día no hay registros no da error: devuelve ceros y una lista vacía.")
    @ApiResponse(responseCode = "200", description = "Resumen del día")
    @ApiResponse(responseCode = "400", description = "Falta la fecha o no tiene el formato dd-MM-yyyy")
    @ApiResponse(responseCode = "502", description = "El servicio de visitantes respondió con un error")
    @ApiResponse(responseCode = "503", description = "No se pudo contactar con el servicio de visitantes")
    @GetMapping("/resumen")
    public ResumenDiarioResponse resumen(
            @Parameter(description = "Fecha en formato dd-MM-yyyy", example = "01-11-2026", required = true)
            @RequestParam String fecha) {
        return dashboardService.resumen(fecha);
    }

    @Operation(summary = "Distribución de los registros",
            description = "Agrupa los registros por clasificación, rango etario, compañía y bolsas. Los dos filtros son opcionales "
                    + "y se pueden combinar: sin filtros calcula sobre todos los registros. "
                    + "Si no hay registros, devuelve total 0 y las listas vacías.")
    @ApiResponse(responseCode = "200", description = "Distribución calculada")
    @ApiResponse(responseCode = "400", description = "La fecha no tiene el formato dd-MM-yyyy")
    @ApiResponse(responseCode = "502", description = "El servicio de visitantes respondió con un error")
    @ApiResponse(responseCode = "503", description = "No se pudo contactar con el servicio de visitantes")
    @GetMapping("/distribucion")
    public DistribucionResponse distribucion(
            @Parameter(description = "Fecha en formato dd-MM-yyyy", example = "01-11-2026")
            @RequestParam(required = false) String fecha,
            @Parameter(description = "Nombre del acceso donde se hizo el conteo", example = "Acceso A1")
            @RequestParam(required = false) String acceso) {
        return dashboardService.distribucion(fecha, acceso);
    }

    @Operation(summary = "Listar los registros en detalle",
            description = "Devuelve los registros tal como los entrega el servicio de visitantes, sin agrupar. "
                    + "Los dos filtros son opcionales y se pueden combinar.")
    @ApiResponse(responseCode = "200", description = "Lista de registros (puede venir vacía)")
    @ApiResponse(responseCode = "400", description = "La fecha no tiene el formato dd-MM-yyyy")
    @ApiResponse(responseCode = "502", description = "El servicio de visitantes respondió con un error")
    @ApiResponse(responseCode = "503", description = "No se pudo contactar con el servicio de visitantes")
    @GetMapping("/registros")
    public List<RegistroDetalleResponse> registros(
            @Parameter(description = "Fecha en formato dd-MM-yyyy", example = "01-11-2026")
            @RequestParam(required = false) String fecha,
            @Parameter(description = "Nombre del acceso donde se hizo el conteo", example = "Acceso A1")
            @RequestParam(required = false) String acceso) {
        return dashboardService.registros(fecha, acceso);
    }
}