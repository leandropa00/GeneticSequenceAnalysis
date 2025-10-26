package master.ucaldas.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import master.ucaldas.model.AnalysisResult;

public class AnalysisResultStorage {
    private static AnalysisResultStorage instance;
    private Map<String, AnalysisResult> resultCache;

    private AnalysisResultStorage() {
        resultCache = new HashMap<>();
    }

    public static synchronized AnalysisResultStorage getInstance() {
        if (instance == null) {
            instance = new AnalysisResultStorage();
        }
        return instance;
    }

    public void storeResult(String key, AnalysisResult result) {
        resultCache.put(key, result);
    }

    public AnalysisResult getResult(String key) {
        return resultCache.get(key);
    }

    public boolean hasResult(String key) {
        return resultCache.containsKey(key);
    }

    public void clearAll() {
        resultCache.clear();
    }

    public List<AnalysisResult> getAllResults() {
        return new ArrayList<>(resultCache.values());
    }

    public int getResultCount() {
        return resultCache.size();
    }
}