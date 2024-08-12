package org.example;

import javax.swing.text.Position;

public class UnaryExpression implements Expression{
    private ASTNode argument;
    private String operator;
    private Position position;

    public UnaryExpression(ASTNode argument, String operator, Position position) {
        this.argument = argument;
        this.operator = operator;
        this.position = position;
    }


    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        argument.accept(visitor);
    }

    @Override
    public String toString() {
        return operator + argument.toString();
    }
}
