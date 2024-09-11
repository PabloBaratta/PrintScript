package org.example;

import org.token.Position;

public class TextLiteral extends Literal<String> {

	public TextLiteral(String value, Position position) {
		super(value, position);
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}
