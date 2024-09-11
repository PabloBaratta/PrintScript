package org.example.interpreter.handlers;

import org.example.ASTNode;
import org.example.Parenthesis;
import org.example.interpreter.Executor;
import org.example.interpreter.Validator;

public class ParenthesisHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Parenthesis parenthesis = (Parenthesis) node;
		executor.evaluate(parenthesis.getExpression());
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		Parenthesis parenthesis = (Parenthesis) node;
		validator.evaluate(parenthesis.getExpression());
	}
}
