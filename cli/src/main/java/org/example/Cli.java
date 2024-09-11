package org.example;

import org.example.builders.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cli {
	Map<String, CommandBuilder> builders;
	String path;

	public Cli(Map<String, CommandBuilder> builders, String path) {
		this.builders = builders;
		this.path = path;
	}

	public Cli(String path) {
		this.path = path;
		this.builders = new HashMap<>();
		builders.put("execute", new ExecutionBuilder());
		builders.put("analyze", new AnalyzingBuilder());
		builders.put("format", new FormattingBuilder());
		builders.put("validate", new ValidationBuilder());
	}

	public void run() throws Exception {
		List<String> lines = getLines();
		for (String line : lines) {
			String[] parts = line.split(" ");
			String commandName = parts[0];
			CommandBuilder builder = builders.get(commandName);
			if (builder == null) {
				throw new RuntimeException("Command not found: " + commandName);
			}
			builder.buildAndRun(parts);
		}
	}

	private List<String> getLines() throws IOException {
		String content = Files.lines(Paths.get(path))
				.collect(Collectors.joining("\n"));
		return List.of(content.split("\n"));
	}
}
