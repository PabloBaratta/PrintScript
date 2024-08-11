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

    public ASTNode getLeft(){
        return this.left;
    }

    public ASTNode getRight(){
        return this.right;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        left.accept(visitor);
        right.accept(visitor);
    }

    @Override
    public String toString() {
        return left.toString() + operator + right.toString();
    }
}
