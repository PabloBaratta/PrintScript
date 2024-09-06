
package org.example;

import org.example.lexer.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
/*
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

		assertTest(lexer, expectedTokens);
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

		assertTest(lexer, expectedTokens);
	}
*/
	@Test
	public void testConst() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("const a: number = 5;".getBytes());
        Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		TokenType num = NativeTokenTypes.NUMBER_TYPE.toTokenType();
		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.CONST.toTokenType(), "const", new Position(0, 5, 1, 1)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "a", new Position(6, 1, 1, 7)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(7, 1, 1, 8)),
				new Token(num, "number", new Position(9, 6, 1, 10)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(16, 1, 1, 17)),
				new Token(NativeTokenTypes.NUMBER.toTokenType(), "5", new Position(18, 1, 1, 19)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(19, 1, 1, 20))
		);

		assertTest(lexer, expectedTokens);
	}



	@Test
	public void testBooleans() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("let a: boolean = true;".getBytes());
        Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		TokenType bool = NativeTokenTypes.BOOLEAN_TYPE.toTokenType();
		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.LET.toTokenType(), "let", new Position(0, 3, 1, 1)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "a", new Position(4, 1, 1, 5)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(5, 1, 1, 6)),
				new Token(bool, "boolean", new Position(7, 7, 1, 8)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(15, 1, 1, 16)),
				new Token(NativeTokenTypes.BOOLEAN.toTokenType(), "true", new Position(17, 4, 1, 18)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(21, 1, 1, 22))
		);

		assertTest(lexer, expectedTokens);
	}

	@Test
	public void testIfElse() throws Exception {

		String code = "if (a) { let b: string = \"hello\"; } else { let b: string = \"world\"; }";
        InputStream inputStream = new ByteArrayInputStream(code.getBytes());
        Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		TokenType leftPar = NativeTokenTypes.LEFT_PARENTHESIS.toTokenType();
		TokenType rightPar = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		TokenType str = NativeTokenTypes.STRING_TYPE.toTokenType();
		TokenType rigthBr = NativeTokenTypes.RIGHT_BRACE.toTokenType();
		TokenType string = NativeTokenTypes.STRING.toTokenType();
		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.IF.toTokenType(), "if", new Position(0, 2, 1, 1)),
				new Token(leftPar, "(", new Position(3, 1, 1, 4)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "a", new Position(4, 1, 1, 5)),
				new Token(rightPar, ")", new Position(5, 1, 1, 6)),
				new Token(NativeTokenTypes.LEFT_BRACE.toTokenType(), "{", new Position(7, 1, 1, 8)),
				new Token(NativeTokenTypes.LET.toTokenType(), "let", new Position(9, 3, 1, 10)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "b", new Position(13, 1, 1, 14)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(14, 1, 1, 15)),
				new Token(str, "string", new Position(16, 6, 1, 17)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(23, 1, 1, 24)),
				new Token(string, "\"hello\"", new Position(25, 7, 1, 26)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(32, 1, 1, 33)),
				new Token(rigthBr, "}", new Position(34, 1, 1, 35)),
				new Token(NativeTokenTypes.ELSE.toTokenType(), "else", new Position(36, 4, 1, 37)),
				new Token(NativeTokenTypes.LEFT_BRACE.toTokenType(), "{", new Position(41, 1, 1, 42)),
				new Token(NativeTokenTypes.LET.toTokenType(), "let", new Position(43, 3, 1, 44)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "b", new Position(47, 1, 1, 48)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(48, 1, 1, 49)),
				new Token(str, "string", new Position(50, 6, 1, 51)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(57, 1, 1, 58)),
				new Token(string, "\"world\"", new Position(59, 7, 1, 60)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(66, 1, 1, 67)),
				new Token(rigthBr, "}", new Position(68, 1, 1, 69))
		);
		assertTest(lexer, expectedTokens);
	}

	@Test
	public void testReadInput() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("let a: string = readInput();".getBytes());
        Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		TokenType str = NativeTokenTypes.STRING_TYPE.toTokenType();
		TokenType read = NativeTokenTypes.READINPUT.toTokenType();
		TokenType leftPar = NativeTokenTypes.LEFT_PARENTHESIS.toTokenType();
		TokenType rightPar = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.LET.toTokenType(), "let", new Position(0, 3, 1, 1)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "a", new Position(4, 1, 1, 5)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(5, 1, 1, 6)),
				new Token(str, "string", new Position(7, 6, 1, 8)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(14, 1, 1, 15)),
				new Token(read, "readInput", new Position(16, 9, 1, 17)),
				new Token(leftPar, "(", new Position(25, 1, 1, 26)),
				new Token(rightPar, ")", new Position(26, 1, 1, 27)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(27, 1, 1, 28))
		);
		assertTest(lexer, expectedTokens);
	}

	@Test
	public void testReadEnv() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("let a: string = readEnv();".getBytes());
        Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		TokenType str = NativeTokenTypes.STRING_TYPE.toTokenType();
		TokenType leftPar = NativeTokenTypes.LEFT_PARENTHESIS.toTokenType();
		TokenType rightPar = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		TokenType read = NativeTokenTypes.READENV.toTokenType();
		List<Token> expectedTokens = Arrays.asList(
				new Token(NativeTokenTypes.LET.toTokenType(), "let", new Position(0, 3, 1, 1)),
				new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "a", new Position(4, 1, 1, 5)),
				new Token(NativeTokenTypes.COLON.toTokenType(), ":", new Position(5, 1, 1, 6)),
				new Token(str, "string", new Position(7, 6, 1, 8)),
				new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", new Position(14, 1, 1, 15)),
				new Token(read, "readEnv", new Position(16, 7, 1, 17)),
				new Token(leftPar, "(", new Position(23, 1, 1, 24)),
				new Token(rightPar, ")", new Position(24, 1, 1, 25)),
				new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", new Position(25, 1, 1, 26))
		);
		assertTest(lexer, expectedTokens);
	}

	private static void assertTest(Lexer lexer, List<Token> expectedTokens) throws Exception {
		List<Token> actualTokens = new ArrayList<>();
		Token result;

		while (lexer.hasNext()) {
			result = lexer.getNext();
			actualTokens.add(result);

		}

		assertEquals(expectedTokens, actualTokens, "Token lists do not match");
	}

}
