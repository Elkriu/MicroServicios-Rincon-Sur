package com.rinconsur.usuario.dto;

public record RestablecerContrasenaRequest(String correo, String codigo, String nueva) {
}