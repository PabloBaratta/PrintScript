package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
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

		Expression argument = arguments.get(0);

		if (!(argument instanceof TextLiteral) && !(argument instanceof Identifier)) {
			String message = "println expects a TextLiteral or Identifier";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(message, line, column);
		}

		executor.evaluate(arguments.getFirst());
		Literal value = executor.getStack().pop();
		System.out.println(value.getValue());
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

		Expression argument = arguments.getFirst();

		if (!(argument instanceof TextLiteral) && !(argument instanceof Identifier)) {
			int line = position.getLine();
			String message = "println expects a TextLiteral or Identifier";
			throw new InterpreterException(message, line, position.getColumn());
		}


		validator.evaluate(arguments.getFirst());
		Literal value = validator.getStack().pop();
		System.out.println(value.getValue());
	}
}
