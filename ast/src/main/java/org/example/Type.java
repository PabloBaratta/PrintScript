package org.example;

import org.token.Position;

public class Type implements ASTNode {

	private final String typeName;
	private final Position position;

	public Type(String typeName, Position position) {
		this.typeName = typeName;
		this.position = position;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {

	}

	@Override
	public Position getPosition() {
		return position;
	}
}
