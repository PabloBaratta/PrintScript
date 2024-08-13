package org.example;


public class UnaryExpression implements Expression{
    private Expression argument;
    private String operator;

    public UnaryExpression(Expression argument, String operator){
        this.argument = argument;
        this.operator = operator;
    }

    public Expression getArgument() {
        return argument;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return operator + argument.toString();
    }

    @Override
    public Object getValue() {
        return null;
    }
}
