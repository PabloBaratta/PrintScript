package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.Util.*;

public class Runner {

	public static void run(String filePath) throws Exception {
		String code = readFileAsString(filePath);
		List<Token> tokens = lex(code);
		Program ast = parse(tokens);
		interpret(ast);
	}

	public static List<Token> lex(String code) throws Exception {
		Lexer lexer = createLexer(code);
		List<Token> tokens = new ArrayList<>();

		while (lexer.hasNext()){
			Try<Token, Exception> possibleToken = lexer.getNext();
			if (possibleToken.isFail()){
				throw possibleToken.getFail().get();
			}
			else {
				tokens.add(possibleToken.getSuccess().get());
			}
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

	public static void interpret(Program ast) throws Exception {
		Interpreter interpreter = createInterpreter();
		interpreter.visit(ast);
	}

	private static String readFileAsString(String filePath) throws IOException {
		return Files.lines(Paths.get(filePath))
				.collect(Collectors.joining("\n"));
	}

}
