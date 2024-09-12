package org.example.builders;

import org.example.CliOutputEmitter;
import org.example.Runner;
import org.example.ConsoleInputProvider;
import org.example.Util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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

        InputStream stream = new FileInputStream(filePath);
        String version = parts[2];
        Runner.run(Util.getObservableInputStream(stream), version, new ConsoleInputProvider(), new CliOutputEmitter());
        return "Execution completed";
    }
}
