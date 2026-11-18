package com.rinconsur.visitante.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sesiones")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {

    public static final String EN_CURSO = "EN_CURSO";
    public static final String FINALIZADA = "FINALIZADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 30)
    private String acceso;

    // Mismo formato que usa la app: dd-MM-yyyy
    @Column(nullable = false, length = 10)
    private String fecha;

    @Column(nullable = false, length = 15)
    private String estado;

    // Id del usuario en ms_usuario (sin FK porque es otra base de datos)
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
}