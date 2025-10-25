package master.ucaldas.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import master.ucaldas.model.GeneticSequence;

public class FASTAReader {

    public static List<GeneticSequence> readFASTA(String filePath) throws IOException {
        List<GeneticSequence> sequences = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            GeneticSequence currentSequence = null;
            StringBuilder sequenceData = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith(">")) {
                    if (currentSequence != null) {
                        currentSequence.setSequence(sequenceData.toString());
                        sequences.add(currentSequence);
                        sequenceData = new StringBuilder();
                    }

                    currentSequence = parseHeader(line);
                } else {
                    sequenceData.append(line);
                }
            }

            if (currentSequence != null) {
                currentSequence.setSequence(sequenceData.toString());
                sequences.add(currentSequence);
            }
        }

        return sequences;
    }

    private static GeneticSequence parseHeader(String header) {
        header = header.substring(1);

        String[] parts = header.split("\\s+", 2);
        String name = parts[0];

        String description = "";
        String type = "DNA";

        if (parts.length > 1) {
            String metadata = parts[1];

            int descIndex = metadata.indexOf("description=");
            if (descIndex >= 0) {
                int descEnd = metadata.indexOf(" ", descIndex);
                if (descEnd < 0) {
                    descEnd = metadata.length();
                }
                description = metadata.substring(descIndex + 12, descEnd);
            }

            int typeIndex = metadata.indexOf("type=");
            if (typeIndex >= 0) {
                int typeEnd = metadata.indexOf(" ", typeIndex);
                if (typeEnd < 0) {
                    typeEnd = metadata.length();
                }
                type = metadata.substring(typeIndex + 5, typeEnd);
            }
        }

        return new GeneticSequence(name, description, type, "");
    }
}
