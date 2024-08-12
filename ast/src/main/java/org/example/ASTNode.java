package org.example;

public interface ASTNode {
    void accept(ASTVisitor visitor) throws Exception;
    String toString();
}
