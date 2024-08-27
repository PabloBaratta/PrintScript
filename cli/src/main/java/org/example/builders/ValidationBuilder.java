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
import static org.example.resources.Util.createInterpreter;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 2) {
            throw new RuntimeException("Invalid number of arguments, should be two");
        }
        String pathFile = Paths.get("").toAbsolutePath().getParent() + parts[1];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        List<Token> tokens = lex(code);
        Program ast = parse(tokens);
        Interpreter interpreter = createInterpreter();
        interpreter.validate(ast);
        return "Validation completed";
    }
}
