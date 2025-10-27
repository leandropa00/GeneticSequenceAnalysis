package master.ucaldas.proxy;

import java.util.Arrays;
import java.util.stream.Collectors;

import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;
import master.ucaldas.singleton.AnalysisResultStorage;
import master.ucaldas.singleton.Configuration;
import master.ucaldas.strategy.IAnalysisStrategy;

public class AnalysisProxy implements IAnalysisStrategy {
    private IAnalysisStrategy realAnalysis;
    private Configuration config;
    private AnalysisResultStorage storage;

    public AnalysisProxy(IAnalysisStrategy realAnalysis) {
        this.realAnalysis = realAnalysis;
        this.config = Configuration.getInstance();
        this.storage = AnalysisResultStorage.getInstance();
    }

    @Override
    public AnalysisResult execute(GeneticSequence... sequences) {
        if (sequences == null || sequences.length == 0) {
            throw new IllegalArgumentException("No se proporcionaron secuencias para el análisis");
        }

        for (GeneticSequence seq : sequences) {
            if (seq == null || seq.getSequence() == null || seq.getSequence().isEmpty()) {
                throw new IllegalArgumentException("Secuencia inválida o vacía");
            }

            if (!isValidSequence(seq.getSequence())) {
                throw new IllegalArgumentException(
                    String.format("La secuencia '%s' contiene caracteres inválidos", seq.getName())
                );
            }
        }

        int minLength = config.getMinSequenceLength();
        for (GeneticSequence seq : sequences) {
            if (seq.getSequence().length() < minLength) {
                throw new IllegalArgumentException(
                    String.format("La secuencia '%s' es demasiado corta (mínimo: %d bases)",
                        seq.getName(), minLength)
                );
            }
        }

        String cacheKey = generateCacheKey(sequences);

        if (storage.hasResult(cacheKey)) {
            System.out.println("Resultado recuperado de caché");
            return storage.getResult(cacheKey);
        }

        System.out.println("Ejecutando análisis...");
        AnalysisResult result = realAnalysis.execute(sequences);

        storage.storeResult(cacheKey, result);

        return result;
    }

    private boolean isValidSequence(String sequence) {
        String upperSeq = sequence.toUpperCase();
        
        String dnaChars = "ATCGUN";
        String proteinChars = "ACDEFGHIKLMNPQRSTVWY";
        
        for (char c : upperSeq.toCharArray()) {
            if (!dnaChars.contains(String.valueOf(c)) && 
                !proteinChars.contains(String.valueOf(c))) {
                return false;
            }
        }
        
        return true;
    }

    private String generateCacheKey(GeneticSequence... sequences) {
        String sequenceIds = Arrays.stream(sequences)
            .map(seq -> seq.getName() + ":" + seq.getSequence().hashCode())
            .collect(Collectors.joining("|"));

        return realAnalysis.getAnalysisType() + ":" + sequenceIds;
    }

    @Override
    public String getAnalysisType() {
        return realAnalysis.getAnalysisType();
    }
}

