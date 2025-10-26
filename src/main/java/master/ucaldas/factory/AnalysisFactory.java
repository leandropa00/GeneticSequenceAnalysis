package master.ucaldas.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import master.ucaldas.strategy.AlignmentAnalysis;
import master.ucaldas.strategy.IAnalysisStrategy;
import master.ucaldas.strategy.MotifDetectionAnalysis;
import master.ucaldas.strategy.StructurePredictionAnalysis;

public class AnalysisFactory {
    private final Map<AnalysisType, Supplier<IAnalysisStrategy>> strategies = new HashMap<>();

    public AnalysisFactory() {
        strategies.put(AnalysisType.ALIGNMENT, AlignmentAnalysis::new);
        strategies.put(AnalysisType.STRUCTURE_PREDICTION, StructurePredictionAnalysis::new);
    }

    public IAnalysisStrategy createAnalysis(AnalysisType type, String... params) {
        if (type == AnalysisType.MOTIF_DETECTION) {
            if (params.length == 0 || params[0] == null || params[0].isEmpty()) {
                throw new IllegalArgumentException("Se requiere un motivo para detección");
            }
            return new MotifDetectionAnalysis(params[0]);
        }

        Supplier<IAnalysisStrategy> supplier = strategies.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Tipo de análisis no soportado: " + type);
        }
        
        return supplier.get();
    }
}