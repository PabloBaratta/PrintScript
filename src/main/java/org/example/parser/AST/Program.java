package org.example.parser.AST;

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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (ASTNode child : children) {
            child.accept(visitor);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ASTNode child : children) {
            result.append(child.toString());
        }
        return result.toString();
    }

}
