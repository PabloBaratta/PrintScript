package org.example;

import java.util.regex.Pattern;

public enum NativeTokenTypes {
    // Separator
    SEMICOLON (";\\b"), LEFT_PARENTHESIS("(\\b"), RIGHT_PARENTHESES("(\\b"), COMMA(",\\b"),
    COLON(":\\b"), EOF(""),

    // Operator
    EQUALS("=\\b"), PLUS("+\\b"), MINUS("-\\b"), ASTERISK("*\\b"), SLASH("/\\b"),

    // Values
    IDENTIFIER("[a-z_]+[a-zA-Z0-9_]*"),STRING("(\"[^\"]*\"|'[^']*')"), NUMBER("\\\b\\d+(\\.\\d+)?\\\b"),

    // KEYWORDS
    // Variable declaration
    LET("let\\b"),
    // Type keyword
    STRING_TYPE("string\\b"), NUMBER_TYPE("number\\b");

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
