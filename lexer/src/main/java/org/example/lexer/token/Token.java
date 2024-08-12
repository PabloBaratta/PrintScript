package org.example.lexer.token;

import org.example.TokenType;

public record Token(TokenType type,
                    String associatedString,
                    int offset,
                    int length) {

    @Override
    public String toString() {
        return "Token " +
                "type=" + type +
                ", associatedString='" + associatedString + '\'' +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }

}
