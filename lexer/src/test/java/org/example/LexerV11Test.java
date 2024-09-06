package org.example;

import org.example.lexer.*;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LexerV11Test {

	private final List<Character> whiteSpaces = Arrays.asList(' ', '\t');

	@Test
	void testHasNext() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let my_variable;".getBytes());
		Lexer lexerWithTokens = LexerProvider.provideV11(new StreamReader(inputStream));
		assertTrue(lexerWithTokens.hasNext());

		lexerWithTokens.getNext();
		assertTrue(lexerWithTokens.hasNext());

		lexerWithTokens.getNext();
		lexerWithTokens.getNext();
		assertFalse(lexerWithTokens.hasNext());

		InputStream emptyInputStream = new ByteArrayInputStream("".getBytes());
		Lexer emptyLexer = LexerProvider.provideV11(new StreamReader(emptyInputStream));
		assertFalse(emptyLexer.hasNext());
	}

	@Test
	void testSingleLineInput() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let x = 10;".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
	}

	@Test
	void testMultiLineInput() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let x = 10;\nlet y = 20;".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
	}

	@Test
	void testInputWithWhitespace() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let    x\t=\t10 ;".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
	}

	@Test
	void testInputWithKeywordsAndIdentifiers() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("if (true) { let y = x; }".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
	}

	@Test
	void testReadEnvFunctionWithWhitespace() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let    envVar\t=\treadEnv() ;".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
	}
}
