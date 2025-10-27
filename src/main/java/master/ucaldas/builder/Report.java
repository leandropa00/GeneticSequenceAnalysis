package master.ucaldas.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import master.ucaldas.model.AnalysisResult;

public class Report {
    private String title;
    private List<String> sections;
    private List<AnalysisResult> analysisResults;

    public Report() {
        this.sections = new ArrayList<>();
        this.analysisResults = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addSection(String section) {
        this.sections.add(section);
    }

    public void addAnalysisResult(AnalysisResult result) {
        this.analysisResults.add(result);
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSections() {
        return new ArrayList<>(sections);
    }

    public List<AnalysisResult> getAnalysisResults() {
        return new ArrayList<>(analysisResults);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(80)).append("\n");
        sb.append(title).append("\n");
        sb.append("═".repeat(80)).append("\n\n");

        for (String section : sections) {
            sb.append(section).append("\n");
        }

        sb.append("═".repeat(80)).append("\n");
        return sb.toString();
    }

    public String export() {
        return toString();
    }

    public String exportToCSV(String filterType) {
        if (analysisResults.isEmpty()) {
            return "No hay datos para exportar";
        }

        StringBuilder csv = new StringBuilder();
        
        List<AnalysisResult> filteredResults = analysisResults;
        if (!filterType.equals("ALL")) {
            filteredResults = new ArrayList<>();
            for (AnalysisResult result : analysisResults) {
                if (result.getAnalysisType().equals(filterType)) {
                    filteredResults.add(result);
                }
            }
            
            if (filteredResults.isEmpty()) {
                return "No hay datos del tipo especificado para exportar";
            }
        }
        
        switch (filterType) {
            case "ALIGNMENT" -> {
                csv.append("Tipo,Fecha,Secuencia1,Longitud1,Secuencia2,Longitud2,Similitud(%),SecuenciaMasLarga\n");
                for (AnalysisResult result : filteredResults) {
                    if (!"ALIGNMENT".equals(result.getAnalysisType())) continue;
                    csv.append(escapeCSV(result.getAnalysisType())).append(",");
                    csv.append(escapeCSV(result.getTimestamp().toString())).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("sequence1")))).append(",");
                    csv.append(result.getData("length1")).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("sequence2")))).append(",");
                    csv.append(result.getData("length2")).append(",");
                    csv.append(String.format("%.2f", result.getData("similarity_percentage"))).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("longer_sequence")))).append("\n");
                }
            }
            case "MOTIF_DETECTION" -> {
                csv.append("Tipo,Fecha,Secuencia,Motivo,Ocurrencias,Posiciones\n");
                for (AnalysisResult result : filteredResults) {
                    if (!"MOTIF_DETECTION".equals(result.getAnalysisType())) continue;
                    csv.append(escapeCSV(result.getAnalysisType())).append(",");
                    csv.append(escapeCSV(result.getTimestamp().toString())).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("sequence_name")))).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("motif")))).append(",");
                    csv.append(result.getData("occurrences")).append(",");

                    @SuppressWarnings("unchecked")
                    List<Integer> positions = (List<Integer>) result.getData("positions");
                    csv.append(escapeCSV(positions.toString())).append("\n");
                }
            }
            case "STRUCTURE_PREDICTION" -> {
                csv.append("Tipo,Fecha,Secuencia,Longitud,A,T,C,G,EstructuraPredicha\n");
                for (AnalysisResult result : filteredResults) {
                    if (!"STRUCTURE_PREDICTION".equals(result.getAnalysisType())) continue;
                    csv.append(escapeCSV(result.getAnalysisType())).append(",");
                    csv.append(escapeCSV(result.getTimestamp().toString())).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("sequence_name")))).append(",");
                    csv.append(result.getData("sequence_length")).append(",");

                    @SuppressWarnings("unchecked")
                    Map<Character, Integer> baseCounts = (Map<Character, Integer>) result.getData("base_counts");
                    csv.append(baseCounts.get('A')).append(",");
                    csv.append(baseCounts.get('T')).append(",");
                    csv.append(baseCounts.get('C')).append(",");
                    csv.append(baseCounts.get('G')).append(",");
                    csv.append(escapeCSV(String.valueOf(result.getData("predicted_structure")))).append("\n");
                }
            }
            case "ALL" -> {
                csv.append("Tipo,Fecha,Datos\n");
                for (AnalysisResult result : analysisResults) {
                    csv.append(escapeCSV(result.getAnalysisType())).append(",");
                    csv.append(escapeCSV(result.getTimestamp().toString())).append(",");
                    csv.append(escapeCSV(result.getAllData().toString())).append("\n");
                }
            }
        }
        
        return csv.toString();
    }
    
    public List<String> getAvailableAnalysisTypes() {
        List<String> types = new ArrayList<>();
        for (AnalysisResult result : analysisResults) {
            String type = result.getAnalysisType();
            if (!types.contains(type)) {
                types.add(type);
            }
        }
        return types;
    }
    
    public int countByType(String type) {
        int count = 0;
        for (AnalysisResult result : analysisResults) {
            if (result.getAnalysisType().equals(type)) {
                count++;
            }
        }
        return count;
    }

    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}