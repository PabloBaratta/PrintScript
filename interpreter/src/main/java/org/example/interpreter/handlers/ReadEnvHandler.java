package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.token.Position;

import java.util.List;

public class ReadEnvHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1 || !(arguments.get(0) instanceof TextLiteral)) {
			String message = "readEnv expects exactly one TextLiteral argument";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(message, line, column);
		}

		String envVariableName = ((TextLiteral) arguments.get(0)).getValue();
		String envValue = System.getenv(envVariableName);

		if (envValue == null) {
			String s = "Environment variable ";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(s + envVariableName + " not found", line, column);
		}

		Literal literalValue = executor.convertStringToLiteral(envValue, position);
		executor.getStack().push(literalValue);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1 || !(arguments.get(0) instanceof TextLiteral)) {
			String message = "readEnv expects exactly one TextLiteral argument";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(message, line, column);
		}

		String envVariableName = ((TextLiteral) arguments.get(0)).getValue();
		String envValue = System.getenv(envVariableName);

		if (envValue == null) {
			String s = "Environment variable ";
			int line = position.getLine();
			int column = position.getColumn();
			throw new InterpreterException(s + envVariableName + " not found", line, column);
		}

		Literal literalValue = validator.convertStringToLiteral(envValue, position);
		validator.getStack().push(literalValue);
	}
}
