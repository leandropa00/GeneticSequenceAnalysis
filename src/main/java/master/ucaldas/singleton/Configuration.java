package master.ucaldas.singleton;

import java.util.Properties;

public class Configuration {
    private static Configuration instance;
    private Properties properties;
    private int minSequenceLength = 10;
    private String fastaPath = "data/sequences.fasta";

    private Configuration() {
        properties = new Properties();
        loadDefaultConfiguration();
    }

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private void loadDefaultConfiguration() {
        properties.setProperty("db.url", "jdbc:mysql://mysql:3306/db?useSSL=false&allowPublicKeyRetrieval=true");
        properties.setProperty("db.username", "user");
        properties.setProperty("db.password", "password");
        properties.setProperty("min.sequence.length", String.valueOf(minSequenceLength));
        properties.setProperty("fasta.path", fastaPath);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public int getMinSequenceLength() {
        return Integer.parseInt(properties.getProperty("min.sequence.length"));
    }

    public void setMinSequenceLength(int length) {
        properties.setProperty("min.sequence.length", String.valueOf(length));
    }

    public String getFastaPath() {
        return properties.getProperty("fasta.path");
    }

    public void setFastaPath(String path) {
        properties.setProperty("fasta.path", path);
    }

    public String getDatabaseUrl() {
        return properties.getProperty("db.url");
    }

    public String getDatabaseUsername() {
        return properties.getProperty("db.username");
    }

    public String getDatabasePassword() {
        return properties.getProperty("db.password");
    }
}

