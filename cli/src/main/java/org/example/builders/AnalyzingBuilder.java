package org.example.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.Runner.*;

public class AnalyzingBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 4) {
            throw new RuntimeException("Invalid number of arguments, should be four");
        }
        String pathFile = Paths.get("").toAbsolutePath() + parts[1];

        String pathConfig = Paths.get("").toAbsolutePath() + parts[2];
        String code = Files.lines(Paths.get(pathFile))
                .collect(Collectors.joining("\n"));
        InputStream inputStream = new ByteArrayInputStream(code.getBytes());

        String version = parts[3];

        lint(inputStream, version, getConfigurators(pathConfig));
        return "linting completed";
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
