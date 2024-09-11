package org.example.builders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.example.Runner.*;

public class AnalyzingBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 4) {
            throw new RuntimeException("Invalid number of arguments, should be four");
        }
        String pathFile = Paths.get("").toAbsolutePath() + parts[1];

        String pathConfig = Paths.get("").toAbsolutePath() + parts[2];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        InputStream inputStream = new ByteArrayInputStream(code.getBytes());

        String version = parts[3];

        String config = Files.lines(Paths.get(pathConfig))
                .collect(Collectors.joining("\n"));

        lint(inputStream, version, config);
        return "linting completed";
    }

}
