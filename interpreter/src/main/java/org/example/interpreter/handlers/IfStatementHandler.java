package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.token.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfStatementHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		IfStatement ifStatement = (IfStatement) node;
		Expression condition = ifStatement.getCondition();
		List<ASTNode> thenBlock = ifStatement.getThenBlock();
		List<ASTNode> elseBlock = ifStatement.getElseBlock();

		Literal conditionResult = executor.evaluateExpression(condition);

		if (!(conditionResult instanceof BooleanLiteral)) {
			Position position = condition.getPosition();
			throw new InterpreterException("Condition in if statement must be a boolean expression",
					position.getLine(), position.getColumn());
		}

		BooleanLiteral booleanLiteral = (BooleanLiteral) conditionResult;

		Map<String, Variable> originalEnvironment = executor.getEnvironments().peek();

		if (booleanLiteral.getValue()) {
			Map<String, Variable> thenEnvironment = new HashMap<>(originalEnvironment);
			executor.getEnvironments().push(thenEnvironment);
			try {
				for (ASTNode nodeInThenBlock : thenBlock) {
					nodeInThenBlock.accept(executor);
				}
			} finally {
				executor.getEnvironments().pop();
			}
		} else if (!elseBlock.isEmpty()) {
			Map<String, Variable> elseEnvironment = new HashMap<>(originalEnvironment);
			executor.getEnvironments().push(elseEnvironment);
			try {
				for (ASTNode nodeInElseBlock : elseBlock) {
					nodeInElseBlock.accept(executor);
				}
			} finally {
				executor.getEnvironments().pop();
			}
		}
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		IfStatement ifStatement = (IfStatement) node;
		Expression condition = ifStatement.getCondition();
		List<ASTNode> thenBlock = ifStatement.getThenBlock();
		List<ASTNode> elseBlock = ifStatement.getElseBlock();

		Literal conditionResult = validator.evaluateExpression(condition);

		if (!(conditionResult instanceof BooleanLiteral)) {
			Position position = condition.getPosition();
			throw new InterpreterException("Condition in if statement must be a boolean expression",
					position.getLine(), position.getColumn());
		}

		for (ASTNode nodeInThenBlock : thenBlock) {
			nodeInThenBlock.accept(validator);
		}

		if (!elseBlock.isEmpty()) {
			for (ASTNode nodeInElseBlock : elseBlock) {
				nodeInElseBlock.accept(validator);
			}
		}
	}
}
