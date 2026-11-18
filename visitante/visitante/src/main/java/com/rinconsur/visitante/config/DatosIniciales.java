package com.rinconsur.visitante.config;

import com.rinconsur.visitante.model.RegistroConteo;
import com.rinconsur.visitante.model.Sesion;
import com.rinconsur.visitante.repository.RegistroConteoRepository;
import com.rinconsur.visitante.repository.SesionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final SesionRepository sesionRepository;
    private final RegistroConteoRepository registroRepository;

    @Override
    public void run(String... args) {
        if (sesionRepository.count() > 0) {
            return;
        }

        Sesion s1 = sesion("SES-A1-859", "Acceso A1", "01-11-2026", Sesion.FINALIZADA, 1L);
        Sesion s2 = sesion("SES-A2-442", "Acceso A2", "01-11-2026", Sesion.FINALIZADA, 1L);
        Sesion s3 = sesion("SES-A3-001", "Acceso A3", "01-11-2026", Sesion.EN_CURSO, 1L);
        sesionRepository.saveAll(List.of(s1, s2, s3));

        registroRepository.saveAll(List.of(
                registro(s1, "VISITA", "RANGO_26_40", "SOLO", false, null, "10:05:00"),
                registro(s1, "TRANSITO", "RANGO_18_25", "PAREJA", true, "A3", "10:12:00"),
                registro(s1, "VISITA", "RANGO_41_60", "CON_NINOS", true, null, "10:15:00"),
                registro(s1, "VISITA", "MENOR_18", "GRUPO", false, null, "10:20:00"),
                registro(s1, "TRANSITO", "MAYOR_60", "SOLO", false, "A2", "10:25:00"),
                registro(s2, "VISITA", "RANGO_26_40", "PAREJA", true, null, "11:00:00"),
                registro(s2, "VISITA", "RANGO_18_25", "SOLO", false, null, "11:10:00"),
                registro(s2, "TRANSITO", "RANGO_41_60", "SOLO", false, "A1", "11:15:00"),
                registro(s2, "VISITA", "MAYOR_60", "CON_NINOS", false, null, "11:20:00"),
                registro(s3, "VISITA", "RANGO_26_40", "GRUPO", true, null, "12:05:00"),
                registro(s3, "VISITA", "RANGO_18_25", "PAREJA", true, null, "12:12:00"),
                registro(s3, "TRANSITO", "MENOR_18", "SOLO", false, "A1", "12:15:00")
        ));
    }

    private Sesion sesion(String codigo, String acceso, String fecha, String estado, Long usuarioId) {
        return Sesion.builder()
                .codigo(codigo).acceso(acceso).fecha(fecha).estado(estado).usuarioId(usuarioId)
                .build();
    }

    private RegistroConteo registro(Sesion sesion, String clasificacion, String rango, String compania,
                                    boolean bolsas, String destino, String hora) {
        return RegistroConteo.builder()
                .sesion(sesion).clasificacion(clasificacion).rangoEtario(rango).compania(compania)
                .bolsas(bolsas).destino(destino).hora(hora)
                .build();
    }
}