package org.example;


public class Assignation implements ASTNode {

    private final Identifier identifier;
    private final Expression expression;

    public Assignation(Identifier identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return identifier.toString() + " = " + expression.toString() + ";\n";
    }
}
