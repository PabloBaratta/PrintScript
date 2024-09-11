package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.token.Position;

import java.util.List;

public class NumericLiteralHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		NumericLiteral numericLiteral = (NumericLiteral) node;
		executor.getStack().push(numericLiteral);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		NumericLiteral numericLiteral = (NumericLiteral) node;
		validator.getStack().push(numericLiteral);
	}
}
