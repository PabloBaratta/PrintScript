package org.example.lexer.token;

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
