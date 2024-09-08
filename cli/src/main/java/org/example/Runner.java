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
import java.util.LinkedList;
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
		Lexer lexer = provideV10(reader);
		Program ast = parse(lexer);
		interpret(ast);
	}


	public static Program parse(PrintScriptIterator<Token> tokens) throws Exception {
		Parser parser = createParser(tokens);
        List<ASTNode> nodes = new LinkedList<>();
		while (parser.hasNext()){
            nodes.add(parser.getNext());
        }
		return new Program(nodes);
	}

	public static Program parseV11(PrintScriptIterator<Token> tokens) throws Exception {
		Parser parser = ParserProvider.provide11(tokens);
        List<ASTNode> nodes = new LinkedList<>();
        while (parser.hasNext()){
            nodes.add(parser.getNext());
        }
        return new Program(nodes);
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
