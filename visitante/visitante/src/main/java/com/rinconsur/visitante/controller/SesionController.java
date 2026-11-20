package com.rinconsur.visitante.controller;

import com.rinconsur.visitante.dto.CrearSesionRequest;
import com.rinconsur.visitante.dto.EstadisticasResponse;
import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroResponse;
import com.rinconsur.visitante.dto.SesionResponse;
import com.rinconsur.visitante.service.SesionService;
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

@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @GetMapping
    public List<SesionResponse> listar(@RequestParam Long usuarioId) {
        return sesionService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/estadisticas")
    public EstadisticasResponse estadisticas(@RequestParam Long usuarioId) {
        return sesionService.estadisticas(usuarioId);
    }

    @GetMapping("/{id}")
    public SesionResponse obtener(@PathVariable Long id) {
        return sesionService.obtener(id);
    }

    @GetMapping("/activas")
    public List<SesionResponse> listarActivas() {
        return sesionService.listarActivas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SesionResponse crear(@RequestBody CrearSesionRequest datos) {
        return sesionService.crear(datos);
    }

    @PutMapping("/{id}/finalizar")
    public SesionResponse finalizar(@PathVariable Long id) {
        return sesionService.finalizar(id);
    }

    @GetMapping("/{id}/registros")
    public List<RegistroResponse> listarRegistros(@PathVariable Long id) {
        return sesionService.listarRegistros(id);
    }

    @PostMapping("/{id}/registros")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse guardarRegistro(@PathVariable Long id, @RequestBody GuardarRegistroRequest datos) {
        return sesionService.guardarRegistro(id, datos);
    }

    @DeleteMapping("/{id}/registros/ultimo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anularUltimoRegistro(@PathVariable Long id) {
        sesionService.anularUltimoRegistro(id);
    }
}