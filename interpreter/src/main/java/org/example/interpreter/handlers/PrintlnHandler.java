package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.example.util.WildcardLiteral;
import org.token.Position;

import java.util.List;

public class PrintlnHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1) {
			String message = "println expects exactly one argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}

		Literal evaluatedArgument = executor.evaluateExpression(arguments.getFirst());

		if (!(evaluatedArgument instanceof TextLiteral)
				&& !(evaluatedArgument instanceof NumericLiteral)
				&& !(evaluatedArgument instanceof BooleanLiteral)){
			String message = "println expects a Literal";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(message, line, column);
		}


		executor.getOutputCapture().capture(evaluatedArgument.toString());
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1) {
			String message = "println expects exactly one argument";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(message, line, column);
		}

		Literal evaluatedArgument = validator.evaluateExpression(arguments.getFirst());

		if (!(evaluatedArgument instanceof TextLiteral)
				&& !(evaluatedArgument instanceof NumericLiteral)
				&& !(evaluatedArgument instanceof BooleanLiteral)
				&& !(evaluatedArgument instanceof WildcardLiteral)){
			int line = position.getLine();
			String message = "println expects a Literal";
			throw new InterpreterException(message, line, position.getColumn());
		}
	}
}
