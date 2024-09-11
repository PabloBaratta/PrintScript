package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.token.Position;

import java.util.List;
import java.util.Scanner;

public class ReadInputHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Method method = (Method) node;
		List<Expression> arguments = method.getArguments();
		Position position = method.getVariable().getPosition();

		if (arguments.size() != 1 || !(arguments.get(0) instanceof TextLiteral)) {
			String message = "readInput expects exactly one TextLiteral argument";
			throw new InterpreterException(message, position.getLine(), position.getColumn());
		}

		String message = ((TextLiteral) arguments.get(0)).getValue();
		System.out.print(message + ": ");
		Scanner scanner = new Scanner(System.in);
		String userInput = scanner.nextLine();

		Literal inputLiteral = executor.convertInputToLiteral(userInput, position);
		executor.getStack().push(inputLiteral);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {

	}
}
