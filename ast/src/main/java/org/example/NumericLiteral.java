package org.example;

import org.example.lexer.token.Position;

public class NumericLiteral extends Literal<Double> {

    public NumericLiteral(Double value, Position position) {
        super(value, position);
    }

}
