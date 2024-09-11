package org.example;

import org.token.Position;

public class BooleanLiteral extends Literal<Boolean> {

	public BooleanLiteral(Boolean value, Position position) {
		super(value, position);
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}
