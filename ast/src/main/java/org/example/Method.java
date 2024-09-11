package org.example;

import org.token.Position;

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
		return identifier.toString() + "(" + printArgs(arguments) + ")";
	}

	private String printArgs(List<Expression> arguments) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < arguments.size(); i++) {
			result.append(arguments.get(i).toString());
			if (i < arguments.size() - 1) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	@Override
	public Position getPosition() {
		return identifier.getPosition();
	}
}
