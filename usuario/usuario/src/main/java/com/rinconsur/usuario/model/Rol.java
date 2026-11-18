package com.rinconsur.usuario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    public static final long ID_OBSERVADOR = 1L;
    public static final long ID_JEFE_TIENDA = 2L;
    public static final long ID_ADMINISTRADOR = 3L;

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;
}