package org.example;

public enum NativeTokenTypes {
    // Separator
    SEMICOLON, LEFT_PARENTHESIS, RIGHT_PARENTHESES, COMMA, COLON, EOF,

    // Operator
    EQUALS, PLUS, MINUS, ASTERISK, SLASH,

    // Values
    IDENTIFIER,STRING, NUMBER,

    // KEYWORDS
    // Variable declaration
    LET,
    // Type keyword
    STRING_TYPE, NUMBER_TYPE;

    public TokenType toTokenType(){
        return new TokenType(this.toString());
    }
}
