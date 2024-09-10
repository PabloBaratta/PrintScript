package org.example;

import org.token.Position;

public class Parenthesis implements Expression{

	private final Expression expression;

	public Parenthesis(Expression expression) {
		this.expression = expression;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	public Expression getExpression(){
		return expression;
	}

	@Override
	public String toString() {
		return "(" + expression.toString() + ")";
	}

	@Override
	public Position getPosition() {
		return expression.getPosition();
	}


}
