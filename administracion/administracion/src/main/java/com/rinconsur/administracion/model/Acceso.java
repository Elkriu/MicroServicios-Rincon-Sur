package com.rinconsur.administracion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Puerta de la tienda. El nombre ("Acceso A1") es la clave. */
@Entity
@Table(name = "accesos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acceso {

    @Id
    @Column(length = 30)
    private String nombre;

    @Column(nullable = false)
    @Builder.Default
    private boolean disponible = true;
}
