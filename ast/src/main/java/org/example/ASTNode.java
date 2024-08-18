package org.example;
import org.example.lexer.token.Position;

public interface ASTNode {

    void accept(ASTVisitor visitor) throws Exception;

    String toString();

    Position getPosition();
}
