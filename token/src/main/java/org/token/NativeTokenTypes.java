package org.token;

import java.util.regex.Pattern;

public enum NativeTokenTypes {

	// Separator
	SEMICOLON (";"), LEFT_PARENTHESIS("\\("), RIGHT_PARENTHESES("\\)"), COMMA(","),
	COLON(":"), EOF(""),
	LEFT_BRACE("\\{"), RIGHT_BRACE("\\}"),

	// Operator
	EQUALS("="), PLUS("\\+"), MINUS("-"), ASTERISK("\\*"), SLASH("\\/"),

	// Values
	IDENTIFIER("[a-z_]+[a-zA-Z0-9_]*"),
	STRING("(\"[^\"]*\"|'[^']*')"),
	NUMBER("\\b\\d+(\\.\\d+)?\\b"),
	BOOLEAN("\\btrue\\b|\\bfalse\\b"),

	// KEYWORDS
	// Variable declaration
	LET("let\\b"), CONST("const\\b"),
	// Type keyword
	STRING_TYPE("string\\b"), NUMBER_TYPE("number\\b"), BOOLEAN_TYPE("boolean\\b"),
	// Methods
	PRINTLN("println\\b"), READINPUT("readInput\\b"), READENV("readEnv\\b"),
	// Control flow
	IF("if\\b"), ELSE("else\\b");

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
