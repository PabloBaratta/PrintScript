package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;

import java.util.Map;
import java.util.Optional;

public class ConstDeclarationHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		ConstDeclaration constDeclaration = (ConstDeclaration) node;
		Identifier identifier = constDeclaration.getIdentifier();
		Type type = constDeclaration.getType();

		Map<String, Variable> currentEnvironment = executor.getEnvironments().peek();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (currentEnvironment.containsKey(identifier.toString())) {
			throw new InterpreterException("Constant variable already declared", line, column);
		}

		Literal astNodeResult = executor.evaluateExpression(constDeclaration.getExpression());

		if (executor.typesMatch(astNodeResult, new Variable(type, Optional.empty(), true))) {
			Variable value = new Variable(type, Optional.of(astNodeResult), true);
			currentEnvironment.put(identifier.toString(), value);
		} else {
			int expLine = constDeclaration.getExpression().getPosition().getLine();
			int expColumn = constDeclaration.getExpression().getPosition().getColumn();
			throw new InterpreterException("Type mismatch", expLine, expColumn);
		}
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		ConstDeclaration constDeclaration = (ConstDeclaration) node;
		Identifier identifier = constDeclaration.getIdentifier();
		Type type = constDeclaration.getType();

		Map<String, Variable> currentEnvironment = validator.getEnvironments().peek();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (currentEnvironment.containsKey(identifier.toString())) {
			throw new InterpreterException("Constant variable already declared", line, column);
		}

		Literal astNodeResult = validator.evaluateExpression(constDeclaration.getExpression());

		if (validator.typesMatch(astNodeResult, new Variable(type, Optional.empty(), true))) {
			Variable value = new Variable(type, Optional.of(astNodeResult), true);
			currentEnvironment.put(identifier.toString(), value);
		} else {
			int expLine = constDeclaration.getExpression().getPosition().getLine();
			int expColumn = constDeclaration.getExpression().getPosition().getColumn();
			throw new InterpreterException("Type mismatch", expLine, expColumn);
		}
	}
}
