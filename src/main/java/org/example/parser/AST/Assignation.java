package org.example.parser.AST;

public class Assignation implements ASTNode {
    private final Variable variable;
    private final ASTNode expression;

    public Assignation(Variable variable, ASTNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public Variable getVariable() {
        return variable;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        variable.accept(visitor);
        expression.accept(visitor);
    }

    @Override
    public String toString() {
        return variable.toString() + " = " + expression.toString() + ";\n";
    }
}
