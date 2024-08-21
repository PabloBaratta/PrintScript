package org.example;

import org.example.lexer.token.Position;

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
		if (this instanceof TextLiteral){
			visitor.visit((TextLiteral) this);
		} else if (this instanceof NumericLiteral){
			visitor.visit((NumericLiteral) this);
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
