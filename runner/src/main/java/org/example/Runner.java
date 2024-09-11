package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.interpreter.ConsoleInputProvider;
import org.example.interpreter.InputProvider;
import org.example.interpreter.Interpreter;
import org.example.interpreter.InterpreterProvider;
import org.example.interpreter.handlers.HandlerFactory;
import org.example.lexer.StreamReader;
import org.linter.Linter;
import org.linter.LinterProvider;
import org.linter.Report;
import org.linter.ReportLine;
import org.token.Token;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.example.lexer.LexerProvider.provideV10;
import static org.example.lexer.LexerProvider.provideV11;

public class Runner {

	public static void run(InputStream inputStream, String version) throws Exception {
		PrintScriptIterator<ASTNode> parser = lnp(inputStream, version);
		Interpreter interpreter = switch (version) {
			case "1.0" -> InterpreterProvider.provideV10(parser, new ConsoleInputProvider());
			case "1.1" -> InterpreterProvider.provideV11(parser, new ConsoleInputProvider());
			default -> throw new Exception("Invalid version");
		};
		interpreter.execute();
	}

	public static void validate(InputStream inputStream, String version) throws Exception {
		PrintScriptIterator<ASTNode> parser = lnp(inputStream, version);
		Interpreter interpreter = switch (version) {
			case "1.0" -> InterpreterProvider.provideV10(parser, new ConsoleInputProvider());
			case "1.1" -> InterpreterProvider.provideV11(parser, new ConsoleInputProvider());
			default -> throw new Exception("Invalid version");
		};
		interpreter.validate();
	}

	public static List<String> lint(InputStream inputStream, String version, String config) throws Exception {
		PrintScriptIterator<ASTNode> parser = lnp(inputStream, version);
		List<ReportLine> reportLines = lint(parser, version, config).getReportLines();
		List<String> errors = new ArrayList<>();
		for (ReportLine reportLine : reportLines) {
			errors.add(reportLine.errorMessage() + " on " + reportLine.position().toString());
		}
		return errors;
	}

	public static String format(InputStream inputStream, String version, String config) throws Exception {
		return format(lnp(inputStream, version), config, version);
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

	private static String format(PrintScriptIterator<ASTNode> parser, String config, String version)
			throws Exception {
		Formatter formatter = switch (version){
			case "1.0" -> FormatterProvider.provideV10(parser, config);
			case "1.1" -> FormatterProvider.provideV11(parser, config);
			default -> throw new Exception("Invalid version");
		};
		return formatter.format();
	}

	private static Map<String, String> parseConfig(String jsonConfig) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonConfig, Map.class);
	}
}
