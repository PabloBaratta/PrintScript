package org.example.builders;

import org.example.Util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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

        InputStream stream = new FileInputStream(pathFile);

        String version = parts[3];

        String config = Files.lines(Paths.get(pathConfig))
                .collect(Collectors.joining("\n"));

        List<String> lint = lint(Util.getObservableInputStream(stream), version, config);

        return "linted";
    }

}
