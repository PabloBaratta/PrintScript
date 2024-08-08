package org.example;


public class Assignation implements ASTNode {

    private final Identifier identifier;
    private final Expression expression;

    public Assignation(Identifier identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public Identifier getVariable() {
        return identifier;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        identifier.accept(visitor);
        expression.accept(visitor);
    }

    @Override
    public String toString() {
        return identifier.toString() + " = " + expression.toString() + ";\n";
    }
}
