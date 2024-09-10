package org.example;

import org.token.Position;

public class Literal<T extends Comparable<T>> implements Expression{

	private final T value;
	private final Position position;

	public Literal(T value, Position position) {
		this.value = value;
		this.position = position;
	}

	public T getValue(){
		return this.value;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		switch (this) {
			case TextLiteral textLiteral -> visitor.visit(textLiteral);
			case NumericLiteral numericLiteral -> visitor.visit(numericLiteral);
			case BooleanLiteral booleanLiteral -> visitor.visit(booleanLiteral);
			default -> {
			}
		}
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Position getPosition() {
		return position;
	}
}
