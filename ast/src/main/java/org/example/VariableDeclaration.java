package org.example;

import org.token.Position;

import java.util.Optional;

public class VariableDeclaration implements ASTNode {

	private final Identifier identifier;
	private final Type type;
	private final Optional<Expression> expression;

	public VariableDeclaration(Identifier id, Type type, Optional<Expression> ex) {
		this.identifier = id;
		this.type = type;
		this.expression = ex;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Type getType() {
		return type;
	}

	public Optional<Expression> getExpression() {
		return expression;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return identifier.toString() + " : " + type.toString() + ";\n";
	}

	@Override
	public Position getPosition() {
		return identifier.getPosition();
	}
}
