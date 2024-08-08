package org.example.parser.AST;

public class Type implements ASTNode {

    private final String typeName;

    public Type(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
