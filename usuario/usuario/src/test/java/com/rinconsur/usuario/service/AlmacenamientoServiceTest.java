package com.rinconsur.usuario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

class AlmacenamientoServiceTest {

    @TempDir
    Path tempDir;

    private AlmacenamientoService almacenamientoService;

    @BeforeEach
    void setUp() {
        almacenamientoService = new AlmacenamientoService(tempDir.toString());
    }

    @Test
    void debeFallarConstructorSiRutaEsUnArchivoYNoCarpeta() throws IOException {
        Path archivoBloqueante = tempDir.resolve("archivo.txt");
        Files.writeString(archivoBloqueante, "contenido");

        assertThrows(IllegalStateException.class, () -> new AlmacenamientoService(archivoBloqueante.toString()));
    }

    @Test
    void debeGuardarImagenExitosamente() throws IOException {
        MockMultipartFile archivo = new MockMultipartFile(
                "file",
                "foto.jpg",
                "image/jpeg",
                "contenido-imagen".getBytes()
        );

        String nombreGuardado = almacenamientoService.guardar(archivo);

        assertNotNull(nombreGuardado);
        assertTrue(nombreGuardado.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve(nombreGuardado)));
    }

    @Test
    void noDebeGuardarSiArchivoEstaVacio() {
        MockMultipartFile archivoVacio = new MockMultipartFile(
                "file",
                "vacio.jpg",
                "image/jpeg",
                new byte[0]
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> almacenamientoService.guardar(archivoVacio));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void noDebeGuardarSiTipoContenidoEsNulo() {
        MockMultipartFile archivoSinTipo = new MockMultipartFile(
                "file",
                "archivo",
                null,
                "datos".getBytes()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> almacenamientoService.guardar(archivoSinTipo));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void noDebeGuardarSiNoEsImagen() {
        MockMultipartFile archivoPdf = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/pdf",
                "datos-pdf".getBytes()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> almacenamientoService.guardar(archivoPdf));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void debeLanzarInternalServerErrorSiFallaLecturaDelStream() throws IOException {
        MultipartFile archivoRoto = mock(MultipartFile.class);
        when(archivoRoto.isEmpty()).thenReturn(false);
        when(archivoRoto.getContentType()).thenReturn("image/png");
        when(archivoRoto.getInputStream()).thenThrow(new IOException("Error de disco simulado"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> almacenamientoService.guardar(archivoRoto));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    void debeLeerImagenExistente() throws IOException {
        byte[] contenidoOriginal = "bytes-de-prueba".getBytes();
        Files.write(tempDir.resolve("test.jpg"), contenidoOriginal);

        byte[] resultado = almacenamientoService.leer("test.jpg");

        assertArrayEquals(contenidoOriginal, resultado);
    }

    @Test
    void debeLanzarNotFoundSiImagenNoExisteAlLeer() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> almacenamientoService.leer("no-existe.jpg"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void debeEliminarImagenExistente() throws IOException {
        Path archivo = tempDir.resolve("borrar.jpg");
        Files.writeString(archivo, "datos");
        assertTrue(Files.exists(archivo));

        almacenamientoService.eliminar("borrar.jpg");

        assertFalse(Files.exists(archivo));
    }

    @Test
    void noDebeFallarAlEliminarSiNombreEsNulo() {
        assertDoesNotThrow(() -> almacenamientoService.eliminar(null));
    }

    @Test
    void noDebeFallarAlEliminarSiArchivoNoExiste() {
        assertDoesNotThrow(() -> almacenamientoService.eliminar("fantasma.jpg"));
    }
}