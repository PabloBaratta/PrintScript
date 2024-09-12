package org.example.builders;

import org.example.interpreter.ConsoleInputProvider;
import org.example.interpreter.OutputCapture;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import static org.example.Runner.*;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 3) {
            throw new RuntimeException("Invalid number of arguments, should be three");
        }
        String pathFile = Paths.get("").toAbsolutePath() + parts[1];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        InputStream inputStream = new ByteArrayInputStream(code.getBytes());
        String version = parts[2];
        validate(inputStream, version, new ConsoleInputProvider(), new OutputCapture());
        return "Validation completed";
    }
}
