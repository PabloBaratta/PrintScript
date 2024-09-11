package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;

import java.util.Map;
import java.util.Optional;

public class VariableDeclarationHandler implements ASTNodeHandler{

	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		VariableDeclaration variableDeclaration = (VariableDeclaration) node;
		Identifier identifier = variableDeclaration.getIdentifier();
		Type type = variableDeclaration.getType();

		Map<String, Variable> currentEnvironment = executor.getEnvironments().peek();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (currentEnvironment.containsKey(identifier.toString())) {
			throw new InterpreterException("Variable already declared", line, column);
		}

		Optional<Expression> optionalExpression = variableDeclaration.getExpression();
		if (optionalExpression.isPresent()) {
			Expression exp = optionalExpression.get();
			Literal astNodeResult = executor.evaluateExpression(exp);

			if (executor.typesMatch(astNodeResult, new Variable(type, Optional.empty(), false))) {
				Variable value = new Variable(type, Optional.of(astNodeResult), false);
				currentEnvironment.put(identifier.toString(), value);
			} else {
				int expLine = exp.getPosition().getLine();
				int expColumn = exp.getPosition().getColumn();
				throw new InterpreterException("Type mismatch", expLine, expColumn);
			}
		} else {
			currentEnvironment.put(identifier.toString(), new Variable(type, Optional.empty(), false));
		}
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		VariableDeclaration variableDeclaration = (VariableDeclaration) node;
		Identifier identifier = variableDeclaration.getIdentifier();
		Type type = variableDeclaration.getType();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (validator.getEnvironment().containsKey(identifier.toString())) {
			throw new InterpreterException("Variable already declared", line, column);
		}

		Variable value1 = new Variable(type, Optional.empty(), false);
		if (variableDeclaration.getExpression().isPresent()) {
			Literal astNodeResult = validator.evaluateExpression(variableDeclaration.getExpression().get());
			if (validator.typesMatch(astNodeResult, value1)) {
				Variable value = new Variable(type, Optional.of(astNodeResult), false);
				validator.getEnvironment().put(identifier.toString(), value);
			} else {
				throw new InterpreterException("Type mismatch", line, column);
			}
		} else {
			validator.getEnvironment().put(identifier.toString(), value1);
		}
	}
}
