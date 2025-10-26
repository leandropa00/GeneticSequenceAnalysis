package master.ucaldas.strategy;

import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;

public class AlignmentAnalysis implements IAnalysisStrategy {
    private static final String ANALYSIS_TYPE = "ALIGNMENT";
    
    @Override
    public AnalysisResult execute(GeneticSequence... sequences) {
        if (sequences.length < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 secuencias para alineamiento");
        }

        GeneticSequence seq1 = sequences[0];
        GeneticSequence seq2 = sequences[1];

        AnalysisResult result = new AnalysisResult(ANALYSIS_TYPE);

        double similarity = calculateSimilarity(seq1.getSequence(), seq2.getSequence());

        int length1 = seq1.getSequence().length();
        int length2 = seq2.getSequence().length();
        String longerSequence = length1 >= length2 ? seq1.getName() : seq2.getName();

        result.addData("sequence1", seq1.getName());
        result.addData("sequence2", seq2.getName());
        result.addData("length1", length1);
        result.addData("length2", length2);
        result.addData("similarity_percentage", similarity);
        result.addData("longer_sequence", longerSequence);

        return result;
    }

    private double calculateSimilarity(String seq1, String seq2) {
        int minLength = Math.min(seq1.length(), seq2.length());
        int maxLength = Math.max(seq1.length(), seq2.length());

        if (maxLength == 0) {
            return 0.0;
        }

        int matches = 0;
        for (int i = 0; i < minLength; i++) {
            if (seq1.charAt(i) == seq2.charAt(i)) {
                matches++;
            }
        }

        return (double) matches / maxLength * 100.0;
    }

    @Override
    public String getAnalysisType() {
        return ANALYSIS_TYPE;
    }
}
