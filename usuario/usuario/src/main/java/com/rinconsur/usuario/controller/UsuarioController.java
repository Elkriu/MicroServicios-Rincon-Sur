package com.rinconsur.usuario.controller;

import com.rinconsur.usuario.dto.CambiarEstadoRequest;
import com.rinconsur.usuario.dto.CambiarRolRequest;
import com.rinconsur.usuario.dto.CambiarContrasenaRequest;
import com.rinconsur.usuario.dto.ActualizarPerfilRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;
import com.rinconsur.usuario.dto.LoginRequest;
import com.rinconsur.usuario.dto.CrearUsuarioRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.rinconsur.usuario.dto.UsuarioResponse;
import com.rinconsur.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios, login, perfil con foto y cambio de clave")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios",
            description = "Devuelve los usuarios ordenados por nombre. Los tres filtros son opcionales y se pueden combinar. "
                    + "Ejemplo: observadores activos = ?rolId=1&activo=true")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios (puede venir vacía)")
    @GetMapping
    public List<UsuarioResponse> listar(
            @Parameter(description = "Texto a buscar en el nombre o en el correo, sin distinguir mayúsculas", example = "tamara")
            @RequestParam(required = false) String buscar,
            @Parameter(description = "Id del rol: 1 Observador de Pasillo, 2 Jefe de Tienda, 3 Administrador", example = "1")
            @RequestParam(required = false) Long rolId,
            @Parameter(description = "true = solo cuentas activas, false = solo cuentas suspendidas", example = "true")
            @RequestParam(required = false) Boolean activo) {
        return usuarioService.listar(buscar, rolId, activo);
    }

    @Operation(summary = "Obtener un usuario por id")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{id}")
    public UsuarioResponse obtener(
            @Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @Operation(summary = "Crear un usuario",
            description = "Valida nombre (solo letras y espacios), correo (@rinconsur.cl) y clave (8 caracteres con "
                    + "mayúscula, minúscula y símbolo). La clave se guarda con BCrypt y nunca se devuelve. "
                    + "Si no se indica rol, queda como Observador de Pasillo.")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Nombre, correo o clave inválidos, o el rol indicado no existe")
    @ApiResponse(responseCode = "409", description = "Ese correo ya está registrado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@RequestBody CrearUsuarioRequest datos) {
        return usuarioService.crear(datos);
    }

    @PostMapping("/login")
    public UsuarioResponse iniciarSesion(@RequestBody LoginRequest datos) {
        return usuarioService.iniciarSesion(datos);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizarPerfil(@PathVariable Long id, @RequestBody ActualizarPerfilRequest datos) {
        return usuarioService.actualizarPerfil(id, datos);
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UsuarioResponse subirFoto(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        return usuarioService.subirFoto(id, archivo);
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(usuarioService.obtenerFoto(id));
    }

    @PutMapping("/{id}/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarContrasena(@PathVariable Long id, @RequestBody CambiarContrasenaRequest datos) {
        usuarioService.cambiarContrasena(id, datos);
    }

    @Operation(summary = "Cambiar el rol de un usuario")
    @ApiResponse(responseCode = "200", description = "Rol actualizado")
    @ApiResponse(responseCode = "400", description = "No se indicó el rol o el rol no existe")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PutMapping("/{id}/rol")
    public UsuarioResponse cambiarRol(
            @Parameter(description = "Id del usuario", example = "1") @PathVariable Long id,
            @RequestBody CambiarRolRequest datos) {
        return usuarioService.cambiarRol(id, datos);
    }

    @Operation(summary = "Suspender o reactivar una cuenta",
            description = "Con activo=false la cuenta queda suspendida: no puede iniciar sesión ni recuperar su clave. "
                    + "Con activo=true se reactiva.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "400", description = "No se indicó el campo activo")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PutMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(
            @Parameter(description = "Id del usuario", example = "1") @PathVariable Long id,
            @RequestBody CambiarEstadoRequest datos) {
        return usuarioService.cambiarEstado(id, datos);
    }
}