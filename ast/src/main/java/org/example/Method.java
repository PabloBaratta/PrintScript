package org.example;

import org.example.lexer.token.Position;

import java.util.List;

public class Method implements Expression{

	private Identifier identifier;
	private List<Expression> arguments;

	public Method(Identifier identifier, List<Expression> arguments) {
		this.identifier = identifier;
		this.arguments = arguments;
	}

	public Identifier getVariable() {
		return identifier;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	// terminar de ver como printear
	@Override
	public String toString() {
		return identifier.toString() + "(" + arguments.toString() + ")";
	}

	@Override
	public Position getPosition() {
		return identifier.getPosition();
	}
}
