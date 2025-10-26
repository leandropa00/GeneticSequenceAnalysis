package master.ucaldas.strategy;

import java.util.HashMap;
import java.util.Map;

import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;

public class StructurePredictionAnalysis implements IAnalysisStrategy {
    private static final String ANALYSIS_TYPE = "STRUCTURE_PREDICTION";

    @Override
    public AnalysisResult execute(GeneticSequence... sequences) {
        if (sequences.length < 1) {
            throw new IllegalArgumentException("Se requiere al menos 1 secuencia para predicción de estructura");
        }

        GeneticSequence sequence = sequences[0];
        AnalysisResult result = new AnalysisResult(ANALYSIS_TYPE);

        Map<Character, Integer> baseCounts = countBases(sequence.getSequence());

        String structure = predictStructure(baseCounts, sequence.getSequence().length());

        result.addData("sequence_name", sequence.getName());
        result.addData("base_counts", baseCounts);
        result.addData("predicted_structure", structure);
        result.addData("sequence_length", sequence.getSequence().length());

        return result;
    }

    private Map<Character, Integer> countBases(String sequence) {
        Map<Character, Integer> counts = new HashMap<>();
        counts.put('A', 0);
        counts.put('T', 0);
        counts.put('C', 0);
        counts.put('G', 0);

        String upperSequence = sequence.toUpperCase();
        for (char base : upperSequence.toCharArray()) {
            if (counts.containsKey(base)) {
                counts.put(base, counts.get(base) + 1);
            }
        }

        return counts;
    }

    private String predictStructure(Map<Character, Integer> baseCounts, int totalLength) {
        int gcCount = baseCounts.get('G') + baseCounts.get('C');
        int atCount = baseCounts.get('A') + baseCounts.get('T');

        double gcPercentage = (double) gcCount / totalLength * 100.0;
        double atPercentage = (double) atCount / totalLength * 100.0;

        if (gcPercentage > 60.0) {
            return "ALFA";
        } else if (atPercentage > 60.0) {
            return "BETA";
        } else {
            return "MIXTA";
        }
    }

    @Override
    public String getAnalysisType() {
        return ANALYSIS_TYPE;
    }
}

