package org.example.parser.AST;

public class TypeDeclaration implements ASTNode {

    private final Variable variable;
    private final Variable type;

    public TypeDeclaration(Variable variable, Variable type) {
        this.variable = variable;
        this.type = type;
    }

    public Variable getVariable() {
        return variable;
    }

    public Variable getType() {
        return type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        variable.accept(visitor);
        type.accept(visitor);
    }

    @Override
    public String toString() {
        return variable.toString() + " : " + type.toString() + ";\n";
    }
}
