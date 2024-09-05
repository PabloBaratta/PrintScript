package org.example;

import java.util.Optional;

public class Variable {

	private Type type;
	private Optional<Literal> literal;
	private Boolean isConst;

	public Variable(Type type, Optional<Literal> expression, Boolean isConst){
		this.type = type;
		this.literal = expression;
		this.isConst = isConst;
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

	public Boolean isConst() {
		return isConst;
	}

	public void setLiteral(Literal literal) {
		this.literal = Optional.ofNullable(literal);
	}
}
