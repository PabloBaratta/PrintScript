package org.example.interpreter;

import org.example.*;
import org.example.lexer.token.Position;

import java.util.*;

public class Executor implements ASTVisitor {

	private final Stack<Map<String, Variable>> environments = new Stack<>();
	private final Stack<Literal> stack = new Stack<>();

	public Executor() {
		environments.push(new HashMap<>());
	}

	@Override
	public void visit(Assignation assignation) throws Exception {
		Identifier identifier = assignation.getIdentifier();
		Expression exp = assignation.getExpression();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		int environmentIndex = findEnvironmentIndex(identifier.toString(), line, column);
		Map<String, Variable> env = environments.get(environmentIndex);
		Variable variable = env.get(identifier.toString());

		if (variable.isConst()) {
			throw new InterpreterException("Cannot reassign a constant variable", line, column);
		}

		Literal astNodeResult = evaluateExpression(exp);

		if (typesMatch(astNodeResult, variable)) {
			variable.setLiteral(astNodeResult);
			env.put(identifier.toString(), variable);
		} else {
			int lineExp = exp.getPosition().getLine();
			int columnExp  = exp.getPosition().getColumn();
			throw new InterpreterException("Type mismatch", lineExp, columnExp);
		}
	}

	private int findEnvironmentIndex(String name, int line, int column) throws InterpreterException {
		for (int i = environments.size() - 1; i >= 0; i--) {
			if (environments.get(i).containsKey(name)) {
				return i;
			}
		} throw new InterpreterException("Variable not declared", line, column);
	}


	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		Identifier identifier = variableDeclaration.getIdentifier();
		Type type = variableDeclaration.getType();

		Map<String, Variable> currentEnvironment = environments.peek();

		if (currentEnvironment.containsKey(identifier.toString())) {
			int line = identifier.getPosition().getLine();
			int column = identifier.getPosition().getColumn();
			throw new InterpreterException("Variable already declared", line, column);
		}

		if (variableDeclaration.getExpression().isPresent()) {
			Literal astNodeResult = evaluateExpression(variableDeclaration.getExpression().get());
			if (typesMatch(astNodeResult, new Variable(type, Optional.empty(), false))) {
				Variable value = new Variable(type, Optional.of(astNodeResult), false);
				currentEnvironment.put(identifier.toString(), value);
			} else {
				int line = variableDeclaration.getExpression().get().getPosition().getLine();
				int column = variableDeclaration.getExpression().get().getPosition().getColumn();
				throw new InterpreterException("Type mismatch", line, column);
			}
		} else {
			currentEnvironment.put(identifier.toString(), new Variable(type, Optional.empty(), false));
		}
	}

	@Override
	public void visit(ConstDeclaration constDeclaration) throws Exception {
		Identifier identifier = constDeclaration.getIdentifier();
		Type type = constDeclaration.getType();

		Map<String, Variable> currentEnvironment = environments.peek();

		if (currentEnvironment.containsKey(identifier.toString())) {
			int line = identifier.getPosition().getLine();
			int column = identifier.getPosition().getColumn();
			throw new InterpreterException("Constant variable already declared", line, column);
		}

		Literal astNodeResult = evaluateExpression(constDeclaration.getExpression());

		if (typesMatch(astNodeResult, new Variable(type, Optional.empty(), true))) {
			Variable value = new Variable(type, Optional.of(astNodeResult), true);
			currentEnvironment.put(identifier.toString(), value);
		} else {
			int line = constDeclaration.getExpression().getPosition().getLine();
			int column = constDeclaration.getExpression().getPosition().getColumn();
			throw new InterpreterException("Type mismatch", line, column);
		}
	}

	@Override
	public void visit(Identifier identifier) throws Exception {
		String identifierName = identifier.getName();

		Optional<Variable> variableOpt = findVariable(identifierName);

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();
		if (variableOpt.isEmpty()) {
			throw new InterpreterException("Undeclared variable", line, column);
		}

		Variable variable = variableOpt.get();
		Optional<Literal> optionalExpression = variable.getLiteral();

		if (optionalExpression.isEmpty()) {
			throw new InterpreterException("Variable declared but not assigned", line, column);
		}
		stack.push(optionalExpression.get());
	}

	private Optional<Variable> findVariable(String name) {
		for (int i = environments.size() - 1; i >= 0; i--) {
			Variable variable = environments.get(i).get(name);
			if (variable != null) {
				return Optional.of(variable);
			}
		}
		return Optional.empty();
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
		String methodName = method.getVariable().getName();
		List<Expression> arguments = method.getArguments();

		switch (methodName) {
			case "println":
				handlePrintln(arguments, method.getVariable().getPosition());
				break;
			case "readInput":
				handleReadInput(arguments, method.getVariable().getPosition());
				break;
			default:
				int line = method.getVariable().getPosition().getLine();
				int column = method.getVariable().getPosition().getColumn();
				throw new InterpreterException("Unknown method: " + methodName, line, column);
		}
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
			if (operator.equals("-")){
				return new NumericLiteral(-value, argument.getPosition());
			} else {
				String error = "Invalid operator for numeric literal";
				throw new InterpreterException(error, line, column);
			}
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
		stack.push(booleanLiteral);
	}

	@Override
	public void visit(IfStatement ifStatement) throws Exception {
		evaluate(ifStatement.getCondition());
		BooleanLiteral conditionResult = (BooleanLiteral) stack.pop();

		Map<String, Variable> originalEnvironment = environments.peek();

		if (conditionResult.getValue()) {
			Map<String, Variable> thenEnvironment = new HashMap<>(originalEnvironment);
			environments.push(thenEnvironment);
			try {
				for (ASTNode node : ifStatement.getThenBlock()) {
					node.accept(this);
				}
			} finally {
				environments.pop();
			}
		} else if (!ifStatement.getElseBlock().isEmpty()) {
			Map<String, Variable> elseEnvironment = new HashMap<>(originalEnvironment);
			environments.push(elseEnvironment);
			try {
				for (ASTNode node : ifStatement.getElseBlock()) {
					node.accept(this);
				}
			} finally {
				environments.pop();
			}
		}
	}

	private void evaluate(ASTNode node) throws Exception {
		node.accept(this);
	}

	public Map<String, Variable> getEnvironment() {
		return environments.peek();
	}

	public Stack<Literal> getStack() {
		return stack;
	}

	private static boolean typesMatch(Expression expression, Variable variable) {
		String variableTypeName = variable.getType().getTypeName();

		if (expression instanceof NumericLiteral && variableTypeName.equals("number")) {
			return true;
		} else if (expression instanceof BooleanLiteral && variableTypeName.equals("boolean")){
			return true;
		} else return expression instanceof TextLiteral && variableTypeName.equals("string");
	}

	private Literal evaluateExpression(Expression expression) throws Exception {
		evaluate(expression);
		return stack.pop();
	}

	private void handlePrintln(List<Expression> arguments, Position position) throws Exception {
		if (arguments.size() != 1) {
			String message = "println expects exactly one argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}
		evaluate(arguments.getFirst());
		Literal value = stack.pop();
		System.out.println(value.getValue());

	}

	private void handleReadInput(List<Expression> arguments, Position position) throws Exception {
		if (arguments.size() != 1 || !(arguments.getFirst() instanceof TextLiteral messageLiteral)) {
			String message = "readInput expects exactly one TextLiteral argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}

		String message = messageLiteral.getValue();

		System.out.print(message + ": ");
		Scanner scanner = new Scanner(System.in);
		String userInput = scanner.nextLine();

		Literal inputLiteral = convertInputToLiteral(userInput, position);

		stack.push(inputLiteral);
	}

	private Literal convertInputToLiteral(String input, Position position) {
		if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
			boolean boolValue = Boolean.parseBoolean(input);
			return new BooleanLiteral(boolValue, position);
		} else if (input.matches("-?\\d+(\\.\\d+)?")) {
			double numberValue = Double.parseDouble(input);
			return new NumericLiteral(numberValue, position);
		} else {
			return new TextLiteral(input, position);
		}
	}

}
