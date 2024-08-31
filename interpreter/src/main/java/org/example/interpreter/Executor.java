package org.example.interpreter;

import org.example.*;

import java.util.*;

public class Executor implements ASTVisitor {

	private final Map<String, Variable> environment = new HashMap<>();
	private final Stack<Literal> stack = new Stack<>();

	@Override
	public void visit(Assignation assignation) throws Exception {
		Identifier identifier = assignation.getIdentifier();
		Expression expression = assignation.getExpression();

		if (!environment.containsKey(identifier.toString())) {
			int line = identifier.getPosition().getLine();
			int column = identifier.getPosition().getColumn();
			throw new InterpreterException("Variable not declared", line, column);
		}

		Literal astNodeResult = evaluateExpression(expression);
		Variable variable = environment.get(identifier.toString());

		if (typesMatch(astNodeResult, variable)) {
			variable.setLiteral(astNodeResult);
			environment.put(identifier.toString(), variable);
		} else {
			int line = expression.getPosition().getLine();
			int column = expression.getPosition().getColumn();
			throw new InterpreterException("Type mismatch", line, column);
		}
	}


	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		Identifier identifier = variableDeclaration.getIdentifier();
		Type type = variableDeclaration.getType();

		if (environment.containsKey(identifier.toString())) {
			int line = identifier.getPosition().getLine();
			int column = identifier.getPosition().getColumn();
			throw new InterpreterException("Variable already declared", line, column);
		}

		if (variableDeclaration.getExpression().isPresent()) {
			Literal astNodeResult = evaluateExpression(variableDeclaration.getExpression().get());
			if (typesMatch(astNodeResult, new Variable(type, Optional.empty()))) {
				environment.put(identifier.toString(), new Variable(type, Optional.of(astNodeResult)));
			} else {
				int line = variableDeclaration.getExpression().get().getPosition().getLine();
				int column = variableDeclaration.getExpression().get().getPosition().getColumn();
				throw new InterpreterException("Type mismatch", line, column);
			}
		} else {
			environment.put(identifier.toString(), new Variable(type, Optional.empty()));
		}
	}

	@Override
	public void visit(Identifier identifier) throws Exception {
		String identifierName = identifier.getName();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();
		if (!environment.containsKey(identifierName)) {
			throw new InterpreterException("Undeclared variable", line, column);
		}

		Variable variable = environment.get(identifierName);
		Optional<Literal> optionalExpression = variable.getLiteral();

		if (optionalExpression.isEmpty()) {
			throw new InterpreterException("Variable declared but not assigned", line, column);
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
		evaluate(method.getArguments().getFirst()); // chequear si el identifier existe
		System.out.println(stack.pop().getValue());
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {
		evaluate(unaryExpression.getArgument());
		Literal argument = stack.pop();
		String operator = unaryExpression.getOperator();

		Literal result = evaluateUnaryOperation(argument, operator);
		stack.push(result);
	}

	private Literal evaluateUnaryOperation(Literal argument, String operator) throws Exception {
		int line = argument.getPosition().getLine();
		int column = argument.getPosition().getColumn();
		if (argument instanceof NumericLiteral) {
			Double value = (Double) argument.getValue();
			return switch (operator) {
				case "-" -> new NumericLiteral(-value, argument.getPosition());
/*                case "++" -> new NumericLiteral(value + 1, argument.getPosition());
				case "--" -> new NumericLiteral(value - 1, argument.getPosition()); */
				default -> {
					String error = "Invalid operator for numeric literal";
					throw new InterpreterException(error, line, column);
				}
			};
		} else {
			throw new InterpreterException("Unsupported unary expression argument type", line, column);
		}
	}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {
		evaluate(binaryExpression.getLeft());
		evaluate(binaryExpression.getRight());
		Literal right = stack.pop();
		Literal left = stack.pop();

		Literal result = evaluateBinaryOperation(left, right, binaryExpression.getOperator());
		stack.push(result);

	}

	private Literal evaluateBinaryOperation(Literal left, Literal right, String operator)
			throws Exception {
		if (operator.equals("+")) {
			if (left instanceof NumericLiteral && right instanceof NumericLiteral) {
				Double leftVal = (Double) left.getValue();
				Double rightVal = (Double) right.getValue();
				return new NumericLiteral(leftVal + rightVal, left.getPosition());
			} else {
				String value = left.getValue().toString() + right.getValue().toString();
				return new TextLiteral(value, left.getPosition());
			}
		} else {
			int line = left.getPosition().getLine();
			int column = left.getPosition().getColumn();
			if (left instanceof NumericLiteral && right instanceof NumericLiteral) {
				Double leftVal = (Double) left.getValue();
				Double rightVal = (Double) right.getValue();
				return switch (operator) {
					case "-" -> new NumericLiteral(leftVal - rightVal, left.getPosition());
					case "/" -> new NumericLiteral(leftVal / rightVal, left.getPosition());
					case "*" -> new NumericLiteral(leftVal * rightVal, left.getPosition());
					default -> throw new InterpreterException("Invalid operator", line, column);
				};
			} else {
				throw new InterpreterException("Type mismatch for operator", line, column);
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
		evaluate(parenthesis.getExpression());
	}

	@Override
	public void visit(BooleanLiteral booleanLiteral) throws Exception {

	}

	@Override
	public void visit(IfStatement ifStatement) throws Exception {

	}

	private void evaluate(ASTNode node) throws Exception {
		node.accept(this);
	}

	public Map<String, Variable> getEnvironment() {
		return environment;
	}

	public Stack<Literal> getStack() {
		return stack;
	}

	private static boolean typesMatch(Expression expression, Variable variable) {
		String variableTypeName = variable.getType().getTypeName();

		if (expression instanceof NumericLiteral && variableTypeName.equals("number")) {
			return true;
		} else return expression instanceof TextLiteral && variableTypeName.equals("string");
	}

	private Literal evaluateExpression(Expression expression) throws Exception {
		evaluate(expression);
		return stack.pop();
	}
}
