package com.rinconsur.usuario.dto;

public record CambiarContrasenaRequest(
    String actual, 
    String nueva) {
}