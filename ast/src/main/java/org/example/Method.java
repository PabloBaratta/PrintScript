package org.example;

import java.util.List;

public class Method implements ASTNode{
    private Identifier identifier;
    private List<ASTNode> arguments;

    public Method(Identifier identifier, List<ASTNode> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public Identifier getVariable() {
        return identifier;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    // terminar de ver como printear
    @Override
    public String toString() {
        return identifier.toString() + "(" + arguments.toString() + ")";
    }
}
