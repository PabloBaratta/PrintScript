package org.example.lexer.token;

import org.example.lexer.token.TokenType;


public record Token(TokenType type,
                    String associatedString, Position position) {

    @Override
    public String toString() {
        return "Token " +
                "type=" + type +
                ", associatedString='" + associatedString + '\'' +
                ", offset=" + position.getOffset() +
                ", length=" + position.getLength() +
                ", line=" + position.getLine() +
                '}';
    }

    public int length() {
        return position.getLength();
    }
}
