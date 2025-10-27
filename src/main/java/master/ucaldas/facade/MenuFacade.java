package master.ucaldas.facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import master.ucaldas.builder.Report;
import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;

public class MenuFacade {
    private final GeneticAnalysisFacade analysisFacade;
    private final Scanner scanner;

    public MenuFacade() {
        this.analysisFacade = new GeneticAnalysisFacade();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printWelcome();

        if (!analysisFacade.isDatabaseConnected()) {
            System.err.println("\nERROR: No se pudo conectar a la base de datos.");
            System.err.println("  Asegúrese de que MySQL esté ejecutándose.");
            return;
        }

        runMainLoop();
        cleanup();
    }

    private void runMainLoop() {
        boolean running = true;
        while (running) {
            try {
                printMainMenu();
                int choice = readInt("Seleccione una opción: ");

                switch (choice) {
                    case 1 -> menuLoadSequences();
                    case 2 -> menuViewSequences();
                    case 3 -> menuPerformAnalysis();
                    case 4 -> menuGenerateReports();
                    case 5 -> menuConfiguration();
                    case 6 -> menuClearCache();
                    case 0 -> {
                        running = false;
                        System.out.println("\nCerrando aplicación...");
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                System.err.println("\nError: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void cleanup() {
        analysisFacade.closeDatabaseConnection();
        scanner.close();
    }

    // ========== UI DISPLAY METHODS ==========

    private void printWelcome() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("  SISTEMA DE ANÁLISIS DE SECUENCIAS GENÉTICAS");
        System.out.println("  Universidad de Caldas - Maestría en Ingeniería Computacional");
        System.out.println("═".repeat(80));
    }

    private void printMainMenu() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("MENÚ PRINCIPAL");
        System.out.println("─".repeat(80));
        System.out.println("1. Cargar secuencias desde FASTA");
        System.out.println("2. Ver secuencias almacenadas");
        System.out.println("3. Realizar análisis");
        System.out.println("4. Generar reportes");
        System.out.println("5. Configuración");
        System.out.println("6. Limpiar caché de análisis");
        System.out.println("0. Salir");
        System.out.println("─".repeat(80));
    }

    // ========== MENÚ 1: LOAD SEQUENCES FROM FASTA ==========

    private void menuLoadSequences() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("CARGAR SECUENCIAS DESDE FASTA");
        System.out.println("─".repeat(80));

        String defaultPath = analysisFacade.getFastaPath();
        System.out.println("Ruta por defecto: " + defaultPath);

        String filePath;
        System.out.print("¿Usar ruta por defecto? (S/N): ");
        if (scanner.nextLine().trim().toUpperCase().equals("S")) {
            filePath = defaultPath;
        } else {
            System.out.print("Nueva ruta FASTA: ");
            filePath = scanner.nextLine().trim();
        }

        System.out.println("\nCargando secuencias...");
        int count = analysisFacade.loadSequencesFromFASTA(filePath);

        if (count > 0) {
            System.out.println("Se cargaron " + count + " secuencias exitosamente");
            System.out.println("Total de secuencias en BD: " + analysisFacade.countSequences());
        } else {
            System.out.println("No se pudieron cargar secuencias");
        }
    }

    // ========== MENÚ 2: VIEW SEQUENCES ==========

    private void menuViewSequences() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("VER SECUENCIAS ALMACENADAS");
        System.out.println("─".repeat(80));
        System.out.println("1. Ver todas las secuencias");
        System.out.println("2. Filtrar por tipo (DNA/RNA/Protein)");
        System.out.println("3. Buscar por nombre");
        System.out.println("0. Volver");
        System.out.println("─".repeat(80));

        int choice = readInt("Seleccione una opción: ");

        switch (choice) {
            case 1 -> viewAllSequences();
            case 2 -> viewSequencesByType();
            case 3 -> viewSequenceByName();
            case 0 -> {}
            default -> System.out.println("Opción inválida");
        }
    }

    private void viewAllSequences() {
        List<GeneticSequence> sequences = analysisFacade.getAllSequences();
        displaySequences(sequences);
    }

    private void viewSequencesByType() {
        System.out.print("Ingrese el tipo (DNA/RNA/Protein): ");
        String type = scanner.nextLine().trim();

        List<GeneticSequence> sequences = analysisFacade.getSequencesByType(type);
        displaySequences(sequences);
    }

    private void viewSequenceByName() {
        System.out.print("Ingrese el nombre de la secuencia: ");
        String name = scanner.nextLine().trim();

        GeneticSequence seq = analysisFacade.getSequenceByName(name);
        if (seq != null) {
            List<GeneticSequence> sequences = new ArrayList<>();
            sequences.add(seq);
            displaySequences(sequences);
        } else {
            System.out.println("Secuencia no encontrada");
        }
    }

    private void displaySequences(List<GeneticSequence> sequences) {
        if (sequences.isEmpty()) {
            System.out.println("\nNo se encontraron secuencias");
            return;
        }

        System.out.println("\n" + "═".repeat(80));
        System.out.printf("%-10s %-15s %-10s %-10s %-30s\n", "ID", "NOMBRE", "TIPO", "LONGITUD", "DESCRIPCIÓN");
        System.out.println("═".repeat(80));

        for (GeneticSequence seq : sequences) {
            String desc = seq.getDescription();
            if (desc.length() > 28) {
                desc = desc.substring(0, 25) + "...";
            }
            System.out.printf("%-10d %-15s %-10s %-10d %-30s\n",
                    seq.getId(),
                    seq.getName(),
                    seq.getType(),
                    seq.getSequence().length(),
                    desc);
        }

        System.out.println("═".repeat(80));
        System.out.println("Total: " + sequences.size() + " secuencias");
    }

    // ========== MENÚ 3: PERFORM ANALYSIS ==========

    private void menuPerformAnalysis() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("REALIZAR ANÁLISIS");
        System.out.println("─".repeat(80));
        System.out.println("1. Alineamiento de secuencias");
        System.out.println("2. Detección de motivos");
        System.out.println("3. Predicción de estructura");
        System.out.println("0. Volver");
        System.out.println("─".repeat(80));

        int choice = readInt("Seleccione una opción: ");

        try {
            switch (choice) {
                case 1 -> performAlignmentAnalysis();
                case 2 -> performMotifDetectionAnalysis();
                case 3 -> performStructurePredictionAnalysis();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } catch (Exception e) {
            System.err.println("Error en el análisis: " + e.getMessage());
        }
    }

    private void performAlignmentAnalysis() {
        System.out.println("\n--- ALINEAMIENTO DE SECUENCIAS ---");
        System.out.print("Nombre de la primera secuencia: ");
        String seq1 = scanner.nextLine().trim();
        System.out.print("Nombre de la segunda secuencia: ");
        String seq2 = scanner.nextLine().trim();

        AnalysisResult result = analysisFacade.performAlignment(seq1, seq2);
        analysisFacade.addSessionResult(result);

        System.out.println("\nAnálisis completado");
        System.out.println("  Similitud: " + String.format("%.2f%%", result.getData("similarity_percentage")));
        System.out.println("  Secuencia más larga: " + result.getData("longer_sequence"));

        System.out.print("\n¿Desea ver el reporte completo? (S/N): ");
        if (scanner.nextLine().trim().toUpperCase().equals("S")) {
            Report report = analysisFacade.generateReport(result, "ANÁLISIS DE ALINEAMIENTO");
            System.out.println("\n" + report);
        }
    }

    private void performMotifDetectionAnalysis() {
        System.out.println("\n--- DETECCIÓN DE MOTIVOS ---");
        System.out.print("Nombre de la secuencia: ");
        String seqName = scanner.nextLine().trim();
        System.out.print("Motivo a buscar: ");
        String motif = scanner.nextLine().trim();

        AnalysisResult result = analysisFacade.performMotifDetection(seqName, motif);
        analysisFacade.addSessionResult(result);

        System.out.println("\nAnálisis completado");
        System.out.println("  Ocurrencias: " + result.getData("occurrences"));

        System.out.print("\n¿Desea ver el reporte completo? (S/N): ");
        if (scanner.nextLine().trim().toUpperCase().equals("S")) {
            Report report = analysisFacade.generateReport(result, "DETECCIÓN DE MOTIVOS");
            System.out.println("\n" + report);
        }
    }

    private void performStructurePredictionAnalysis() {
        System.out.println("\n--- PREDICCIÓN DE ESTRUCTURA ---");
        System.out.print("Nombre de la secuencia: ");
        String seqName = scanner.nextLine().trim();

        AnalysisResult result = analysisFacade.performStructurePrediction(seqName);
        analysisFacade.addSessionResult(result);

        System.out.println("\nAnálisis completado");
        System.out.println("  Estructura predicha: " + result.getData("predicted_structure"));

        System.out.print("\n¿Desea ver el reporte completo? (S/N): ");
        if (scanner.nextLine().trim().toUpperCase().equals("S")) {
            Report report = analysisFacade.generateReport(result, "PREDICCIÓN DE ESTRUCTURA");
            System.out.println("\n" + report);
        }
    }

    // ========== MENÚ 4: GENERATE REPORTS ==========

    private void menuGenerateReports() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("GENERAR REPORTES");
        System.out.println("─".repeat(80));
        System.out.println("1. Reporte de análisis de esta sesión");
        System.out.println("2. Reporte de todos los análisis en caché");
        System.out.println("0. Volver");
        System.out.println("─".repeat(80));

        int choice = readInt("Seleccione una opción: ");

        Report report;

        switch (choice) {
            case 1 -> {
                if (analysisFacade.getSessionResultsCount() == 0) {
                    System.out.println("No hay análisis en esta sesión");
                    return;
                }
                report = analysisFacade.generateMultiAnalysisReport(analysisFacade.getSessionResults(),
                        "REPORTE DE ANÁLISIS GENÉTICOS - SESIÓN ACTUAL");
            }
            case 2 -> {
                if (analysisFacade.getCachedAnalysisCount() == 0) {
                    System.out.println("No hay análisis en caché");
                    return;
                }
                report = analysisFacade.generateCachedAnalysisReport();
            }
            case 0 -> {
                return;
            }
            default -> {
                System.out.println("Opción inválida");
                return;
            }
        }

        if (report != null) {
            System.out.println("\n" + report);

            System.out.print("\n¿Desea exportar el reporte a un archivo? (S/N): ");
            if (scanner.nextLine().trim().toUpperCase().equals("S")) {
                handleReportExport(report);
            }
        }
    }

    private void handleReportExport(Report report) {
        System.out.println("\nSeleccione el formato de exportación:");
        System.out.println("1. Texto plano (TXT)");
        System.out.println("2. CSV (Comma Separated Values)");
        System.out.print("Opción: ");
        
        int formatChoice = readInt("");
        String extension;
        String content;
        
        if (formatChoice == 2) {
            extension = ".csv";
            content = handleCSVExport(report);
        } else {
            extension = ".txt";
            content = report.export();
        }
        
        System.out.print("Nombre del archivo (sin extensión): ");
        String filename = scanner.nextLine().trim();
        try {
            analysisFacade.exportReport(content, "reports/" + filename + extension);
            System.out.println("Reporte exportado a: reports/" + filename + extension);
        } catch (IOException e) {
            System.err.println("Error exportando reporte: " + e.getMessage());
        }
    }

    private String handleCSVExport(Report report) {
        List<String> availableTypes = report.getAvailableAnalysisTypes();
        
        if (availableTypes.isEmpty()) {
            System.out.println("No hay datos de análisis para exportar.");
            return "";
        }
        
        if (availableTypes.size() > 1) {
            System.out.println("\nTipos de análisis disponibles en el reporte:");
            int index = 1;
            for (String type : availableTypes) {
                String typeName = analysisFacade.getAnalysisTypeName(type);
                int count = report.countByType(type);
                System.out.println(index + ". " + typeName + " (" + count + " registros)");
                index++;
            }
            System.out.println(index + ". Exportar todos (formato genérico)");
            
            System.out.print("\nSeleccione el tipo de análisis a exportar: ");
            int typeChoice = readInt("");
            
            if (typeChoice > 0 && typeChoice <= availableTypes.size()) {
                String selectedType = availableTypes.get(typeChoice - 1);
                return report.exportToCSV(selectedType);
            } else {
                return report.exportToCSV("ALL");
            }
        } else {
            String selectedType = availableTypes.get(0);
            System.out.println("Exportando " + analysisFacade.getAnalysisTypeName(selectedType));
            return report.exportToCSV(selectedType);
        }
    }

    // ========== MENÚ 5: CONFIGURATION ==========

    private void menuConfiguration() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("CONFIGURACIÓN");
        System.out.println("─".repeat(80));

        System.out.println("Configuración actual:");
        System.out.println("  Ruta FASTA: " + analysisFacade.getFastaPath());
        System.out.println("  Longitud mínima: " + analysisFacade.getMinSequenceLength() + " bases");
        System.out.println("  Análisis en caché: " + analysisFacade.getCachedAnalysisCount());
        System.out.println("  Secuencias en BD: " + analysisFacade.countSequences());

        System.out.println("\n1. Cambiar ruta FASTA por defecto");
        System.out.println("2. Cambiar longitud mínima");
        System.out.println("0. Volver");

        int choice = readInt("Seleccione una opción: ");

        switch (choice) {
            case 1 -> {
                System.out.print("Nueva ruta FASTA: ");
                String path = scanner.nextLine().trim();
                analysisFacade.setFastaPath(path);
                System.out.println("Ruta actualizada");
            }
            case 2 -> {
                int minLength = readInt("Nueva longitud mínima: ");
                analysisFacade.setMinSequenceLength(minLength);
                System.out.println("Longitud mínima actualizada");
            }
            case 0 -> {}
            default -> System.out.println("Opción inválida");
        }
    }

    // ========== MENÚ 6: CLEAR CACHE ==========

    private void menuClearCache() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("LIMPIAR CACHÉ");
        System.out.println("─".repeat(80));
        System.out.println("Análisis en caché: " + analysisFacade.getCachedAnalysisCount());
        System.out.print("¿Está seguro de limpiar el caché? (S/N): ");

        if (scanner.nextLine().trim().toUpperCase().equals("S")) {
            analysisFacade.clearCache();
            System.out.println("Caché limpiada");
        } else {
            System.out.println("Operación cancelada");
        }
    }

    // ========== UTILITIES ==========

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            }
        }
    }
}

