package org.example;

import org.example.lexer.token.Position;

public class BooleanLiteral extends Literal<Boolean> {

	public BooleanLiteral(Boolean value, Position position) {
		super(value, position);
	}
}
