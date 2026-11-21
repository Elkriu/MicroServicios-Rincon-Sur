package com.rinconsur.dashboard.controller;

import com.rinconsur.dashboard.dto.DistribucionResponse;
import com.rinconsur.dashboard.dto.RegistroDetalleResponse;
import com.rinconsur.dashboard.dto.ResumenDiarioResponse;
import com.rinconsur.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/fechas")
    public List<String> fechas() {
        return dashboardService.fechas();
    }

    @GetMapping("/resumen")
    public ResumenDiarioResponse resumen(@RequestParam String fecha) {
        return dashboardService.resumen(fecha);
    }

    @GetMapping("/distribucion")
    public DistribucionResponse distribucion(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String acceso) {
        return dashboardService.distribucion(fecha, acceso);
    }

    @GetMapping("/registros")
    public List<RegistroDetalleResponse> registros(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String acceso) {
        return dashboardService.registros(fecha, acceso);
    }
}