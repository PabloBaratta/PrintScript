package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.StreamReader;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;
import org.linter.Linter;
import org.linter.LinterProvider;
import org.linter.Report;
import org.linter.ReportLine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.lexer.LexerProvider.provideV10;
import static org.example.lexer.LexerProvider.provideV11;

public class Runner {

	public static void run(InputStream inputStream, String version) throws Exception {
		interpret(lexAndParse(inputStream, version));
	}

	public static void validate(InputStream inputStream, String version) throws Exception {
		validate(lexAndParse(inputStream, version));
	}

	public static void lint(InputStream inputStream, String version, Map<String, String> config) throws Exception {
		ASTNode ast = lexAndParse(inputStream, version);
		boolean isV10 = version.equals("1.0");
		lint(ast, isV10, config);
	}

	public static void format(InputStream inputStream, String version, String config) throws Exception {
		ASTNode ast = lexAndParse(inputStream, version);
		format(ast, config);
	}

	private static ASTNode lexAndParse(InputStream inputStream, String version) throws Exception {
		boolean is10 = version.equals("1.0");
		List<Token> tokens = lex(inputStream, is10);
		return parse(tokens, is10);
	}

	private static List<Token> lex(InputStream inputStream, boolean isV10) throws Exception {
		StreamReader reader = new StreamReader(inputStream);

		Lexer lexer = isV10 ? provideV10(reader) : provideV11(reader);
		List<Token> tokens = new ArrayList<>();

		while (lexer.hasNext()) {
			Token token = lexer.getNext();
			tokens.add(token);
		}
		return tokens;
	}

	private static ASTNode parse(List<Token> tokens, boolean isV10) throws Exception {
		Parser parser = isV10 ? ParserProvider.provide10(tokens) : ParserProvider.provide11(tokens);
		Try<ASTNode, Exception> possibleAst = parser.parseExpression();

		if (possibleAst.isFail()) {
			throw possibleAst.getFail().get();
		}
		return possibleAst.getSuccess().get();
	}

	private static void interpret(ASTNode ast) throws Exception {
		Interpreter interpreter = new Interpreter();
		interpreter.visit(ast);
	}

	private static void validate(ASTNode ast) throws Exception {
		Interpreter interpreter = new Interpreter();
		interpreter.validate(ast);
	}

	private static void lint(ASTNode ast, boolean isV10, Map<String, String> config) throws Exception {
		Linter linter = isV10 ? LinterProvider.getLinterV10() : LinterProvider.getLinterV11();
		Report report = linter.analyze((Program) ast, config);
		for (ReportLine reportLine : report.getReportLines()) {
			System.out.println(reportLine.errorMessage() + " on " + reportLine.position().toString());
		}
	}

	private static void format(ASTNode ast, String config) throws Exception {
		Map<String, Rule> rules = JsonReader.readRulesFromJson(config);
		Formatter formatter = new Formatter(rules);
		System.out.println(formatter.format((Program) ast));
	}
}
