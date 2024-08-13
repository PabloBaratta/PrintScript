package org.example;

public class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object getValue() {
        return null;
    }
}
