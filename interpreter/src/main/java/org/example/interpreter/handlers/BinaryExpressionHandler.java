package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.example.util.Wildcard;
import org.example.util.WildcardLiteral;
import org.token.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
			if (leftLiteral instanceof BooleanLiteral && rightLiteral instanceof BooleanLiteral) {
				throw new InterpreterException("Invalid operator", line, column);
			}
			else if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				BigDecimal leftVal = ((NumericLiteral) leftLiteral).getValue();
				BigDecimal rightVal = ((NumericLiteral) rightLiteral).getValue();
				result = new NumericLiteral(leftVal.add(rightVal), leftLiteral.getPosition());
			} else {
				String value = leftLiteral.getValue().toString() + rightLiteral.getValue().toString();
				result = new TextLiteral(value, leftLiteral.getPosition());
			}
		} else {
			if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				BigDecimal leftVal = ((NumericLiteral) leftLiteral).getValue();
				BigDecimal rightVal = ((NumericLiteral) rightLiteral).getValue();
				result = switch (operator) {
					case "-" -> new NumericLiteral(leftVal.subtract(rightVal),
							leftLiteral.getPosition());
					case "/" -> new NumericLiteral(leftVal.divide(rightVal,
							10, RoundingMode.HALF_UP),
							leftLiteral.getPosition());
					case "*" -> new NumericLiteral(leftVal.multiply(rightVal),
							leftLiteral.getPosition());
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

			if (leftLiteral instanceof BooleanLiteral && rightLiteral instanceof BooleanLiteral) {
				throw new InterpreterException("Invalid operator", line, column);
			}
			else if (leftLiteral instanceof WildcardLiteral && rightLiteral instanceof WildcardLiteral){
				result = leftLiteral;
			} else if (leftLiteral instanceof WildcardLiteral && rightLiteral instanceof NumericLiteral) {
				result = leftLiteral;
			} else if (rightLiteral instanceof WildcardLiteral && leftLiteral instanceof NumericLiteral){
				result = rightLiteral;
			} else if (leftLiteral instanceof NumericLiteral && rightLiteral instanceof NumericLiteral) {
				result = new NumericLiteral(BigDecimal.valueOf(0), leftLiteral.getPosition());
			} else {
				String value = leftLiteral.getValue().toString() + rightLiteral.getValue().toString();
				result = new TextLiteral(value, leftLiteral.getPosition());
			}
		} else {

			if (operator.equals("-") || operator.equals("/") || operator.equals("*")) {

				if (leftLiteral instanceof WildcardLiteral
						&& rightLiteral instanceof WildcardLiteral){
					result = leftLiteral;
				} else if (leftLiteral instanceof WildcardLiteral
						&& rightLiteral instanceof NumericLiteral) {
					result = rightLiteral;
				} else if (leftLiteral instanceof NumericLiteral
						&& rightLiteral instanceof WildcardLiteral) {
					result = leftLiteral;
				} else if (leftLiteral instanceof NumericLiteral
						&& rightLiteral instanceof NumericLiteral) {
					result = leftLiteral;
				}
				else {
					throw new InterpreterException("Type mismatch for operator", line, column);
				}

			}

			else {
				throw new InterpreterException("Invalid operator", line, column);
			}
		}

		validator.getStack().push(result);
	}

}
