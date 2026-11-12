package com.rinconsur.usuario.dto;

public record CrearUsuarioRequest(
    String nombre,
    String correo,
    String contrasena,
    Long rolId) 
    {
}