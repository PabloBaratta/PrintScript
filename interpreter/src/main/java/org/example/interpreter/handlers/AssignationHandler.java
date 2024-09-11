package org.example.interpreter.handlers;

import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;

import java.util.Map;

public class AssignationHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Assignation assignation = (Assignation) node;
		Identifier identifier = assignation.getIdentifier();
		Expression exp = assignation.getExpression();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		int environmentIndex = executor.findEnvironmentIndex(identifier.toString(), line, column);
		Map<String, Variable> env = executor.getEnvironments().get(environmentIndex);
		Variable variable = env.get(identifier.toString());

		if (variable.isConst()) {
			throw new InterpreterException("Cannot reassign a constant variable", line, column);
		}

		Literal astNodeResult = executor.evaluateExpression(exp);

		if (executor.typesMatch(astNodeResult, variable)) {
			variable.setLiteral(astNodeResult);
			env.put(identifier.toString(), variable);
		} else {
			int lineExp = exp.getPosition().getLine();
			int columnExp = exp.getPosition().getColumn();
			throw new InterpreterException("Type mismatch", lineExp, columnExp);
		}
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		Assignation assignation = (Assignation) node;
		Identifier identifier = assignation.getIdentifier();
		Expression exp = assignation.getExpression();

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (!validator.getEnvironment().containsKey(identifier.toString())) {
			throw new InterpreterException("Variable not declared", line, column);
		}

		Literal astNodeResult = validator.evaluateExpression(exp);
		Variable variable = validator.getEnvironment().get(identifier.toString());

		if (variable.isConst()) {
			throw new InterpreterException("Cannot reassign a constant variable", line, column);
		}

		if (validator.typesMatch(astNodeResult, variable)) {
			variable.setLiteral(astNodeResult);
			validator.getEnvironment().put(identifier.toString(), variable);
		} else {
			int lineExp = exp.getPosition().getLine();
			int columnExp = exp.getPosition().getColumn();
			throw new InterpreterException("Type mismatch", lineExp, columnExp);
		}
	}
}
