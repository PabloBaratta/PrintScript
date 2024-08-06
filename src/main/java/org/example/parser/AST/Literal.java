package org.example.parser.AST;

public class Literal<T extends Comparable<T>> implements ASTNode{

    private final T value;

    public Literal(T value) {
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
