package org.example;

import org.example.lexer.token.Position;

import java.util.List;

public class Program implements ASTNode {

	private final List<ASTNode> children;

	public Program(List<ASTNode> children) {
		this.children = children;
	}

	public List<ASTNode> getChildren() {
		return children;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (ASTNode child : children) {
			result.append(child.toString());
		}
		return result.toString();
	}

	@Override
	public Position getPosition() {
		return new Position(0,0,0);
	}

}
