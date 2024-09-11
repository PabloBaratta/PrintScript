package org.example;

import org.token.Position;

public abstract class Literal<T> implements Expression{

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
	public String toString() {
		return value.toString();
	}

	@Override
	public Position getPosition() {
		return position;
	}
}
