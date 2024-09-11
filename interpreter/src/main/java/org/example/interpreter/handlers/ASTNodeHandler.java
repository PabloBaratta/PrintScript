package org.example.interpreter.handlers;

import org.example.ASTNode;
import org.example.interpreter.Executor;
import org.example.interpreter.Validator;

public interface ASTNodeHandler {
	void handleExecution(ASTNode node, Executor executor) throws Exception;
	void handleValidation(ASTNode node, Validator validator) throws Exception;
}
