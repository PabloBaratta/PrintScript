package org.example;

import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.PrintScriptTokenConfig;
import org.token.NativeTokenTypes;
import org.token.Position;
import org.token.Token;
import org.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TokenConstructorTest {


	TokenConstructor keysV10 = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMapV10());
	TokenConstructor keysV11 = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMapV11());
	TokenConstructor operatorConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap());

	TokenConstructor sepConstrV10;

	{
		Map<Pattern, TokenType> map = PrintScriptTokenConfig.separatorTokenTypeMapV10();
		sepConstrV10 = new TokenConstructorImpl(map);
	}

	TokenConstructor sepConstrV11;

	{
		Map<Pattern, TokenType> map = PrintScriptTokenConfig.separatorTokenTypeMapV11();
		sepConstrV11 = new TokenConstructorImpl(map);
	}


	TokenConstructor literalsV10 = new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMapV10());
	TokenConstructor literalsV11 = new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMapV11());

	@Test
	void keywordConstructorLetTest() {
		String input = "let a : string = \"string\"";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "let";
		TokenType tokenType = NativeTokenTypes.LET.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV10, line, column);
	}

	@Test
	void keywordConstructorStringTest() {
		String input = "let a : string = \"string\"";
		int offset = 8;
		int line = 0;
		int column = 0;
		String associatedString = "string";
		TokenType tokenType = NativeTokenTypes.STRING_TYPE.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV10, line, column);
	}

	@Test
	void keywordConstructorNumberTest() {
		String input = "let a : number = 5";
		int offset = 8;
		int line = 0;
		String associatedString = "number";
		int column = 0;
		TokenType tokenType = NativeTokenTypes.NUMBER_TYPE.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV10, line, column);
	}

	@Test
	void operatorConstructorPlusTest() {
		String input = "let a : number = 5 + 5";
		int offset = 19;
		int line = 0;
		int column = 0;
		String associatedString = "+";
		TokenType tokenType = NativeTokenTypes.PLUS.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line, column);
		String input2 = "let a : number = 5 +5";
		tokenAssertionMethod(input2, offset, expectedToken, operatorConstructor, line, column);
	}

	@Test
	void operatorConstructorMinusTest() {
		String input = "let a : number = 5 - 5";
		int offset = 19;
		int line = 0;
		int column = 0;
		String associatedString = "-";
		TokenType tokenType = NativeTokenTypes.MINUS.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line, column);
		String input2 = "let a : number = 5 -5";
		tokenAssertionMethod(input2, offset, expectedToken, operatorConstructor, line, column);
	}

	@Test
	void operatorConstructorMultiplicationTest() {
		String input = "let a : number = 5 * 5";
		int offset = 19;
		int line = 0;
		int column = 0;
		String associatedString = "*";
		TokenType tokenType = NativeTokenTypes.ASTERISK.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line, column);
		String input2 = "let a : number = 5 *5";
		tokenAssertionMethod(input2, offset, expectedToken, operatorConstructor, line, column);
	}

	@Test
	void operatorConstructorSlashTest() {
		String input = "let a : number = 5 / 5";
		int offset = 19;
		int line = 0;
		int column = 0;
		String associatedString = "/";
		TokenType tokenType = NativeTokenTypes.SLASH.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line, column);
		String input2 = "let a : number = 5 /5";
		tokenAssertionMethod(input2, offset, expectedToken, operatorConstructor, line, column);
	}

	@Test
	void separatorConstructorSemicolonTest() {
		String input = "let a = 5;";
		int offset = 9;
		int line = 0;
		int column = 0;
		String associatedString = ";";
		TokenType tokenType = NativeTokenTypes.SEMICOLON.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConstrV10, line, column);
	}
	@Test
	void literalconstructor() {
		String input = "let a : number = 5 / 5";
		int offset = 17;
		int line = 0;
		int column = 0;
		String associatedString = "5";
		TokenType tokenType = NativeTokenTypes.NUMBER.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, literalsV10, line, column);
		String withoutSp = "let a : number = 5 /5";
		tokenAssertionMethod(withoutSp, offset, expectedToken, literalsV10, line, column);
	}



	@Test
	void separatorConstructorLeftParenthesisTest() {
		String input = "println(";
		int offset = 7;
		int line = 0;
		int column = 0;
		String associatedString = "(";
		TokenType tokenType = NativeTokenTypes.LEFT_PARENTHESIS.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConstrV10, line, column);
	}

	@Test
	void separatorConstructorRightParenthesisTest() {
		String input = "println(a)";
		int offset = 9;
		int line = 0;
		int column = 0;
		String associatedString = ")";
		TokenType tokenType = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConstrV10, line, column);
	}

	@Test
	void separatorConstructorCommaTest() {
		String input = "let a, b = 5";
		int offset = 5;
		int line = 0;
		int column = 0;
		String associatedString = ",";
		TokenType tokenType = NativeTokenTypes.COMMA.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConstrV10, line, column);
	}

	@Test
	void separatorConstructorColonTest() {
		String input = "let a: number";
		int offset = 5;
		int line = 0;
		int column = 0;
		String associatedString = ":";
		TokenType tokenType = NativeTokenTypes.COLON.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConstrV10, line, column);
	}

	@Test
	void testConst(){
		String input = "const a : number = 5";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "const";
		TokenType tokenType = NativeTokenTypes.CONST.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV11, line, column);
	}

	@Test
	void testBoolean(){
		String input = "let a : boolean = true";
		int offset = 8;
		int line = 0;
		int column = 0;
		String associatedString = "boolean";
		String associatedString2 = "true";
		TokenType tokenType = NativeTokenTypes.BOOLEAN_TYPE.toTokenType();
		TokenType tokenType2 = NativeTokenTypes.BOOLEAN.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Position position2 = new Position(18, associatedString2.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		Token expectedToken2 = new Token(tokenType2, associatedString2, position2);
		tokenAssertionMethod(input, offset, expectedToken, keysV11, line, column);
		tokenAssertionMethod(input, 18, expectedToken2, literalsV11, line, column);
	}

	@Test
	void testIf(){
		String input = "if (a)";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "if";
		TokenType tokenType = NativeTokenTypes.IF.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV11, line, column);
	}

	@Test
	void testElse(){
		String input = "else";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "else";
		TokenType tokenType = NativeTokenTypes.ELSE.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV11, line, column);
	}

	@Test
	void testReadInput(){
		String input = "readInput()";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "readInput";
		TokenType tokenType = NativeTokenTypes.READINPUT.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV11, line, column);
	}

	@Test
	void testReadEnv(){
		String input = "readEnv()";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "readEnv";
		TokenType tokenType = NativeTokenTypes.READENV.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, keysV11, line, column);
	}

	@Test
	void testBraces(){
		String input = "{";
		int offset = 0;
		int line = 0;
		int column = 0;
		String associatedString = "{";
		TokenType tokenType = NativeTokenTypes.LEFT_BRACE.toTokenType();
		Position position = new Position(offset, associatedString.length(), line, column);
		Token expectedToken = new Token(tokenType, associatedString, position);
		tokenAssertionMethod(input, offset, expectedToken, sepConstrV11, line, column);
	}

	private void tokenAssertionMethod(String input, int offset, Token token,
									TokenConstructor constr, int line, int column) {

		Optional<Token> optionalToken = constr.constructToken(input.substring(offset), offset, line, column);

		assertTrue(optionalToken.isPresent());

		Token actualToken = optionalToken.get();

		assertEquals(token.type(), actualToken.type());
		assertEquals(token.associatedString(), actualToken.associatedString());
		assertEquals(token.position().getOffset(), actualToken.position().getOffset());
		assertEquals(token.position().getLength(), actualToken.position().getLength());
		assertEquals(token.position().getLine(), actualToken.position().getLine());
	}



}
