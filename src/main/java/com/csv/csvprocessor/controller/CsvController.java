package com.csv.csvprocessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    // 🔹 Détecte si le fichier utilise ";" ou ","
    private String detectSeparator(String headerLine) {
        return headerLine.contains(";") ? ";" : ",";
    }

    // 🔹 Retourne les en-têtes de colonnes
    @PostMapping("/headers")
    public ResponseEntity<List<String>> getHeaders(@RequestParam("file") MultipartFile file) {
        try (Scanner scanner = new Scanner(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            if (scanner.hasNextLine()) {
                String headerLine = scanner.nextLine();
                String separator = detectSeparator(headerLine);
                String[] headers = headerLine.split(separator);
                return ResponseEntity.ok(Arrays.stream(headers)
                        .map(String::trim)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 🔹 Applique un filtre dynamique (ou aucun) + retourne les données sélectionnées
    @PostMapping("/filter")
    public ResponseEntity<List<Map<String, String>>> filterCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("filter") MultipartFile filterJson
    ) {
        try {
            Scanner jsonScanner = new Scanner(filterJson.getInputStream()).useDelimiter("\\A");
            String json = jsonScanner.hasNext() ? jsonScanner.next() : "{}";

            Map<String, Object> filter = new ObjectMapper().readValue(json, Map.class);

            String filterColumn = (String) filter.get("column");
            String condition = (String) filter.get("condition");
            String value = filter.get("value") != null ? ((String) filter.get("value")).toLowerCase() : null;
            int limit = (int) filter.get("limit");
            List<String> columns = (List<String>) filter.get("columns");

            Scanner scanner = new Scanner(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            String headerLine = scanner.hasNextLine() ? scanner.nextLine() : null;
            if (headerLine == null) return ResponseEntity.ok(List.of());

            String separator = detectSeparator(headerLine);
            String[] headers = headerLine.split(separator);
            List<Map<String, String>> result = new ArrayList<>();

            while (scanner.hasNextLine() && result.size() < limit) {
                String[] values = scanner.nextLine().split(separator);
                if (values.length != headers.length) continue;

                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }

                boolean keep = true;
                if (filterColumn != null && condition != null && value != null) {
                    String target = row.get(filterColumn);
                    if (target == null) continue;
                    target = target.toLowerCase();

                    keep = switch (condition) {
                        case "contains" -> target.contains(value);
                        case "startsWith" -> target.startsWith(value);
                        case "greaterThan" -> {
                            try {
                                yield Double.parseDouble(target) > Double.parseDouble(value);
                            } catch (NumberFormatException e) {
                                yield false;
                            }
                        }
                        default -> false;
                    };
                }

                if (keep) {
                    Map<String, String> filteredRow = new LinkedHashMap<>();
                    for (String col : columns) {
                        filteredRow.put(col, row.get(col));
                    }
                    result.add(filteredRow);
                }
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
