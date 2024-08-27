package org.example.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Program;
import org.example.lexer.token.Token;
import org.linter.Linter;
import org.linter.LinterConfigurator;
import org.linter.Report;
import org.linter.ReportLine;
import org.linter.configurator.Configurator;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.PrintLineConfiguration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.example.Runner.lex;
import static org.example.Runner.parse;

public class AnalyzingBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 3) {
            throw new RuntimeException("Invalid number of arguments, should be three");
        }
        String pathFile = Paths.get("").toAbsolutePath().getParent() + parts[1];
        String pathConfig = Paths.get("").toAbsolutePath().getParent() + parts[2];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        List<Token> tokens = lex(code);
        Program program = parse(tokens);
        List<Configurator> configurators = List.of(new IdentifierConfiguration(), new PrintLineConfiguration());
        Linter linter = new Linter(new LinterConfigurator(configurators));
        Report report = linter.analyze(program, getConfigurators(pathConfig));
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
