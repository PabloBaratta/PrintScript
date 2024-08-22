package org.example;

import org.example.lexer.token.Position;

public class TextLiteral extends Literal<String> {

    public TextLiteral(String value, Position position) {
        super(value, position);
    }
}
