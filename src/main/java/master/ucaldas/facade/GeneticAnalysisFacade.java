package master.ucaldas.facade;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import master.ucaldas.builder.Report;
import master.ucaldas.builder.ReportBuilder;
import master.ucaldas.dao.SequenceDAO;
import master.ucaldas.factory.AnalysisFactory;
import master.ucaldas.factory.AnalysisType;
import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;
import master.ucaldas.proxy.AnalysisProxy;
import master.ucaldas.singleton.AnalysisResultStorage;
import master.ucaldas.singleton.Configuration;
import master.ucaldas.singleton.DatabaseConnection;
import master.ucaldas.strategy.IAnalysisStrategy;
import master.ucaldas.util.FASTAReader;

public class GeneticAnalysisFacade {

    private final SequenceDAO sequenceDAO;
    private final Configuration config;
    private final AnalysisResultStorage storage;
    private final AnalysisFactory analysisFactory;
    private final List<AnalysisResult> sessionResults;

    public GeneticAnalysisFacade() {
        this.sequenceDAO = new SequenceDAO();
        this.config = Configuration.getInstance();
        this.storage = AnalysisResultStorage.getInstance();
        this.analysisFactory = new AnalysisFactory();
        this.sessionResults = new ArrayList<>();
    }

    // ========== SEQUENCES MANAGEMENT ==========
    public int loadSequencesFromFASTA(String filePath) {
        try {
            List<GeneticSequence> sequences = FASTAReader.readFASTA(filePath);
            int count = 0;

            for (GeneticSequence seq : sequences) {
                if (sequenceDAO.insert(seq)) {
                    count++;
                }
            }

            System.out.println("Cargadas " + count + " secuencias desde " + filePath);
            return count;
        } catch (IOException e) {
            System.err.println("Error leyendo archivo FASTA: " + e.getMessage());
            return 0;
        }
    }

    public List<GeneticSequence> getAllSequences() {
        return sequenceDAO.findAll();
    }

    public GeneticSequence getSequenceByName(String name) {
        return sequenceDAO.findByName(name);
    }

    public List<GeneticSequence> getSequencesByType(String type) {
        return sequenceDAO.findByType(type);
    }

    public int countSequences() {
        return sequenceDAO.count();
    }

    public boolean clearAllSequences() {
        return sequenceDAO.deleteAll();
    }

    // ========== ANALYSIS ==========
    public AnalysisResult performAlignment(String seq1Name, String seq2Name) {
        GeneticSequence seq1 = sequenceDAO.findByName(seq1Name);
        GeneticSequence seq2 = sequenceDAO.findByName(seq2Name);

        if (seq1 == null || seq2 == null) {
            throw new IllegalArgumentException("Una o ambas secuencias no fueron encontradas");
        }

        IAnalysisStrategy analysis = analysisFactory.createAnalysis(AnalysisType.ALIGNMENT);
        AnalysisProxy proxy = new AnalysisProxy(analysis);

        return proxy.execute(seq1, seq2);
    }

    public AnalysisResult performMotifDetection(String seqName, String motif) {
        GeneticSequence sequence = sequenceDAO.findByName(seqName);

        if (sequence == null) {
            throw new IllegalArgumentException("Secuencia no encontrada: " + seqName);
        }

        IAnalysisStrategy analysis = analysisFactory.createAnalysis(AnalysisType.MOTIF_DETECTION, motif);
        AnalysisProxy proxy = new AnalysisProxy(analysis);

        return proxy.execute(sequence);
    }

    public AnalysisResult performStructurePrediction(String seqName) {
        GeneticSequence sequence = sequenceDAO.findByName(seqName);

        if (sequence == null) {
            throw new IllegalArgumentException("Secuencia no encontrada: " + seqName);
        }

        IAnalysisStrategy analysis = analysisFactory.createAnalysis(AnalysisType.STRUCTURE_PREDICTION);
        AnalysisProxy proxy = new AnalysisProxy(analysis);

        return proxy.execute(sequence);
    }

    // ========== REPORTS ==========
    public Report generateReport(AnalysisResult result, String title) {
        ReportBuilder builder = new ReportBuilder();
        builder.withTitle(title);

        switch (result.getAnalysisType()) {
            case "ALIGNMENT":
                builder.addAlignmentSection(result);
                break;
            case "MOTIF_DETECTION":
                builder.addMotifDetectionSection(result);
                break;
            case "STRUCTURE_PREDICTION":
                builder.addStructurePredictionSection(result);
                break;
        }

        return builder.build();
    }

    public Report generateMultiAnalysisReport(List<AnalysisResult> results, String title) {
        ReportBuilder builder = new ReportBuilder();
        builder.withTitle(title);

        for (AnalysisResult result : results) {
            switch (result.getAnalysisType()) {
                case "ALIGNMENT":
                    builder.addAlignmentSection(result);
                    break;
                case "MOTIF_DETECTION":
                    builder.addMotifDetectionSection(result);
                    break;
                case "STRUCTURE_PREDICTION":
                    builder.addStructurePredictionSection(result);
                    break;
            }
        }

        return builder.build();
    }

    public Report generateCachedAnalysisReport() {
        List<AnalysisResult> results = storage.getAllResults();
        return generateMultiAnalysisReport(results, "REPORTE DE ANÁLISIS GENÉTICOS - RESULTADOS EN CACHÉ");
    }

    // ========== CONFIGURATION ==========
    public Configuration getConfiguration() {
        return config;
    }

    public void clearCache() {
        storage.clearAll();
        System.out.println("Caché de análisis limpiada");
    }

    public int getCachedAnalysisCount() {
        return storage.getResultCount();
    }

    // ========== DATABASE CONNECTION ==========
    public boolean isDatabaseConnected() {
        return DatabaseConnection.getInstance().isConnected();
    }

    public void closeDatabaseConnection() {
        DatabaseConnection.getInstance().closeConnection();
    }

    // ========== SESSION RESULTS MANAGEMENT ==========
    public void addSessionResult(AnalysisResult result) {
        sessionResults.add(result);
    }

    public List<AnalysisResult> getSessionResults() {
        return new ArrayList<>(sessionResults);
    }

    public int getSessionResultsCount() {
        return sessionResults.size();
    }

    public void clearSessionResults() {
        sessionResults.clear();
    }

    // ========== REPORT EXPORT ==========
    public void exportReport(String content, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }

    // ========== CONFIGURATION HELPERS ==========
    public void setFastaPath(String path) {
        config.setFastaPath(path);
    }

    public void setMinSequenceLength(int minLength) {
        config.setMinSequenceLength(minLength);
    }

    public String getFastaPath() {
        return config.getFastaPath();
    }

    public int getMinSequenceLength() {
        return config.getMinSequenceLength();
    }

    // ========== UTILITIES ==========
    public String getAnalysisTypeName(String type) {
        return switch (type) {
            case "ALIGNMENT" -> "Análisis de Alineamiento";
            case "MOTIF_DETECTION" -> "Detección de Motivos";
            case "STRUCTURE_PREDICTION" -> "Predicción de Estructura";
            default -> type;
        };
    }
}
