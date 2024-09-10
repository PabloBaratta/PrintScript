package org.example.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Parser;
import org.example.ParserProvider;
import org.example.Program;
import org.example.lexer.Lexer;
import org.example.lexer.StreamReader;
import org.example.lexer.token.Token;
import org.linter.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.example.Runner.parse;
import static org.example.lexer.LexerProvider.provideV10;

public class AnalyzingBuilder implements CommandBuilder{
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
        Lexer lexer = provideV10(reader);
        Parser parser = ParserProvider.provide10(lexer);
        Linter linter = LinterProvider.getLinterV10();
        Report report = linter.analyze(parser, getConfigurators(pathConfig));
        for (ReportLine reportLine : report.getReportLines()) {
            System.out.println(reportLine.errorMessage() + " on " + reportLine.position().toString());
        }
        return report.toString();
    }

    private Map<String, String> getConfigurators(String pathConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(Paths.get(pathConfig).toFile());
        Map<String, String> configMap = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            configMap.put(field.getKey(), field.getValue().asText());
        }

        return configMap;
    }

}
