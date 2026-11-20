package com.rinconsur.administracion.config;

import com.rinconsur.administracion.model.Acceso;
import com.rinconsur.administracion.repository.AccesoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final AccesoRepository accesoRepository;

    @Override
    public void run(String... args) {
        if (accesoRepository.count() == 0) {
            accesoRepository.saveAll(List.of(
                    acceso("Acceso A1"),
                    acceso("Acceso A2"),
                    acceso("Acceso A3"),
                    acceso("Acceso A4")
            ));
        }
    }

    private Acceso acceso(String nombre) {
        return Acceso.builder()
                .nombre(nombre)
                .build();
    }
}