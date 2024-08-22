package org.example;

import org.example.lexer.token.Position;

public class Identifier implements Expression {

	private final String name;
	private final Position position;

	public Identifier(String name, Position position) {
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public Object getValue() {
		return null;
	}
}
