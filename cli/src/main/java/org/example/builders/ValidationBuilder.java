package org.example.builders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        String pathFile = parts[1];
        if (pathFile == null) {
            throw new RuntimeException("File path not found");
        }
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        // Validation logic
        return "Validation completed";
    }
}
