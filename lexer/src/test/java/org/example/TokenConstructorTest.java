package org.example;

import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TokenConstructorTest {


	TokenConstructor keywordConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMap());
	TokenConstructor operatorConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap());

	TokenConstructor sepConst;

	{
		Map<Pattern, TokenType> map = PrintScriptTokenConfig.separatorTokenTypeMap();
		sepConst = new TokenConstructorImpl(map);
	}

	TokenConstructor literalConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMap());

	@Test
	void keywordConstructorLetTest() {
		String input = "let a : string = \"string\"";
		int offset = 0;
		int line = 0;
		String associatedString = "let";
		TokenType tokenType = NativeTokenTypes.LET.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keywordConstructor, line);
	}

	@Test
	void keywordConstructorStringTest() {
		String input = "let a : string = \"string\"";
		int offset = 8;
		int line = 0;
		String associatedString = "string";
		TokenType tokenType = NativeTokenTypes.STRING_TYPE.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keywordConstructor, line);
	}

	@Test
	void keywordConstructorNumberTest() {
		String input = "let a : number = 5";
		int offset = 8;
		int line = 0;
		String associatedString = "number";
		TokenType tokenType = NativeTokenTypes.NUMBER_TYPE.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keywordConstructor, line);
	}

	@Test
	void operatorConstructorPlusTest() {
		String input = "let a : number = 5 + 5";
		int offset = 19;
		int line = 0;
		String associatedString = "+";
		TokenType tokenType = NativeTokenTypes.PLUS.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
		String inputWithoutSpaceBetween = "let a : number = 5 +5";
		tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
	}

	@Test
	void operatorConstructorMinusTest() {
		String input = "let a : number = 5 - 5";
		int offset = 19;
		int line = 0;
		String associatedString = "-";
		TokenType tokenType = NativeTokenTypes.MINUS.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
		String inputWithoutSpaceBetween = "let a : number = 5 -5";
		tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
	}

	@Test
	void operatorConstructorMultiplicationTest() {
		String input = "let a : number = 5 * 5";
		int offset = 19;
		int line = 0;
		String associatedString = "*";
		TokenType tokenType = NativeTokenTypes.ASTERISK.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
		String inputWithoutSpaceBetween = "let a : number = 5 *5";
		tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
	}

	@Test
	void operatorConstructorSlashTest() {
		String input = "let a : number = 5 / 5";
		int offset = 19;
		int line = 0;
		String associatedString = "/";
		TokenType tokenType = NativeTokenTypes.SLASH.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
		String inputWithoutSpaceBetween = "let a : number = 5 /5";
		tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
	}

	@Test
	void separatorConstructorSemicolonTest() {
		String input = "let a = 5;";
		int offset = 9;
		int line = 0;
		String associatedString = ";";
		TokenType tokenType = NativeTokenTypes.SEMICOLON.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConst, line);
	}
	@Test
	void literalconstructor() {
		String input = "let a : number = 5 / 5";
		int offset = 17;
		int line = 0;
		String associatedString = "5";
		TokenType tokenType = NativeTokenTypes.NUMBER.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, literalConstructor, line);
		String inputWithoutSpaceBetween = "let a : number = 5 /5";
		tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, literalConstructor, line);
	}



	@Test
	void separatorConstructorLeftParenthesisTest() {
		String input = "println(";
		int offset = 7;
		int line = 0;
		String associatedString = "(";
		TokenType tokenType = NativeTokenTypes.LEFT_PARENTHESIS.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConst, line);
	}

	@Test
	void separatorConstructorRightParenthesisTest() {
		String input = "println(a)";
		int offset = 9;
		int line = 0;
		String associatedString = ")";
		TokenType tokenType = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConst, line);
	}

	@Test
	void separatorConstructorCommaTest() {
		String input = "let a, b = 5";
		int offset = 5;
		int line = 0;
		String associatedString = ",";
		TokenType tokenType = NativeTokenTypes.COMMA.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConst, line);
	}

	@Test
	void separatorConstructorColonTest() {
		String input = "let a: number";
		int offset = 5;
		int line = 0;
		String associatedString = ":";
		TokenType tokenType = NativeTokenTypes.COLON.toTokenType();
		Position position = new Position(offset, associatedString.length(), line);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConst, line);
	}

	private void tokenAssertionMethod(String input, int offset, Token expectedToken,
									TokenConstructor constructor, int line) {

		Optional<Token> optionalToken = constructor.constructToken(input.substring(offset), offset, line);

		assertTrue(optionalToken.isPresent());

		Token actualToken = optionalToken.get();

		assertEquals(expectedToken.type(), actualToken.type());
		assertEquals(expectedToken.associatedString(), actualToken.associatedString());
		assertEquals(expectedToken.position().getOffset(), actualToken.position().getOffset());
		assertEquals(expectedToken.position().getLength(), actualToken.position().getLength());
		assertEquals(expectedToken.position().getLine(), actualToken.position().getLine());
	}



}
