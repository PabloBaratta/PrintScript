package org.example.builders;

import org.example.Program;
import org.example.interpreter.Interpreter;
import org.example.lexer.token.Token;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.Runner.lex;
import static org.example.Runner.parse;
import static org.example.Util.createInterpreter;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        String pathFile = parts[1];
        if (pathFile == null) {
            throw new RuntimeException("File path not found");
        }
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        List<Token> tokens = lex(code);
        Program ast = parse(tokens);
        Interpreter interpreter = createInterpreter();
        // Validation
        return "Validation completed";
    }
}
