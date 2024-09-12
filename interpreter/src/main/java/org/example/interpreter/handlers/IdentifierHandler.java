package org.example.interpreter.handlers;

import org.example.ASTNode;
import org.example.Identifier;
import org.example.Literal;
import org.example.Variable;
import org.example.interpreter.Executor;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;

import java.util.Optional;

public class IdentifierHandler implements ASTNodeHandler{
	@Override
	public void handleExecution(ASTNode node, Executor executor) throws Exception {
		Identifier identifier = (Identifier) node;
		String identifierName = identifier.getName();

		Optional<Variable> variableOpt = executor.findVariable(identifierName);

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (variableOpt.isEmpty()) {
			throw new InterpreterException("Undeclared variable", line, column);
		}

		Variable variable = variableOpt.get();
		Optional<Literal> optionalExpression = variable.getLiteral();

		if (optionalExpression.isEmpty()) {
			throw new InterpreterException("Variable declared but not assigned", line, column);
		}

		executor.getStack().push(optionalExpression.get());
	}

	@Override
	public void handleValidation(ASTNode node, Validator validator) throws Exception {
		Identifier identifier = (Identifier) node;
		String identifierName = identifier.getName();

		Optional<Variable> variableOpt = validator.findVariable(identifierName);

		int line = identifier.getPosition().getLine();
		int column = identifier.getPosition().getColumn();

		if (variableOpt.isEmpty()) {
			throw new InterpreterException("Undeclared variable", line, column);
		}

		Variable variable = variableOpt.get();
		Optional<Literal> optionalExpression = variable.getLiteral();

		if (optionalExpression.isEmpty()) {
			throw new Exception("Variable declared but not assigned");
		}

		validator.getStack().push(optionalExpression.get());
	}
}
