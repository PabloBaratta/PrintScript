package org.example;

import org.example.lexer.token.Position;

public class ConstDeclaration implements ASTNode{
	private final Identifier identifier;
	private final Type type;
	private final Expression expression;

	public ConstDeclaration(Identifier id, Type type, Expression ex) {
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

	public Expression getExpression() {
		return expression;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		//visitor.visit(this);
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
