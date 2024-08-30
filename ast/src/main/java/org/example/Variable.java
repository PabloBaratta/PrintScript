package org.example;

import java.util.Optional;

public class Variable {

	private Type type;
	private Optional<Literal> literal;

	public Variable(Type type, Optional<Literal> expression){
		this.type = type;
		this.literal = expression;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Optional<Literal> getLiteral() {
		return literal;
	}

	public void setLiteral(Literal literal) {
		this.literal = Optional.ofNullable(literal);
	}
}
