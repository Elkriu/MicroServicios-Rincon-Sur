package com.rinconsur.dashboard.service;

import com.rinconsur.dashboard.client.VisitanteClient;
import com.rinconsur.dashboard.dto.DistribucionResponse;
import com.rinconsur.dashboard.dto.ItemDistribucionResponse;
import com.rinconsur.dashboard.dto.RegistroDetalleResponse;
import com.rinconsur.dashboard.dto.ResumenDiarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final Pattern FORMATO_FECHA = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");

    private static final Comparator<ItemDistribucionResponse> POR_CANTIDAD =
            Comparator.comparingLong(ItemDistribucionResponse::cantidad).reversed()
                    .thenComparing(ItemDistribucionResponse::etiqueta);

    private static final Comparator<ItemDistribucionResponse> POR_ETIQUETA =
            Comparator.comparing(ItemDistribucionResponse::etiqueta);

    private final VisitanteClient visitanteClient;

    public List<String> fechas() {
        return visitanteClient.listarFechas();
    }

    public ResumenDiarioResponse resumen(String fecha) {
        validarFecha(fecha, true);
        List<RegistroDetalleResponse> registros = visitanteClient.listarRegistros(fecha, null);

        long sesiones = registros.stream()
                .map(RegistroDetalleResponse::sesionId)
                .distinct()
                .count();

        return new ResumenDiarioResponse(
                fecha,
                registros.size(),
                sesiones,
                agrupar(registros, RegistroDetalleResponse::acceso, POR_ETIQUETA)
        );
    }

    public DistribucionResponse distribucion(String fecha, String acceso) {
        validarFecha(fecha, false);
        String accesoFiltro = (acceso == null || acceso.isBlank()) ? null : acceso.trim();
        String fechaFiltro = (fecha == null || fecha.isBlank()) ? null : fecha.trim();

        List<RegistroDetalleResponse> registros = visitanteClient.listarRegistros(fechaFiltro, accesoFiltro);

        return new DistribucionResponse(
                fechaFiltro,
                accesoFiltro,
                registros.size(),
                agrupar(registros, RegistroDetalleResponse::clasificacion, POR_CANTIDAD),
                agrupar(registros, RegistroDetalleResponse::rangoEtario, POR_CANTIDAD),
                agrupar(registros, RegistroDetalleResponse::compania, POR_CANTIDAD),
                agrupar(registros, r -> r.bolsas() ? "Con bolsas" : "Sin bolsas", POR_CANTIDAD)
        );
    }

    public List<RegistroDetalleResponse> registros(String fecha, String acceso) {
        validarFecha(fecha, false);
        String accesoFiltro = (acceso == null || acceso.isBlank()) ? null : acceso.trim();
        String fechaFiltro = (fecha == null || fecha.isBlank()) ? null : fecha.trim();
        return visitanteClient.listarRegistros(fechaFiltro, accesoFiltro);
    }

    private List<ItemDistribucionResponse> agrupar(List<RegistroDetalleResponse> registros,
                                                   Function<RegistroDetalleResponse, String> campo,
                                                   Comparator<ItemDistribucionResponse> orden) {
        return registros.stream()
                .collect(Collectors.groupingBy(campo, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new ItemDistribucionResponse(e.getKey(), e.getValue()))
                .sorted(orden)
                .toList();
    }

    private void validarFecha(String fecha, boolean obligatoria) {
        boolean vacia = fecha == null || fecha.isBlank();
        if (vacia && obligatoria) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha es obligatoria");
        }
        if (!vacia && !FORMATO_FECHA.matcher(fecha.trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha debe tener el formato dd-MM-yyyy");
        }
    }
}