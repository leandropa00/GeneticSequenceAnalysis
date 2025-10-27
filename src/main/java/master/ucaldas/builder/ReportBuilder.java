package master.ucaldas.builder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import master.ucaldas.model.AnalysisResult;

public class ReportBuilder {
    private Report report;
    private DateTimeFormatter dateFormatter;

    public ReportBuilder() {
        this.report = new Report();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public ReportBuilder withTitle(String title) {
        report.setTitle(title);
        return this;
    }

    public ReportBuilder addAlignmentSection(AnalysisResult result) {
        if (!"ALIGNMENT".equals(result.getAnalysisType())) {
            return this;
        }

        StringBuilder section = new StringBuilder();
        section.append("┌─ ANÁLISIS DE ALINEAMIENTO ").append("─".repeat(52)).append("\n");
        section.append("│\n");
        section.append(String.format("│  Fecha: %s\n", result.getTimestamp().format(dateFormatter)));
        section.append("│\n");
        section.append(String.format("│  Secuencia 1: %s\n", result.getData("sequence1")));
        section.append(String.format("│  Longitud:    %d bases\n", result.getData("length1")));
        section.append("│\n");
        section.append(String.format("│  Secuencia 2: %s\n", result.getData("sequence2")));
        section.append(String.format("│  Longitud:    %d bases\n", result.getData("length2")));
        section.append("│\n");
        section.append(String.format("│  Similitud:   %.2f%%\n", result.getData("similarity_percentage")));
        section.append(String.format("│  Más larga:   %s\n", result.getData("longer_sequence")));
        section.append("└").append("─".repeat(79));

        report.addSection(section.toString());
        report.addAnalysisResult(result);
        return this;
    }

    public ReportBuilder addMotifDetectionSection(AnalysisResult result) {
        if (!"MOTIF_DETECTION".equals(result.getAnalysisType())) {
            return this;
        }

        StringBuilder section = new StringBuilder();
        section.append("┌─ DETECCIÓN DE MOTIVOS ").append("─".repeat(55)).append("\n");
        section.append("│\n");
        section.append(String.format("│  Fecha: %s\n", result.getTimestamp().format(dateFormatter)));
        section.append("│\n");
        section.append(String.format("│  Secuencia: %s\n", result.getData("sequence_name")));
        section.append(String.format("│  Motivo:    %s\n", result.getData("motif")));
        section.append(String.format("│  Ocurrencias: %d\n", result.getData("occurrences")));
        section.append("│\n");

        @SuppressWarnings("unchecked")
        List<Integer> positions = (List<Integer>) result.getData("positions");
        
        if (positions.isEmpty()) {
            section.append("│  No se encontraron ocurrencias del motivo\n");
        } else {
            section.append("│  Posiciones encontradas:\n");
            section.append("│  ");
            for (int i = 0; i < positions.size(); i++) {
                section.append(positions.get(i));
                if (i < positions.size() - 1) {
                    section.append(", ");
                }
                // Salto de línea cada 10 posiciones
                if ((i + 1) % 10 == 0 && i < positions.size() - 1) {
                    section.append("\n│  ");
                }
            }
            section.append("\n");
        }

        section.append("└").append("─".repeat(79));

        report.addSection(section.toString());
        report.addAnalysisResult(result);
        return this;
    }

    public ReportBuilder addStructurePredictionSection(AnalysisResult result) {
        if (!"STRUCTURE_PREDICTION".equals(result.getAnalysisType())) {
            return this;
        }

        StringBuilder section = new StringBuilder();
        section.append("┌─ PREDICCIÓN DE ESTRUCTURA ").append("─".repeat(51)).append("\n");
        section.append("│\n");
        section.append(String.format("│  Fecha: %s\n", result.getTimestamp().format(dateFormatter)));
        section.append("│\n");
        section.append(String.format("│  Secuencia: %s\n", result.getData("sequence_name")));
        section.append(String.format("│  Longitud:  %d bases\n", result.getData("sequence_length")));
        section.append("│\n");

        @SuppressWarnings("unchecked")
        Map<Character, Integer> baseCounts = (Map<Character, Integer>) result.getData("base_counts");
        
        section.append("│  Composición de bases:\n");
        section.append(String.format("│    A (Adenina):  %d\n", baseCounts.get('A')));
        section.append(String.format("│    T (Timina):   %d\n", baseCounts.get('T')));
        section.append(String.format("│    C (Citosina): %d\n", baseCounts.get('C')));
        section.append(String.format("│    G (Guanina):  %d\n", baseCounts.get('G')));
        section.append("│\n");
        section.append(String.format("│  Estructura predicha: %s\n", result.getData("predicted_structure")));
        section.append("└").append("─".repeat(79));

        report.addSection(section.toString());
        report.addAnalysisResult(result);
        return this;
    }

    public ReportBuilder addCustomSection(String sectionContent) {
        report.addSection(sectionContent);
        return this;
    }

    public Report build() {
        return report;
    }

    public ReportBuilder reset() {
        this.report = new Report();
        return this;
    }
}

