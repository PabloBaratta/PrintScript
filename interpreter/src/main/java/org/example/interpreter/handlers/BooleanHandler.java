package org.example.interpreter.handlers;

import org.example.ASTNode;
import org.example.BooleanLiteral;
import org.example.interpreter.Executor;
import org.example.interpreter.Validator;

public class BooleanHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		BooleanLiteral booleanLiteral = (BooleanLiteral) node;
		executor.getStack().push(booleanLiteral);
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		BooleanLiteral booleanLiteral = (BooleanLiteral) node;
		validator.getStack().push(booleanLiteral);
	}
}
