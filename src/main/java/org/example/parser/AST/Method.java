package org.example.parser.AST;

import java.util.List;

public class Method implements ASTNode{
    private Variable variable;
    private List<ASTNode> arguments;

    public Method(Variable variable, List<ASTNode> arguments) {
        this.variable = variable;
        this.arguments = arguments;
    }

    public Variable getVariable() {
        return variable;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        variable.accept(visitor);
        for (ASTNode node : arguments){
            node.accept(visitor);
        }
    }

    // terminar de ver como printear
    @Override
    public String toString() {
        return variable.toString() + "(" + arguments.toString() + ")";
    }
}
