package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.example.util.Wildcard;
import org.example.util.WildcardLiteral;
import org.token.Position;

import java.util.List;
import java.util.Scanner;

public class ReadInputHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1) {
			String message = "readInput expects exactly one argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}

		Literal evaluatedArgument = executor.evaluateExpression(arguments.getFirst());

		if (!(evaluatedArgument instanceof TextLiteral)) {
			String message = "readInput expects exactly one TextLiteral argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}

		TextLiteral message = ((TextLiteral) evaluatedArgument);
		executor.getOutputCapture().capture(message.toString());
		String userInput = executor.getInputProvider().readInput(message.toString());

		Literal inputLiteral = executor.convertInputToLiteral(userInput, position);
		executor.getStack().push(inputLiteral);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1) {
			String message = "readInput expects exactly one argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}

		Literal evaluatedArgument = validator.evaluateExpression(arguments.get(0));

		if (!(evaluatedArgument instanceof TextLiteral)) {
			String message = "readInput expects exactly one TextLiteral argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}


		WildcardLiteral inputAny = new WildcardLiteral(new Wildcard(), method.getPosition());

		validator.getStack().push(inputAny);
	}
}
