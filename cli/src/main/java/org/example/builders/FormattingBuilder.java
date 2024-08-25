package org.example.builders;

import org.example.Formatter;
import org.example.JsonReader;
import org.example.Program;
import org.example.Rule;
import org.example.lexer.token.Token;
import java.io.IOException;
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
