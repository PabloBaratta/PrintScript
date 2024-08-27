package org.example;

import org.example.lexer.Lexer;
import org.example.lexer.NoMoreTokensAvailableException;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

	private final List<Character> whiteSpaces = Arrays.asList(' ', '\t', '\n');

	@Test
	public void testHasNext() {
		TokenConstructor keyConst = new TokenConstructorImpl(Map.of(
				NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
		));

		Collection<TokenConstructor> tokenConst = List.of(new TokenConstructorImpl(Map.of(
				NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType(),
				NativeTokenTypes.SEMICOLON.getRegex(), NativeTokenTypes.SEMICOLON.toTokenType()
		)));

		Lexer lexerWithTokens = new Lexer("let my_variable;", tokenConst, keyConst, whiteSpaces);
		assertTrue(lexerWithTokens.hasNext());

		lexerWithTokens.getNext();
		assertTrue(lexerWithTokens.hasNext());

		lexerWithTokens.getNext();
		lexerWithTokens.getNext();
		assertFalse(lexerWithTokens.hasNext());

		Lexer emptyLexer = new Lexer("", tokenConst, keyConst, whiteSpaces);
		assertFalse(emptyLexer.hasNext());
	}

	@Test
	public void testKeywordToken() {
		TokenConstructorImpl keywordConstructor = new TokenConstructorImpl(Map.of(
				NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
		));

		Lexer lexer = new Lexer("let a = 5", Collections.singletonList(new TokenConstructorImpl(Map.of())),
				keywordConstructor, whiteSpaces);

		Try<Token, Exception> result = lexer.getNext();

		assertTrue(result.isSuccess());
		Token token = result.getSuccess().orElseThrow();

		assertEquals(NativeTokenTypes.LET.toTokenType(), token.type(), "Expected token: KEYWORD");

		assertEquals("let", token.associatedString(), "Expected associated string: 'let'");
	}

	@Test
	public void testNoMoreTokens() {
		TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
				NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
		));

		Lexer lexer = new Lexer("", Collections.singletonList(new TokenConstructorImpl(Map.of())),
				keywordConstructor, whiteSpaces);

		Try<Token, Exception> result = lexer.getNext();

		assertTrue(result.isFail());
		assertTrue(result.getFail().get() instanceof NoMoreTokensAvailableException);
	}

	@Test
	public void testKeywordAndIdentifier() {
		Pattern regex = NativeTokenTypes.LET.getRegex();
		TokenType tokenType = NativeTokenTypes.LET.toTokenType();
		TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
				regex, tokenType
		));

		Lexer lexer = new Lexer("let letter", Collections.singletonList(new TokenConstructorImpl(Map.of(
				NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType()
		))), keywordConstructor, whiteSpaces);

		Try<Token, Exception> result1 = lexer.getNext();
		assertTrue(result1.isSuccess());
		Token token1 = result1.getSuccess().orElseThrow();
		assertEquals(tokenType, token1.type(), "Expected token type: KEYWORD");
		assertEquals("let", token1.associatedString(), "Expected associated string: 'let'");

		Try<Token, Exception> result2 = lexer.getNext();
		assertTrue(result2.isSuccess());
		Token token2 = result2.getSuccess().orElseThrow();
		String message = "Expected token type: IDENTIFIER";
		assertEquals(NativeTokenTypes.IDENTIFIER.toTokenType(), token2.type(), message);
		assertEquals("letter", token2.associatedString(), "Expected associated string: 'letter'");
	}

	@Test
	public void testVariableDeclaration() {
		TokenType strType = NativeTokenTypes.STRING_TYPE.toTokenType();
		TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
				NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType(),
				NativeTokenTypes.STRING_TYPE.getRegex(), strType
		));

		TokenType str = NativeTokenTypes.STRING.toTokenType();
		Collection<TokenConstructor> tokenConstructors = List.of(new TokenConstructorImpl(Map.of(
				NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType(),
				NativeTokenTypes.EQUALS.getRegex(), NativeTokenTypes.EQUALS.toTokenType(),
				NativeTokenTypes.STRING.getRegex(), str,
				NativeTokenTypes.SEMICOLON.getRegex(), NativeTokenTypes.SEMICOLON.toTokenType(),
				NativeTokenTypes.COLON.getRegex(), NativeTokenTypes.COLON.toTokenType()
		)));

		Lexer lexer = new Lexer("let my_cool_variable: string = \"ciclon\";", tokenConstructors,
				keywordConstructor, whiteSpaces);

		String var = "my_cool_variable";
		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.LET.toTokenType(), "let", new Position(0, 3, 1, 1)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), var, new Position(4, 16, 1, 5)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(20, 1, 1, 21)),
				new Token(strType, "string", new Position(22, 6, 1, 23)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(29, 1, 1, 30)),
				new Token(str, "\"ciclon\"", new Position(31, 8, 1, 32)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(39, 1, 1, 40))
		);

		List<Token> actualTokens = new ArrayList<>();
		Try<Token, Exception> result;

		while (lexer.hasNext()) {
			result = lexer.getNext();
			if (result.isSuccess()) {
				actualTokens.add(result.getSuccess().orElseThrow());
			} else {
				result.getFail().orElseThrow();
			}
		}

		assertEquals(expectedTokens, actualTokens, "Token lists do not match");
	}

	@Test
	public void testPrintln() {
		TokenConstructor keyConst = new TokenConstructorImpl(Map.of(
				NativeTokenTypes.PRINTLN.getRegex(), NativeTokenTypes.PRINTLN.toTokenType()
		));

		TokenType tokenType = NativeTokenTypes.IDENTIFIER.toTokenType();
		TokenType tokenType1 = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		TokenType leftPar = NativeTokenTypes.LEFT_PARENTHESIS.toTokenType();
		Collection<TokenConstructor> tokenConst = List.of(new TokenConstructorImpl(Map.of(
				NativeTokenTypes.IDENTIFIER.getRegex(),
				tokenType,
				NativeTokenTypes.EQUALS.getRegex(),
				NativeTokenTypes.EQUALS.toTokenType(),
				NativeTokenTypes.STRING.getRegex(),
				NativeTokenTypes.STRING.toTokenType(),
				NativeTokenTypes.SEMICOLON.getRegex(),
				NativeTokenTypes.SEMICOLON.toTokenType(),
				NativeTokenTypes.LEFT_PARENTHESIS.getRegex(),
				leftPar,
				NativeTokenTypes.RIGHT_PARENTHESES.getRegex(),
				tokenType1,
				NativeTokenTypes.COLON.getRegex(),
				NativeTokenTypes.COLON.toTokenType()
		)));

		List<Character> whiteSpaces = Arrays.asList(' ', '\t', '\n');
		String code = "println(my_cool_variable);";
		Lexer lexer = new Lexer(code, tokenConst, keyConst, whiteSpaces);

		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.PRINTLN.toTokenType(), "println", new Position(0, 7, 1, 1)),
				new Token(leftPar, "(", new Position(7, 1, 1, 8)),
				new Token(tokenType, "my_cool_variable", new Position(8, 16, 1, 9)),
				new Token(tokenType1, ")", new Position(24, 1, 1, 25)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(25, 1, 1, 26))
		);

		List<Token> actualTokens = new ArrayList<>();
		Try<Token, Exception> result;

		while (lexer.hasNext()) {
			result = lexer.getNext();
			if (result.isSuccess()) {
				actualTokens.add(result.getSuccess().orElseThrow());
			} else {
				result.getFail().orElseThrow();
			}
		}

		assertEquals(expectedTokens, actualTokens, "Token lists do not match");
	}
}
