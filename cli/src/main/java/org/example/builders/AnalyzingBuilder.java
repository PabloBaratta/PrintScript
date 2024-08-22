package org.example.builders;

import org.example.Program;
import org.example.lexer.token.Token;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import static org.example.Runner.lex;
import static org.example.Runner.parse;

public class AnalyzingBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        String pathFile = parts[1];
        String pathConfig = parts[2];
        if (pathFile == null) {
            throw new RuntimeException("File path not found");
        }
        if (pathConfig == null) {
            throw new RuntimeException("Config path not found");
        }
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        List<Token> tokens = lex(code);
        Program program = parse(tokens);
        // Analyzing logic
        // return analyzer.analyze(program); ???
        return "Analyzing completed";
    }
}
