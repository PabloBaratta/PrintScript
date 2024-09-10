package org.example.lexer;




import org.token.NativeTokenTypes;
import org.token.TokenType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PrintScriptTokenConfig {

	public static Map<Pattern, TokenType> keywordTokenTypeMapV10() {

		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.LET,
				NativeTokenTypes.STRING_TYPE,
				NativeTokenTypes.NUMBER_TYPE,
				NativeTokenTypes.PRINTLN};

		return getMapFromArray(typeArray);
	}

	public static Map<Pattern, TokenType> keywordTokenTypeMapV11() {

		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.LET,
				NativeTokenTypes.CONST,
				NativeTokenTypes.STRING_TYPE,
				NativeTokenTypes.NUMBER_TYPE,
				NativeTokenTypes.BOOLEAN_TYPE,
				NativeTokenTypes.PRINTLN,
				NativeTokenTypes.READINPUT,
				NativeTokenTypes.READENV,
				NativeTokenTypes.IF,
				NativeTokenTypes.ELSE};

		return getMapFromArray(typeArray);
	}

	public static Map<Pattern, TokenType> operatorTokenTypeMap() {

		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.EQUALS,
				NativeTokenTypes.PLUS,
				NativeTokenTypes.MINUS,
				NativeTokenTypes.ASTERISK,
				NativeTokenTypes.SLASH};

		return getMapFromArray(typeArray);
	}

	public static Map<Pattern, TokenType> separatorTokenTypeMapV10() {

		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.COMMA,
				NativeTokenTypes.COLON,
				NativeTokenTypes.SEMICOLON,
				NativeTokenTypes.LEFT_PARENTHESIS,
				NativeTokenTypes.RIGHT_PARENTHESES,
		};

		return getMapFromArray(typeArray);
	}

	public static Map<Pattern, TokenType> separatorTokenTypeMapV11() {

		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.COMMA,
				NativeTokenTypes.COLON,
				NativeTokenTypes.SEMICOLON,
				NativeTokenTypes.LEFT_PARENTHESIS,
				NativeTokenTypes.RIGHT_PARENTHESES,
				NativeTokenTypes.LEFT_BRACE,
				NativeTokenTypes.RIGHT_BRACE,
		};

		return getMapFromArray(typeArray);
	}

	public static Map<Pattern, TokenType> literalTokenTypeMapV10() {
		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.NUMBER,
				NativeTokenTypes.STRING,
				NativeTokenTypes.IDENTIFIER,
		};

		return getMapFromArray(typeArray);
	}

	public static Map<Pattern, TokenType> literalTokenTypeMapV11() {
		NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
				NativeTokenTypes.NUMBER,
				NativeTokenTypes.STRING,
				NativeTokenTypes.BOOLEAN,
				NativeTokenTypes.IDENTIFIER
		};

		return getMapFromArray(typeArray);
	}

	private static LinkedHashMap<Pattern, TokenType> getMapFromArray(NativeTokenTypes[] a) {
		LinkedHashMap<Pattern, TokenType> map = new LinkedHashMap<>();

		Arrays.stream(a).forEach(x -> map.put(x.getRegex(), x.toTokenType()));
		return map;
	}

}
