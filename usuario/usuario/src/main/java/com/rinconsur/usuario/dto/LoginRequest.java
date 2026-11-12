package com.rinconsur.usuario.dto;

public record LoginRequest(
    String correo,
    String contrasena) {
}