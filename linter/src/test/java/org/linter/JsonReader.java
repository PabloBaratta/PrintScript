package org.linter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonReader {

	public Map<String, String> readJsonToMap(String filePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		TypeReference<Map<String, String>> valueTypeRef = new TypeReference<>() {};
		return objectMapper.readValue(new File(filePath), valueTypeRef);
	}

}
