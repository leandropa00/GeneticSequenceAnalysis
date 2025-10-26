package master.ucaldas.builder;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String title;
    private List<String> sections;

    public Report() {
        this.sections = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addSection(String section) {
        this.sections.add(section);
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSections() {
        return new ArrayList<>(sections);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(80)).append("\n");
        sb.append(title).append("\n");
        sb.append("═".repeat(80)).append("\n\n");

        for (String section : sections) {
            sb.append(section).append("\n");
        }

        sb.append("═".repeat(80)).append("\n");
        return sb.toString();
    }

    public String export() {
        return toString();
    }
}

