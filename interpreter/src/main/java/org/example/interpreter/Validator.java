package org.example.interpreter;

import org.example.*;
import java.util.HashMap;
import java.util.Map;

public class Validator implements ASTVisitor {

    private final Map<String, Variable> environment = new HashMap<>();

    @Override
    public void visit(Assignation assignation) throws Exception {
        Identifier identifier = assignation.getIdentifier();
        if (!environment.containsKey(identifier.toString())) {
            throw new Exception("la variable no esta declarada");
        }
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) throws Exception {
        Identifier identifier = variableDeclaration.getIdentifier();
        if (environment.containsKey(identifier.toString())) {
            throw new Exception("la variable ya esta declarada");
        }
        environment.put(identifier.toString(), new Variable(variableDeclaration.getType(), null));
    }

    @Override
    public void visit(Identifier identifier) throws Exception {
        if (!environment.containsKey(identifier.getName())) {
            throw new Exception("undeclared variable");
        }
    }

    @Override
    public void visit(TextLiteral textLiteral) {
        // No validation needed for literals
    }

    @Override
    public void visit(NumericLiteral numericLiteral) {
        // No validation needed for literals
    }

    @Override
    public void visit(Method method) throws Exception {
        // Validate method arguments if needed
    }

    @Override
    public void visit(UnaryExpression unaryExpression) throws Exception {
        // Validate unary expression if needed
    }

    @Override
    public void visit(BinaryExpression binaryExpression) throws Exception {
        // Validate binary expression if needed
    }

    @Override
    public void visit(Program program) throws Exception {
        for (ASTNode child : program.getChildren()) {
            child.accept(this);
        }
    }

    @Override
    public void visit(Parenthesis parenthesis) throws Exception {
        parenthesis.getExpression().accept(this);
    }

    public Map<String, Variable> getEnvironment() {
        return environment;
    }
}
