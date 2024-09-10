package org.example;


import org.token.Position;

public class UnaryExpression implements Expression{

	private Expression argument;
	private String operator;
	private final Position position;

	public UnaryExpression(Expression argument, String operator, Position position){
		this.argument = argument;
		this.operator = operator;
		this.position = position;
	}

	public Expression getArgument() {
		return argument;
	}

	public String getOperator() {
		return operator;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return operator + argument.toString();
	}

	@Override
	public Position getPosition() {
		return position;
	}

}
