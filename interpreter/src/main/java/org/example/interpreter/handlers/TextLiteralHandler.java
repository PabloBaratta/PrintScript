package org.example.interpreter.handlers;

import org.example.ASTNode;
import org.example.TextLiteral;
import org.example.interpreter.Executor;
import org.example.interpreter.Validator;

public class TextLiteralHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		TextLiteral textLiteral = (TextLiteral) node;
		executor.getStack().push(textLiteral);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		TextLiteral textLiteral = (TextLiteral) node;
		validator.getStack().push(textLiteral);
	}
}
