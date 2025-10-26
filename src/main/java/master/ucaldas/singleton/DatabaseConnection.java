package master.ucaldas.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private Configuration config;

    private DatabaseConnection() {
        config = Configuration.getInstance();
        connect();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void connect() {
        int maxRetries = 10;
        int retryDelay = 3000;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = config.getDatabaseUrl();
                String username = config.getDatabaseUsername();
                String password = config.getDatabasePassword();
                
                if (attempt > 1) {
                    System.out.println("Intento " + attempt + "/" + maxRetries + " de conexión a la base de datos...");
                }
                
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Conexión a base de datos establecida");
                return;
            } catch (ClassNotFoundException e) {
                System.err.println("Error al conectar a la base de datos: " + e.getMessage());
                connection = null;
                return;
            } catch (SQLException e) {
                if (attempt == maxRetries) {
                    System.err.println("Error al conectar a la base de datos después de " + maxRetries + " intentos: " + e.getMessage());
                    connection = null;
                    return;
                }
                
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    connection = null;
                    return;
                }
            }
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Error verificando conexión: " + e.getMessage());
            connect();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

