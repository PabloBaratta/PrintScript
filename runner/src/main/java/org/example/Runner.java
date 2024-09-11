package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.interpreter.Interpreter;
import org.example.interpreter.handlers.HandlerFactory;
import org.example.lexer.StreamReader;
import org.linter.Linter;
import org.linter.LinterProvider;
import org.linter.Report;
import org.linter.ReportLine;
import org.token.Token;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static org.example.lexer.LexerProvider.provideV10;
import static org.example.lexer.LexerProvider.provideV11;

public class Runner {

	public static void run(InputStream inputStream, String version) throws Exception {
		PrintScriptIterator<ASTNode> parser = lnp(inputStream, version);
		Interpreter interpreter = new Interpreter(parser, HandlerFactory.createHandlers(version));
		interpreter.execute();
	}

	public static void validate(InputStream inputStream, String version) throws Exception {
		PrintScriptIterator<ASTNode> parser = lnp(inputStream, version);
		Interpreter interpreter = new Interpreter(parser, HandlerFactory.createHandlers(version));
		interpreter.validate();
	}

	public static void lint(InputStream inputStream, String version, String config) throws Exception {
		PrintScriptIterator<ASTNode> parser = lnp(inputStream, version);
		lint(parser, version, config);
	}

	public static String format(InputStream inputStream, String version, String config) throws Exception {
		return format(lnp(inputStream, version), config);
	}

	private static PrintScriptIterator<ASTNode> lnp(InputStream inputStream, String version) throws Exception {
		Iterator<String> iterator = new StreamReader(inputStream);
		switch (version){
			case "1.0":
				PrintScriptIterator<Token> lexerV10 = provideV10(iterator);
				return ParserProvider.provide10(lexerV10);

			case "1.1":
				PrintScriptIterator<Token> lexerV11 = provideV11(iterator);
				return ParserProvider.provide11(lexerV11);
		}
		throw new Exception("Invalid version");
	}

	private static Report lint(PrintScriptIterator<ASTNode> ast, String version, String config) throws Exception {
		Linter linter = switch (version) {
			case "1.0" -> LinterProvider.getLinterV10();
			case "1.1" -> LinterProvider.getLinterV11();
			default -> throw new Exception("Invalid version");
		};
		Map<String, String> configuration = parseConfig(config);
		Report report = linter.analyze(ast, configuration);
		for (ReportLine reportLine : report.getReportLines()) {
			System.out.println(reportLine.errorMessage() + " on " + reportLine.position().toString());
		}
		return report;
	}

	private static String format(PrintScriptIterator<ASTNode> parser, String config) throws Exception {
		//Map<String, Rule> rules = JsonReader.readRulesFromJson(config);
		Formatter formatter = FormatterProvider.provideV10(parser);
		return formatter.format();
	}

	private static Map parseConfig(String jsonConfig) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonConfig, Map.class);
	}
}
