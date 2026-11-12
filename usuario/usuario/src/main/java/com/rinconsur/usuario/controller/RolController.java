package com.rinconsur.usuario.controller;

import com.rinconsur.usuario.dto.RolResponse;
import com.rinconsur.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<RolResponse> listar() {
        return usuarioService.listarRoles();
    }
}