package com.example.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class PersonaController {

    @GetMapping(value = "/api/personas", produces = "text/plain")
    public ResponseEntity<String> entregarPersonas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "desconocido";
        }

        Path path = Paths.get("/mnt/nfs_compartido/operadores.csv");

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        try (Stream<String> lines = Files.lines(path)) {
            long skipLines = (long) page * size;

            List<String> result = lines
                    .skip(skipLines)
                    .limit(size)
                    .collect(Collectors.toList());

            String encabezado = "CONTENEDOR ATENDIENDO: " + hostname + "\n========================================\n";
            String respuesta = encabezado + String.join("\n", result);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header("X-Container-ID", hostname)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=operadores.csv")
                    .body(respuesta);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error leyendo el archivo NFS: " + e.getMessage());
        }
    }
}