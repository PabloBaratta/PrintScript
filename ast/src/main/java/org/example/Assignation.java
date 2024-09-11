package org.example;


import org.token.Position;

public class Assignation implements ASTNode {

	private final Identifier identifier;
	private final Expression expression;
	private final Position position;

	public Assignation(Identifier identifier, Expression expression, Position position) {
		this.identifier = identifier;
		this.expression = expression;
		this.position = position;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return identifier.toString() + " = " + expression.toString() + ";\n";
	}

	@Override
	public Position getPosition() {
		return position;
	}
}
