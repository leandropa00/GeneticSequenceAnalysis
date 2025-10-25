package master.ucaldas.model;

import java.time.LocalDateTime;

public class GeneticSequence {

    private Integer id;
    private String name;
    private String description;
    private String type;
    private String sequence;
    private LocalDateTime creationDate;

    public GeneticSequence() {
    }

    public GeneticSequence(String name, String description, String type, String sequence) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.sequence = sequence;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "GeneticSequence{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", type='" + type + '\''
                + ", sequenceLength=" + (sequence != null ? sequence.length() : 0)
                + '}';
    }
}
