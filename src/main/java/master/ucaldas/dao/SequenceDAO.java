package master.ucaldas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import master.ucaldas.model.GeneticSequence;
import master.ucaldas.singleton.DatabaseConnection;

public class SequenceDAO {
    private DatabaseConnection dbConnection;

    public SequenceDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean insert(GeneticSequence sequence) {
        String sql = "INSERT INTO genetic_sequences (name, description, type, sequence) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, sequence.getName());
            stmt.setString(2, sequence.getDescription());
            stmt.setString(3, sequence.getType());
            stmt.setString(4, sequence.getSequence());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sequence.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error insertando secuencia: " + e.getMessage());
        }

        return false;
    }

    public List<GeneticSequence> findAll() {
        List<GeneticSequence> sequences = new ArrayList<>();
        String sql = "SELECT id, name, description, type, sequence, creation_date FROM genetic_sequences ORDER BY id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GeneticSequence seq = new GeneticSequence();
                seq.setId(rs.getInt("id"));
                seq.setName(rs.getString("name"));
                seq.setDescription(rs.getString("description"));
                seq.setType(rs.getString("type"));
                seq.setSequence(rs.getString("sequence"));
                seq.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                sequences.add(seq);
            }
        } catch (SQLException e) {
            System.err.println("Error recuperando secuencias: " + e.getMessage());
        }

        return sequences;
    }

    public GeneticSequence findByName(String name) {
        String sql = "SELECT id, name, description, type, sequence, creation_date FROM genetic_sequences WHERE name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    GeneticSequence seq = new GeneticSequence();
                    seq.setId(rs.getInt("id"));
                    seq.setName(rs.getString("name"));
                    seq.setDescription(rs.getString("description"));
                    seq.setType(rs.getString("type"));
                    seq.setSequence(rs.getString("sequence"));
                    seq.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                    return seq;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando secuencia: " + e.getMessage());
        }

        return null;
    }

    public List<GeneticSequence> findByType(String type) {
        List<GeneticSequence> sequences = new ArrayList<>();
        String sql = "SELECT id, name, description, type, sequence, creation_date FROM genetic_sequences WHERE type = ? ORDER BY id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GeneticSequence seq = new GeneticSequence();
                    seq.setId(rs.getInt("id"));
                    seq.setName(rs.getString("name"));
                    seq.setDescription(rs.getString("description"));
                    seq.setType(rs.getString("type"));
                    seq.setSequence(rs.getString("sequence"));
                    seq.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                    sequences.add(seq);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando por tipo: " + e.getMessage());
        }

        return sequences;
    }

    public boolean deleteAll() {
        String sql = "DELETE FROM genetic_sequences";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error eliminando secuencias: " + e.getMessage());
        }

        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) as total FROM genetic_sequences";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error contando secuencias: " + e.getMessage());
        }

        return 0;
    }
}

