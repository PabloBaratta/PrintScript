package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Rule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonReader {

	public static Map<String, Rule> readRulesFromJson(String jsonString) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonString);
		Map<String, Rule> rules = new HashMap<>();

		rootNode.fields().forEachRemaining(entry -> {
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			boolean rule = value.get("rule").asBoolean();
			Optional<Integer> qty = value.has("qty") ? Optional.of(value.get("qty").asInt()) : Optional.empty();
			rules.put(key, new Rule(rule, qty));
		});

		return rules;
	}
}
