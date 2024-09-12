package org.example.interpreter;

import org.example.*;
import org.example.interpreter.handlers.ASTNodeHandler;
import org.example.util.WildcardLiteral;
import org.token.Position;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Validator implements ASTVisitor {

	private final Stack<Map<String, Variable>> environments = new Stack<>();
	private final Stack<Literal> stack = new Stack<>();
	private final InputProvider inputProvider;
	private final Map<String, ASTNodeHandler> handlers;

	public Validator(Map<String, ASTNodeHandler> handlers, InputProvider inputProvider) {
		environments.push(new HashMap<>());
		this.handlers = handlers;
		this.inputProvider = inputProvider;
	}

	@Override
	public void visit(Assignation assignation) throws Exception {
		ASTNodeHandler handler = handlers.get("Assignation");
		if (handler != null) {
			handler.handleValidation(assignation, this);
		} else {
			String s = "No handler found for node type: ";
			int line = assignation.getPosition().getLine();
			int column = assignation.getPosition().getColumn();
			throw new InterpreterException(s + "Assignation", line, column);
		}
	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		ASTNodeHandler handler = handlers.get("VariableDeclaration");
		if (handler != null) {
			handler.handleValidation(variableDeclaration, this);
		} else {
			String s = "No handler found for node type: ";
			int line = variableDeclaration.getPosition().getLine();
			int column = variableDeclaration.getPosition().getColumn();
			throw new InterpreterException(s + "Variable declaration", line, column);
		}
	}

	public boolean typesMatch(Expression expression, Variable variable) {
		String variableTypeName = variable.getType().getTypeName();

		if (expression instanceof NumericLiteral && variableTypeName.equals("number")) {
			return true;
		} else if (expression instanceof TextLiteral && variableTypeName.equals("string")) {
			return true;
		} else if (expression instanceof BooleanLiteral && variableTypeName.equals("boolean")) {
			return true;
		}
		else return expression instanceof WildcardLiteral;
	}

	public Literal evaluateExpression(Expression expression) throws Exception {
		evaluate(expression);
		return stack.pop();
	}

	@Override
	public void visit(Identifier identifier) throws Exception {
		ASTNodeHandler handler = handlers.get("Identifier");
		if (handler != null) {
			handler.handleValidation(identifier, this);
		} else {
			String s = "No handler found for node type: ";
			int line = identifier.getPosition().getLine();
			int column = identifier.getPosition().getColumn();
			throw new InterpreterException(s + "Identifier", line, column);
		}
	}

	@Override
	public void visit(TextLiteral textLiteral) throws Exception {
		ASTNodeHandler handler = handlers.get("TextLiteral");
		if (handler != null) {
			handler.handleValidation(textLiteral, this);
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
			handler.handleValidation(numericLiteral, this);
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
			handler.handleValidation(method, this);
		} else {
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException("No handler found for node type: " + methodName, line, column);
		}
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {
		ASTNodeHandler handler = handlers.get("UnaryExpression");
		if (handler != null) {
			handler.handleValidation(unaryExpression, this);
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
			handler.handleValidation(binaryExpression, this);
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
			handler.handleValidation(parenthesis, this);
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
			handler.handleValidation(booleanLiteral, this);
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
			handler.handleValidation(ifStatement, this);
		} else {
			String s = "No handler found for node type: ";
			int line = ifStatement.getPosition().getLine();
			int column = ifStatement.getPosition().getColumn();
			throw new InterpreterException(s + "If Statement", line, column);
		}
	}

	@Override
	public void visit(ConstDeclaration constDeclaration) throws Exception {
		ASTNodeHandler handler = handlers.get("ConstDeclaration");
		if (handler != null) {
			handler.handleValidation(constDeclaration, this);
		} else {
			String s = "No handler found for node type: ";
			int line = constDeclaration.getPosition().getLine();
			int column = constDeclaration.getPosition().getColumn();
			throw new InterpreterException(s + "Const declaration", line, column);
		}
	}

	public Literal convertStringToLiteral(String input, Position position) {
		if (input.equalsIgnoreCase("\"true\"") || input.equalsIgnoreCase("\"false\"")) {
			boolean boolValue = Boolean.parseBoolean(input);
			return new BooleanLiteral(boolValue, position);
		} else if (input.matches("\"-?\\d+(\\.\\d+)?\"")) {
			return new NumericLiteral(new BigDecimal(input), position);
		} else {
			return new TextLiteral(input, position);
		}
	}

	public Literal convertInputToLiteral(String input, Position position) {
		if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
			boolean boolValue = Boolean.parseBoolean(input);
			return new BooleanLiteral(boolValue, position);
		} else if (input.matches("-?\\d+(\\.\\d+)?")) {
			return new NumericLiteral(new BigDecimal(input), position);
		} else {
			return new TextLiteral(input, position);
		}
	}

	public Stack<Map<String, Variable>> getEnvironments() {
		return environments;
	}

	public void evaluate(ASTNode node) throws Exception {
		node.accept(this);
	}

	public Map<String, Variable> getEnvironment() {
		return environments.peek();
	}

	public Stack<Literal> getStack() {
		return stack;
	}

	public InputProvider getInputProvider() {
		return inputProvider;
	}
}
