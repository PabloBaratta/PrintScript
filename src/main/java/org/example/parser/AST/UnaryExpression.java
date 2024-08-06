package org.example.parser.AST;

public class UnaryExpression implements ASTNode{
    private ASTNode argument;
    private String operator;

    public UnaryExpression(ASTNode argument, String operator){
        this.argument = argument;
        this.operator = operator;
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
