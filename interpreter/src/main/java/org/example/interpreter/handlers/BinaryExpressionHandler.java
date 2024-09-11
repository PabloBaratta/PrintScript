package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;

public class BinaryExpressionHandler implements ASTNodeHandler {
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		BinaryExpression binaryExpression = (BinaryExpression) node;
		String operator = binaryExpression.getOperator();
		Expression left = binaryExpression.getLeft();
		Expression right = binaryExpression.getRight();

		executor.evaluate(left);
		executor.evaluate(right);
		Literal rightLiteral = executor.getStack().pop();
		Literal leftLiteral = executor.getStack().pop();

		Literal result;
		int line = leftLiteral.getPosition().getLine();
		int column = leftLiteral.getPosition().getColumn();

		if (operator.equals("+")) {
			if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				Double leftVal = (Double) leftLiteral.getValue();
				Double rightVal = (Double) rightLiteral.getValue();
				result = new NumericLiteral(leftVal + rightVal, leftLiteral.getPosition());
			} else {
				String value = leftLiteral.getValue().toString() + rightLiteral.getValue().toString();
				result = new TextLiteral(value, leftLiteral.getPosition());
			}
		} else {
			if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				Double leftVal = (Double) leftLiteral.getValue();
				Double rightVal = (Double) rightLiteral.getValue();
				result = switch (operator) {
					case "-" -> new NumericLiteral(leftVal - rightVal, leftLiteral.getPosition());
					case "/" -> new NumericLiteral(leftVal / rightVal, leftLiteral.getPosition());
					case "*" -> new NumericLiteral(leftVal * rightVal, leftLiteral.getPosition());
					default -> throw new InterpreterException("Invalid operator", line, column);
				};
			} else {
				throw new InterpreterException("Type mismatch for operator", line, column);
			}
		}

		executor.getStack().push(result);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		BinaryExpression binaryExpression = (BinaryExpression) node;
		String operator = binaryExpression.getOperator();
		Expression left = binaryExpression.getLeft();
		Expression right = binaryExpression.getRight();

		validator.evaluate(left);
		validator.evaluate(right);
		Literal rightLiteral = validator.getStack().pop();
		Literal leftLiteral = validator.getStack().pop();

		Literal result;
		int line = leftLiteral.getPosition().getLine();
		int column = leftLiteral.getPosition().getColumn();

		if (operator.equals("+")) {
			if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				Double leftVal = (Double) leftLiteral.getValue();
				Double rightVal = (Double) rightLiteral.getValue();
				result = new NumericLiteral(leftVal + rightVal, leftLiteral.getPosition());
			} else {
				String value = leftLiteral.getValue().toString() + rightLiteral.getValue().toString();
				result = new TextLiteral(value, leftLiteral.getPosition());
			}
		} else {
			if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				result = switch (operator) {
					case "-" -> new NumericLiteral(0.0, left.getPosition());
					case "/" -> new NumericLiteral(0.0, left.getPosition());
					case "*" -> new NumericLiteral(0.0, left.getPosition());
					default -> throw new InterpreterException("Invalid operator", line, column);
				};
			} else {
				throw new InterpreterException("Type mismatch for operator", line, column);
			}
		}

		validator.getStack().push(result);
	}
}
