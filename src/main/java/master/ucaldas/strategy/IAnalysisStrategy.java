package master.ucaldas.strategy;

import master.ucaldas.model.AnalysisResult;
import master.ucaldas.model.GeneticSequence;

public interface IAnalysisStrategy {

    AnalysisResult execute(GeneticSequence... sequences);

    String getAnalysisType();
}
