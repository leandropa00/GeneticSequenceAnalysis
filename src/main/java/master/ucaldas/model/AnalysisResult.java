package master.ucaldas.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AnalysisResult {
    private String analysisType;
    private LocalDateTime timestamp;
    private Map<String, Object> data;

    public AnalysisResult(String analysisType) {
        this.analysisType = analysisType;
        this.timestamp = LocalDateTime.now();
        this.data = new HashMap<>();
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    public Object getData(String key) {
        return this.data.get(key);
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getAllData() {
        return new HashMap<>(data);
    }

    @Override
    public String toString() {
        return "AnalysisResult{" +
                "type='" + analysisType + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}