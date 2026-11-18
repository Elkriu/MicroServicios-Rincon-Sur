package com.rinconsur.visitante.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registros_conteo")
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroConteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;

    @Column(nullable = false, length = 20)
    private String clasificacion;

    @Column(name = "rango_etario", nullable = false, length = 20)
    private String rangoEtario;

    @Column(nullable = false, length = 20)
    private String compania;

    @Column(nullable = false)
    private boolean bolsas;

    @Column(length = 10)
    private String destino;

    @Column(nullable = false, length = 8)
    private String hora;
}