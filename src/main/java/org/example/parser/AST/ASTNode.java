package org.example.parser.AST;

public interface ASTNode {
    void accept(ASTVisitor visitor);
    String toString();
}
