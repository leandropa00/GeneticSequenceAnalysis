package master.ucaldas.strategy;

import java.util.ArrayList;
import java.util.List;

import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;

public class MotifDetectionAnalysis implements IAnalysisStrategy {
    private static final String ANALYSIS_TYPE = "MOTIF_DETECTION";
    private String motif;

    public MotifDetectionAnalysis(String motif) {
        if (motif == null || motif.isEmpty()) {
            throw new IllegalArgumentException("El motivo no puede estar vacío");
        }
        this.motif = motif.toUpperCase();
    }

    @Override
    public AnalysisResult execute(GeneticSequence... sequences) {
        if (sequences.length < 1) {
            throw new IllegalArgumentException("Se requiere al menos 1 secuencia para detección de motivos");
        }

        GeneticSequence sequence = sequences[0];
        AnalysisResult result = new AnalysisResult(ANALYSIS_TYPE);

        List<Integer> positions = findMotifPositions(sequence.getSequence(), motif);

        result.addData("sequence_name", sequence.getName());
        result.addData("motif", motif);
        result.addData("positions", positions);
        result.addData("occurrences", positions.size());

        return result;
    }

    private List<Integer> findMotifPositions(String sequence, String motif) {
        List<Integer> positions = new ArrayList<>();
        String upperSequence = sequence.toUpperCase();

        int index = upperSequence.indexOf(motif);
        while (index >= 0) {
            positions.add(index);
            index = upperSequence.indexOf(motif, index + 1);
        }

        return positions;
    }

    @Override
    public String getAnalysisType() {
        return ANALYSIS_TYPE;
    }

    public String getMotif() {
        return motif;
    }
}

