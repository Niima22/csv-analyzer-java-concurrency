package com.csv.csvprocessor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CsvProcessingService {

    private final ExecutorService executor = Executors.newFixedThreadPool(4); // pool de 4 threads

    public void processCsv(MultipartFile file) {
        try (Scanner scanner = new Scanner(file.getInputStream())) {

            String headerLine = scanner.hasNextLine() ? scanner.nextLine() : null;

            if (headerLine == null) {
                System.out.println("Fichier vide ou en-tête manquant.");
                return;
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                final String currentLine = line;
                executor.submit(() -> traiterLigne(headerLine, currentLine));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode générique pour lire n'importe quelle ligne CSV avec en-têtes
    private void traiterLigne(String headerLine, String line) {
        String[] headers = headerLine.split(";");
        String[] valeurs = line.split(";");

        if (valeurs.length != headers.length) {
            System.out.println("Ligne ignorée (colonnes non conformes) : " + line);
            return;
        }

        System.out.println("Ligne analysée par " + Thread.currentThread().getName() + " :");
        for (int i = 0; i < headers.length; i++) {
            System.out.println(" - " + headers[i].trim() + " : " + valeurs[i].trim());
        }
        System.out.println("-----");
    }

    public void shutdown() {
        executor.shutdown();
    }
}
