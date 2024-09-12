package org.example.builders;

import org.apache.commons.io.input.ObservableInputStream;
import org.example.CliOutputEmitter;
import org.example.ConsoleInputProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import static org.example.Runner.*;
import static org.example.Util.getObservableInputStream;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 3) {
            throw new RuntimeException("Invalid number of arguments, should be three");
        }
        String pathFile = Paths.get("").toAbsolutePath() + parts[1];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));

        InputStream stream = new FileInputStream(pathFile);

        String version = parts[2];
        validate(getObservableInputStream(stream), version, new ConsoleInputProvider(), new CliOutputEmitter());
        return "Validation completed";
    }


}
