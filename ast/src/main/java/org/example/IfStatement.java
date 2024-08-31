package org.example;

import org.example.lexer.token.Position;

import java.util.List;

public class IfStatement implements ASTNode {

	private final BooleanLiteral condition;
	private final List<ASTNode> thenBlock;
	private final List<ASTNode> elseBlock;
	private final Position position;


	public IfStatement(BooleanLiteral cond, List<ASTNode> thenBlock, List<ASTNode> elseBlock, Position position) {
		this.condition = cond;
		this.thenBlock = thenBlock;
		this.elseBlock = elseBlock;
		this.position = position;
	}

	public BooleanLiteral getCondition() {
		return condition;
	}

	public List<ASTNode> getThenBlock() {
		return thenBlock;
	}

	public List<ASTNode> getElseBlock() {
		return elseBlock;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		if (!elseBlock.isEmpty()){
			return "if (" + condition + ") { " + thenBlock + "} else" + elseBlock + "}";
		} else{
			return "if (" + condition + ") { " + thenBlock + "}";
		}

	}

	@Override
	public Position getPosition() {
		return position;
	}
}
