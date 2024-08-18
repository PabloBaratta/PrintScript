package org.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Formatter {

    private final Map<String, Rule> rules;

    public Formatter(Map<String, Rule> rules) {
        this.rules = rules;
    }

    public String format(Program program) throws Exception {
        StringBuilder result = new StringBuilder();
        List<ASTNode> children = program.getChildren();

        for (ASTNode child : children) {
            formatNode(child, result);
        }
        return result.toString();
    }

    private void formatNode(ASTNode child, StringBuilder result) {
        switch (child) {
            case VariableDeclaration variableDeclaration:
                formatVariableDeclaration(variableDeclaration, result);
                break;
            case Assignation assignation:
                formatAssignation(assignation, result);
                break;
            case Method method:
                formatMethod(method, result);
                break;
            default:
                throw new IllegalArgumentException("Unknown node type: " + child.getClass());
        }
    }

    private void formatMethod(Method method, StringBuilder result) {
        Identifier identifier = method.getVariable();
        List<Expression> arguments = method.getArguments();
        if (identifier.toString().equals("println")) {
            if (rules.get("newLineBeforePrintln").getRule()) {
                int qty = rules.get("newLineBeforePrintln").getQty().get();
                result.append("\n".repeat(Math.max(0, qty)));
            }
            result.append(identifier.toString()).append("(");
            for (int i = 0; i < arguments.size(); i++) {
                result.append(arguments.get(i).toString());
                if (i < arguments.size() - 1) {
                    result.append(", ");
                }
            }
            result.append(");\n");
        }
    }

    private void formatAssignation(Assignation assignation, StringBuilder result) {
        Identifier identifier = assignation.getIdentifier();
        Expression expression = assignation.getExpression();
        result.append(identifier.toString());
        if (rules.get("spaceBeforeAssignation").getRule()) {
            result.append(" ");
        }
        result.append("=");
        if (rules.get("spaceAfterAssignation").getRule()) {
            result.append(" ");
        }
        result.append(expression.toString());
        result.append(";\n");
    }

    private void formatVariableDeclaration(VariableDeclaration variableDeclaration, StringBuilder result) {
        Identifier identifier = variableDeclaration.getIdentifier();
        Type type = variableDeclaration.getType();
        Optional<Expression> expression = variableDeclaration.getExpression();
        result.append("let ").append(identifier.toString());
        if (rules.get("spaceBeforeColon").getRule()) {
            result.append(" ");
        }
        result.append(":");
        if (rules.get("spaceAfterColon").getRule()) {
            result.append(" ");
        }
        result.append(type.toString());
        if (expression.isPresent()){
            if (rules.get("spaceBeforeAssignation").getRule()) {
                result.append(" ");
            }
            result.append("=");
            if (rules.get("spaceAfterAssignation").getRule()) {
                result.append(" ");
            }
            result.append(expression.get().toString());
        }
        result.append(";\n");
    }

}
