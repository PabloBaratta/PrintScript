package org.example;
import org.token.Position;

public interface ASTNode {

	void accept(ASTVisitor visitor) throws Exception;

	String toString();

	Position getPosition();
}
