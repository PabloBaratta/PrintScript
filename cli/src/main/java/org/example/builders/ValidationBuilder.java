/*
package org.example.builders;

import org.example.Program;
import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.StreamReader;
import org.token.Token;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.example.Runner.parse;
import static org.example.lexer.LexerProvider.provideV10;
import static org.example.resources.Util.createInterpreter;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
//        if (parts.length != 2) {
//            throw new RuntimeException("Invalid number of arguments, should be two");
//        }
//        String pathFile = Paths.get("").toAbsolutePath() + parts[1];
//        String code = Files.lines(Paths.get(pathFile))
//                .collect(Collectors.joining("\n"));
//        InputStream inputStream = new ByteArrayInputStream(code.getBytes());
//        StreamReader reader = new StreamReader(inputStream);
//        Lexer lexer = provideV10(reader);
//        Program ast = parse(lexer);
//        Interpreter interpreter = provideV10(ast);
//        interpreter.validate();
        return "Validation completed";
    }
}
*/
