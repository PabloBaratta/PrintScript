package org.example.parser.AST;

public class BinaryExpression implements Expression {

    private ASTNode left;
    private String operator;
    private ASTNode right;

    public BinaryExpression(ASTNode left, String operator, ASTNode right){
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
