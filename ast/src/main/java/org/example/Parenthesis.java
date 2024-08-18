package org.example;

import org.example.lexer.token.Position;

public class Parenthesis implements Expression{

    private final Expression expression;
    private final Position position;

    public Parenthesis(Expression expression, Position position) {
        this.expression = expression;
        this.position = position;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public Expression getExpression(){
        return expression;
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")";
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Object getValue() {
        return null;
    }
}
