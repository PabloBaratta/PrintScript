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

        if (!environment.containsKey(identifier.toString())) {
            throw new Exception("Variable not declared");
        }

        Expression astNodeResult = evaluateExpression(expression);
        Variable variable = environment.get(identifier.toString());

        if (typesMatch(astNodeResult, variable)) {
            variable.setExpression(astNodeResult);
            environment.put(identifier.toString(), variable);
        } else {
            throw new Exception("Type mismatch");
        }
	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		Identifier identifier = variableDeclaration.getIdentifier();
		Type type = variableDeclaration.getType();

        if (environment.containsKey(identifier.toString())) {
            throw new Exception("Variable already declared");
        }

        if (variableDeclaration.getExpression().isPresent()) {
            Expression astNodeResult = evaluateExpression(variableDeclaration.getExpression().get());
            if (typesMatch(astNodeResult, new Variable(type, Optional.empty()))) {
                environment.put(identifier.toString(), new Variable(type, Optional.of(astNodeResult)));
            } else {
                throw new Exception("Type mismatch");
            }
        } else {
            environment.put(identifier.toString(), new Variable(type, Optional.empty()));
        }
	}

    private static boolean typesMatch(Expression expression, Variable variable) {
        String variableTypeName = variable.getType().getTypeName();

        if (expression instanceof NumericLiteral && variableTypeName.equals("number")) {
            return true;
        } else if (expression instanceof TextLiteral && variableTypeName.equals("string")) {
            return true;
        }
        return false;
    }

    private Expression evaluateExpression(Expression expression) throws Exception {
        evaluate(expression);
        return stack.pop();
    }

	@Override
	public void visit(Identifier identifier) throws Exception {
		String identifierName = identifier.getName();

        if (!environment.containsKey(identifierName)) {
            throw new Exception("Undeclared variable");
        }

        Variable variable = environment.get(identifierName);
        Optional<Expression> optionalExpression = variable.getExpression();

        if (optionalExpression.isEmpty()) {
            throw new Exception("Variable declared but not assigned");
        }
        stack.push(optionalExpression.get());
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

        Expression arg = method.getArguments().getFirst();
        if (arg instanceof Identifier && !environment.containsKey(arg.toString())) {
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

        Expression result = evaluateBinaryOperation(left, right, binaryExpression.getOperator());
        stack.push(result);
	}

    private Expression evaluateBinaryOperation(Expression left, Expression right, String operator) throws Exception {
        if (operator.equals("+")) {
            if (left instanceof NumericLiteral && right instanceof NumericLiteral) {
                return new NumericLiteral(0.0, left.getPosition());
            } else {
                return new TextLiteral("", left.getPosition());
            }
        } else if (left instanceof NumericLiteral && right instanceof NumericLiteral) {
            return switch (operator) {
                case "-" -> new NumericLiteral(0.0, left.getPosition());
                case "/" -> new NumericLiteral(0.0, left.getPosition());
                case "*" -> new NumericLiteral(0.0, left.getPosition());
                default -> throw new Exception("Invalid operator");
            };
        } else {
            throw new Exception("Type mismatch for operator");
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
