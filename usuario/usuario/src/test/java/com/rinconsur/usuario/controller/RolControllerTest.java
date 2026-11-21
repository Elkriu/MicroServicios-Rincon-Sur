package com.rinconsur.usuario.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rinconsur.usuario.dto.RolResponse;
import com.rinconsur.usuario.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private RolController rolController;

    @Test
    void debeListarRoles() {
        RolResponse rol = new RolResponse(1L, "ADMIN");
        when(usuarioService.listarRoles()).thenReturn(List.of(rol));

        List<RolResponse> resultado = rolController.listar();

        assertEquals(1, resultado.size());
        assertEquals("ADMIN", resultado.get(0).nombre());
        verify(usuarioService, times(1)).listarRoles();
    }
}