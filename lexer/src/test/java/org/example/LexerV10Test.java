package org.example;

import org.example.lexer.*;
import org.token.Token;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LexerV10Test {

	private final List<Character> whiteSpaces = Arrays.asList(' ', '\t');

	@Test
	void testHasNext() throws Exception {

		InputStream inputStream = new ByteArrayInputStream("let my_variable;".getBytes());
		Lexer lexerWithTokens = LexerProvider.provideV10(new StreamReader(inputStream));
		assertTrue(lexerWithTokens.hasNext());

		lexerWithTokens.getNext();
		assertTrue(lexerWithTokens.hasNext());

		lexerWithTokens.getNext();
		lexerWithTokens.getNext();
		assertFalse(lexerWithTokens.hasNext());

		InputStream emptyInputStream = new ByteArrayInputStream("".getBytes());
		Lexer emptyLexer = LexerProvider.provideV10(new StreamReader(emptyInputStream));
		assertFalse(emptyLexer.hasNext());
	}

	@Test
	void testSingleLineInput() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let x = 10;".getBytes());
		Lexer lexer = LexerProvider.provideV10(new StreamReader(inputStream));
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
		Lexer lexer = LexerProvider.provideV10(new StreamReader(inputStream));
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
		Lexer lexer = LexerProvider.provideV10(new StreamReader(inputStream));
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
		assertNotNull(lexer.getNext());
		assertTrue(lexer.hasNext());
	}

	@Test
	void testInputWithMultipleLinesAndWhitespace() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let    x\t=\t10 ;\nlet y = 20;".getBytes());
		Lexer lexer = LexerProvider.provideV10(new StreamReader(inputStream));
		List<Token> tokens = new ArrayList<>();
		while (lexer.hasNext()) {
			tokens.add(lexer.getNext());
		}
		assertEquals(10, tokens.size());
	}


}
