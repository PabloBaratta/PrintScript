package org.example.util;

import org.example.ASTVisitor;
import org.example.Literal;
import org.token.Position;

public class WildcardLiteral extends Literal<Wildcard> {
	public WildcardLiteral(Wildcard value, Position position) {
		super(value, position);
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {

	}
}
