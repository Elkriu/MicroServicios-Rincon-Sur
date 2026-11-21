package com.rinconsur.usuario.controller;

import com.rinconsur.usuario.dto.RolResponse;
import com.rinconsur.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Roles", description = "Catálogo de roles. Son 3 fijos: 1 Observador de Pasillo, 2 Jefe de Tienda y 3 Administrador")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar roles",
            description = "Devuelve los 3 roles disponibles. Sirve para llenar el selector de rol al crear o editar un usuario.")
    @ApiResponse(responseCode = "200", description = "Lista de roles")
    @GetMapping
    public List<RolResponse> listar() {
        return usuarioService.listarRoles();
    }
}