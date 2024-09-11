package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;

import java.math.BigDecimal;

public class UnaryExpressionHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		UnaryExpression unaryExpression = (UnaryExpression) node;
		String operator = unaryExpression.getOperator();
		Expression argument = unaryExpression.getArgument();

		executor.evaluate(argument);
		Literal argumentLiteral = executor.getStack().pop();

		int line = argumentLiteral.getPosition().getLine();
		int column = argumentLiteral.getPosition().getColumn();

		Literal result;
		if (argumentLiteral instanceof NumericLiteral) {
			BigDecimal value = ((NumericLiteral) argumentLiteral).getValue();
			if (operator.equals("-")) {
				BigDecimal subtract = BigDecimal.valueOf(0).subtract(value);
				result = new NumericLiteral(subtract, argumentLiteral.getPosition());
			} else {
				String error = "Invalid operator for numeric literal";
				throw new InterpreterException(error, line, column);
			}
		} else {
			throw new InterpreterException("Unsupported unary expression argument type", line, column);
		}

		executor.getStack().push(result);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		UnaryExpression unaryExpression = (UnaryExpression) node;
		String operator = unaryExpression.getOperator();
		Expression argumentExpression = unaryExpression.getArgument();

		validator.evaluate(argumentExpression);
		Literal argument = validator.getStack().pop();

		int line = argument.getPosition().getLine();
		int column = argument.getPosition().getColumn();

		if (operator.equals("-")) {
			if (!(argument instanceof NumericLiteral)) {
				String s = "Type mismatch for unary operator: ";
				throw new InterpreterException(s + operator, line, column);
			}
		} else {
			String s = "Invalid operator for unary expression: ";
			throw new InterpreterException(s + operator, line, column);
		}

		Literal result = new NumericLiteral(BigDecimal.valueOf(0.0), argument.getPosition());
		validator.getStack().push(result);
	}
}
