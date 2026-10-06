package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

//@Component
public class GeneradorCsvRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        File csv = new File("/mnt/nfs_compartido/operadores.csv");

        // Solo lo genera si el archivo no existe en NFS
        if (!csv.exists()) {
            System.out.println("Iniciando generación de 10M de registros en NFS...");
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csv), 8192 * 10)) {
                writer.write("id,nombre,cargo,estado\n");
                
                for (int i = 1; i <= 10000000; i++) {
                    writer.write(i + ",Usuario_" + i + ",Operador,Activo\n");
                }
            }
            
            System.out.println("Archivo CSV generado exitosamente.");
        } else {
            System.out.println("El archivo CSV ya existe. Se omitió la generación.");
        }
    }
}