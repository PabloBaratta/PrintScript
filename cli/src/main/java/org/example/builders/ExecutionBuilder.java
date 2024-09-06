package org.example.builders;

import org.example.Runner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ExecutionBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 3) {
            throw new RuntimeException("Invalid number of arguments, should be three");
        }
        String filePath = Paths.get("").toAbsolutePath() + parts[1];
        String code = Files.lines(Paths.get(filePath))
                .collect(Collectors.joining("\n"));
        InputStream inputStream = new ByteArrayInputStream(code.getBytes());
        String version = parts[2];
        Runner.run(inputStream, version);
        return "Execution completed";
    }
}
