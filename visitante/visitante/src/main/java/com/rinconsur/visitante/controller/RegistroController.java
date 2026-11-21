package com.rinconsur.visitante.controller;

import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroDetalleResponse;
import com.rinconsur.visitante.service.RegistroService;
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

@RestController
@RequestMapping("/api/registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroService registroService;

    @GetMapping
    public List<RegistroDetalleResponse> listar(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String acceso) {
        return registroService.listar(fecha, acceso);
    }

    @GetMapping("/fechas")
    public List<String> fechas() {
        return registroService.fechas();
    }

    @PutMapping("/{id}")
    public RegistroDetalleResponse editar(@PathVariable Long id,
                                          @RequestBody GuardarRegistroRequest solicitud) {
        return registroService.editar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        registroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}