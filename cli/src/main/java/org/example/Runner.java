package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.StreamReader;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.lexer.LexerProvider.provideV10;
import static org.example.lexer.LexerProvider.provideV11;
import static org.example.resources.Util.*;

public class Runner {

	public static void run(String filePath) throws Exception {
		String code = readFileAsString(filePath);
		InputStream inputStream = new ByteArrayInputStream(code.getBytes());
		StreamReader reader = new StreamReader(inputStream);
		List<Token> tokens = lex(reader);
		Program ast = parse(tokens);
		interpret(ast);
	}

	public static List<Token> lex(Iterator<String> reader) throws Exception {
		Lexer lexer = provideV10(reader);
		List<Token> tokens = new ArrayList<>();

		while (lexer.hasNext()){
			Token token = lexer.getNext();
			tokens.add(token);
		}
		return tokens;
	}

	public static List<Token> lexV11(Iterator<String> code) throws Exception {
		Lexer lexer = provideV11(code);
		List<Token> tokens = new ArrayList<>();

		while (lexer.hasNext()){
			Token token = lexer.getNext();
			tokens.add(token);
		}
		return tokens;
	}

	public static Program parse(List<Token> tokens) throws Exception {
		Parser parser = createParser(tokens);
		Try<ASTNode, Exception> possibleAst = parser.parseExpression();
		if (possibleAst.isFail()){
			throw possibleAst.getFail().get();
		}
		return (Program) possibleAst.getSuccess().get();
	}

	public static Program parseV11(List<Token> tokens) throws Exception {
		Parser parser = ParserProvider.provide11(tokens);
		Try<ASTNode, Exception> possibleAst = parser.parseExpression();
		if (possibleAst.isFail()){
			throw possibleAst.getFail().get();
		}
		return (Program) possibleAst.getSuccess().get();
	}
	public static void interpret(Program ast) throws Exception {
		Interpreter interpreter = createInterpreter();
		interpreter.visit(ast);
	}

	private static String readFileAsString(String filePath) throws IOException {
		return Files.lines(Paths.get(filePath))
				.collect(Collectors.joining("\n"));
	}

}
