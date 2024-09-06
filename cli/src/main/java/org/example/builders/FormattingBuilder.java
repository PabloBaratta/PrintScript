package org.example.builders;

import org.example.Formatter;
import org.example.JsonReader;
import org.example.Program;
import org.example.Rule;
import org.example.lexer.StreamReader;
import org.example.lexer.token.Token;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.Runner.lex;
import static org.example.Runner.parse;

public class FormattingBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 3) {
            throw new RuntimeException("Invalid number of arguments, should be three");
        }
        String pathFile = Paths.get("").toAbsolutePath() + parts[1];
        String pathConfig = Paths.get("").toAbsolutePath() + parts[2];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        InputStream inputStream = new ByteArrayInputStream(code.getBytes());
        StreamReader reader = new StreamReader(inputStream);
        List<Token> tokens = lex(reader);
        Program program = parse(tokens);
        Map<String, Rule> rules = readJson(pathConfig);
        Formatter formatter = new Formatter(rules);
        System.out.println(formatter.format(program));
        return formatter.format(program);
    }

    private Map<String, Rule> readJson(String pathConfig) throws IOException {
        String config = Files.lines(Paths.get(pathConfig))
                .collect(Collectors.joining("\n"));
        return JsonReader.readRulesFromJson(config);
    }
}
