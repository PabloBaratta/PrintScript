package org.example.lexer.token;

import java.util.regex.Pattern;

public enum NativeTokenTypes {
	// Separator
	SEMICOLON (";"), LEFT_PARENTHESIS("\\("), RIGHT_PARENTHESES("\\)"), COMMA(","),
	COLON(":"), EOF(""),

	// Operator
	EQUALS("="), PLUS("\\+"), MINUS("-"), ASTERISK("\\*"), SLASH("\\/"),

	// Values
	IDENTIFIER("[a-z_]+[a-zA-Z0-9_]*"),
	STRING("(\"[^\"]*\"|'[^']*')"),
	NUMBER("\\b\\d+(\\.\\d+)?\\b"),

	// KEYWORDS
	// Variable declaration
	LET("let\\b"),
	// Type keyword
	STRING_TYPE("string\\b"), NUMBER_TYPE("number\\b"), PRINTLN("println\\b");

	private final String regex;

	NativeTokenTypes(String regex){
		this.regex = regex;
	}

	public TokenType toTokenType(){
		return new TokenType(this.toString());
	}

	public Pattern getRegex() {
		return Pattern.compile("^" + regex);
	}
}
