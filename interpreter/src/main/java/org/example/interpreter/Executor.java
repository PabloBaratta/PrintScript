package org.example.interpreter;

import org.example.*;
import org.example.interpreter.handlers.ASTNodeHandler;
import org.token.Position;
import java.util.*;

public class Executor implements ASTVisitor {

	private final Stack<Map<String, Variable>> environments = new Stack<>();
	private final Stack<Literal> stack = new Stack<>();

	private final Map<String, ASTNodeHandler> handlers;


	public Executor(Map<String, ASTNodeHandler> handlers) {
		environments.push(new HashMap<>());
		this.handlers = handlers;
	}

	@Override
	public void visit(Assignation assignation) throws Exception {
		ASTNodeHandler handler = handlers.get("Assignation");
		if (handler != null) {
			handler.handleExecution(assignation, this);
		} else {
			String s = "No handler found for node type: ";
			int line = assignation.getPosition().getLine();
			int column = assignation.getPosition().getColumn();
			throw new InterpreterException(s + "Assignation", line, column);
		}
	}

	public int findEnvironmentIndex(String name, int line, int column) throws InterpreterException {
		for (int i = environments.size() - 1; i >= 0; i--) {
			if (environments.get(i).containsKey(name)) {
				return i;
			}
		} throw new InterpreterException("Variable not declared", line, column);
	}


	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		ASTNodeHandler handler = handlers.get("VariableDeclaration");
		if (handler != null) {
			handler.handleExecution(variableDeclaration, this);
		} else {
			String s = "No handler found for node type: ";
			int line = variableDeclaration.getPosition().getLine();
			int column = variableDeclaration.getPosition().getColumn();
			throw new InterpreterException(s + "Variable declaration", line, column);
		}
	}

	@Override
	public void visit(ConstDeclaration constDeclaration) throws Exception {
		ASTNodeHandler handler = handlers.get("ConstDeclaration");
		if (handler != null) {
			handler.handleExecution(constDeclaration, this);
		} else {
			String s = "No handler found for node type: ";
			int line = constDeclaration.getPosition().getLine();
			int column = constDeclaration.getPosition().getColumn();
			throw new InterpreterException(s + "Const declaration", line, column);
		}
	}

	@Override
	public void visit(Identifier identifier) throws Exception {
		ASTNodeHandler handler = handlers.get("Identifier");
		if (handler != null) {
			handler.handleExecution(identifier, this);
		} else {
			String s = "No handler found for node type: ";
			int line = identifier.getPosition().getLine();
			int column = identifier.getPosition().getColumn();
			throw new InterpreterException(s + "Identifier", line, column);
		}
	}

	public Optional<Variable> findVariable(String name) {
		for (int i = environments.size() - 1; i >= 0; i--) {
			Variable variable = environments.get(i).get(name);
			if (variable != null) {
				return Optional.of(variable);
			}
		}
		return Optional.empty();
	}

	@Override
	public void visit(TextLiteral textLiteral) throws Exception {
		ASTNodeHandler handler = handlers.get("TextLiteral");
		if (handler != null) {
			handler.handleExecution(textLiteral, this);
		} else {
			String s = "No handler found for node type: ";
			int line = textLiteral.getPosition().getLine();
			int column = textLiteral.getPosition().getColumn();
			throw new InterpreterException(s + "Text literal", line, column);
		}
	}

	@Override
	public void visit(NumericLiteral numericLiteral) throws Exception {
		ASTNodeHandler handler = handlers.get("NumericLiteral");
		if (handler != null) {
			handler.handleExecution(numericLiteral, this);
		} else {
			String s = "No handler found for node type: ";
			int line = numericLiteral.getPosition().getLine();
			int column = numericLiteral.getPosition().getColumn();
			throw new InterpreterException(s + "Numeric literal", line, column);
		}
	}

	@Override
	public void visit(Method method) throws Exception {
		String methodName = method.getVariable().getName();
		Position position = method.getVariable().getPosition();

		ASTNodeHandler handler = handlers.get(methodName);

		if (handler != null) {
			handler.handleExecution(method, this);
		} else {
			String s = "No handler found for node type: ";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(s + methodName, line, column);
		}
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {
		ASTNodeHandler handler = handlers.get("UnaryExpression");
		if (handler != null) {
			handler.handleExecution(unaryExpression, this);
		} else {
			String s = "No handler found for node type: ";
			int line = unaryExpression.getPosition().getLine();
			int column = unaryExpression.getPosition().getColumn();
			throw new InterpreterException(s + "Unary expression", line, column);
		}
	}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {
		ASTNodeHandler handler = handlers.get("BinaryExpression");
		if (handler != null) {
			handler.handleExecution(binaryExpression, this);
		} else {
			String s = "No handler found for node type: ";
			int line = binaryExpression.getPosition().getLine();
			int column = binaryExpression.getPosition().getColumn();
			throw new InterpreterException(s + "Binary expression", line, column);
		}
	}

	@Override
	public void visit(Program program) throws Exception {
//		for (ASTNode child : program.getChildren()) {
//			child.accept(this);
//		}
	}

	@Override
	public void visit(Parenthesis parenthesis) throws Exception {
		ASTNodeHandler handler = handlers.get("Parenthesis");
		if (handler != null) {
			handler.handleExecution(parenthesis, this);
		} else {
			String s = "No handler found for node type: ";
			int line = parenthesis.getPosition().getLine();
			int column = parenthesis.getPosition().getColumn();
			throw new InterpreterException(s + "Parenthesis", line, column);
		}
	}

	@Override
	public void visit(BooleanLiteral booleanLiteral) throws Exception {
		ASTNodeHandler handler = handlers.get("BooleanLiteral");
		if (handler != null) {
			handler.handleExecution(booleanLiteral, this);
		} else {
			String s = "No handler found for node type: ";
			int line = booleanLiteral.getPosition().getLine();
			int column = booleanLiteral.getPosition().getColumn();
			throw new InterpreterException(s + "BooleanLiteral", line, column);
		}
	}

	@Override
	public void visit(IfStatement ifStatement) throws Exception {
		ASTNodeHandler handler = handlers.get("IfStatement");
		if (handler != null) {
			handler.handleExecution(ifStatement, this);
		} else {
			int line = ifStatement.getPosition().getLine();
			int column = ifStatement.getPosition().getColumn();
			String s = "No handler found for node type: ";
			throw new InterpreterException(s + "If Statement", line, column);
		}
	}

	public void evaluate(ASTNode node) throws Exception {
		node.accept(this);
	}

	public Map<String, Variable> getEnvironment() {
		return environments.peek();
	}

	public Stack<Map<String, Variable>> getEnvironments() {
		return environments;
	}

	public Stack<Literal> getStack() {
		return stack;
	}

	public boolean typesMatch(Expression expression, Variable variable) {
		String variableTypeName = variable.getType().getTypeName();

		if (expression instanceof NumericLiteral && variableTypeName.equals("number")) {
			return true;
		} else if (expression instanceof BooleanLiteral && variableTypeName.equals("boolean")){
			return true;
		} else return expression instanceof TextLiteral && variableTypeName.equals("string");
	}

	public Literal evaluateExpression(Expression expression) throws Exception {
		evaluate(expression);
		return stack.pop();
	}

	public Literal convertStringToLiteral(String input, Position position) {
		if (input.equalsIgnoreCase("\"true\"") || input.equalsIgnoreCase("\"false\"")) {
			boolean boolValue = Boolean.parseBoolean(input);
			return new BooleanLiteral(boolValue, position);
		} else if (input.matches("\"-?\\d+(\\.\\d+)?\"")) {
			double numberValue = Double.parseDouble(input);
			return new NumericLiteral(numberValue, position);
		} else {
			return new TextLiteral(input, position);
		}
	}

	public Literal convertInputToLiteral(String input, Position position) {
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
