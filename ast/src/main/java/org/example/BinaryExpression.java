package org.example;

public class BinaryExpression implements Expression {

    private Expression left;
    private String operator;
    private Expression right;

    public BinaryExpression(Expression left, String operator, Expression right){
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft(){
        return this.left;
    }

    public Expression getRight(){
        return this.right;
    }

    public String getOperator(){return this.operator;}

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return left.toString() + operator + right.toString();
    }

    @Override
    public Object getValue() {
        return null;
    }
}
