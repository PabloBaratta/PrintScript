package org.example.interpreter;

import org.example.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class Validator implements ASTVisitor {

    private final Map<String, Variable> environment = new HashMap<>();
    private final Stack<Expression> stack = new Stack<>();

    @Override
    public void visit(Assignation assignation) throws Exception {
        Identifier identifier = assignation.getIdentifier();
        Expression expression = assignation.getExpression();
        if (environment.containsKey(identifier.toString())) {
            evaluate(expression);
            Expression astNodeResult = stack.pop();
            Variable variable = environment.get(identifier.toString());
            if (astNodeResult instanceof TextLiteral && variable.getType().getTypeName().equals("string")) {
                variable.setExpression(astNodeResult);
                environment.put(identifier.toString(), variable);
            } else if (astNodeResult instanceof NumericLiteral && variable.getType().getTypeName().equals("number")) {
                variable.setExpression(astNodeResult);
                environment.put(identifier.toString(), variable);
            } else {
                throw new Exception("los tipos no coinciden");
            }
        } else {
            throw new Exception("la variable no esta declarada");
        }
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) throws Exception {
        Identifier identifier = variableDeclaration.getIdentifier();
        Type type = variableDeclaration.getType();
        if (environment.containsKey(identifier.toString())) {
            throw new Exception("la variable ya esta declarada");
        }
        if (variableDeclaration.getExpression().isPresent()) {
            evaluate(variableDeclaration.getExpression().get());
            Expression astNodeResult = stack.pop();
            if (type.getTypeName().equals("string") && astNodeResult instanceof TextLiteral) {
                environment.put(identifier.toString(), new Variable(type, astNodeResult));
            } else if (type.getTypeName().equals("number") && astNodeResult instanceof NumericLiteral) {
                environment.put(identifier.toString(), new Variable(type, astNodeResult));
            } else {
                throw new Exception("los tipos no coinciden");
            }
        } else {
            environment.put(identifier.toString(), new Variable(type, null));
        }
    }

    @Override
    public void visit(Identifier identifier) throws Exception {
        String identifierName = identifier.getName();
        if (environment.containsKey(identifierName)) {
            Variable variable = environment.get(identifierName);
            Optional<Expression> optionalExpression = variable.getExpression();
            if (optionalExpression.isEmpty()) {
                throw new Exception("variable was declared but not assigned");
            }
            stack.push(optionalExpression.get());
        } else {
            throw new Exception("undeclared variable");
        }
    }

    @Override
    public void visit(TextLiteral textLiteral) {
        stack.push(textLiteral);
    }

    @Override
    public void visit(NumericLiteral numericLiteral) {
        stack.push(numericLiteral);
    }

    @Override
    public void visit(Method method) throws Exception {
        if (method.getArguments().getFirst() instanceof Identifier && !environment.containsKey(method.getArguments().getFirst().toString())) {
            throw new Exception("undeclared variable");
        }
    }

    @Override
    public void visit(UnaryExpression unaryExpression) throws Exception {
        // Validate unary expression if needed
    }

    @Override
    public void visit(BinaryExpression binaryExpression) throws Exception {
        evaluate(binaryExpression.getLeft());
        evaluate(binaryExpression.getRight());
        Expression right = stack.pop();
        Expression left = stack.pop();
        if (binaryExpression.getOperator().equals("+")) {
            if (left instanceof NumericLiteral && right instanceof NumericLiteral) {
                stack.push(new NumericLiteral(0.0));
            } else {
                stack.push(new TextLiteral(""));
            }
        } else if (!(left instanceof NumericLiteral && right instanceof NumericLiteral)) {
            throw new Exception("para las siguientes operaciones tienen que ser numeros");
        } else {
            switch (binaryExpression.getOperator()) {
                case "-":
                    stack.push(new NumericLiteral(0.0));
                    break;
                case "/":
                    stack.push(new NumericLiteral(0.0));
                    break;
                case "*":
                    stack.push(new NumericLiteral(0.0));
                    break;
                default:
                    throw new Exception("Operador no válido: " + binaryExpression.getOperator());
            }
        }
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

    private void evaluate(ASTNode node) throws Exception {
        node.accept(this);
    }

    public Map<String, Variable> getEnvironment() {
        return environment;
    }

    public Stack<Expression> getStack() {
        return stack;
    }
}
