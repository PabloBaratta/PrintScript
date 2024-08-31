package org.example.interpreter;

import org.example.*;

import java.util.Map;
import java.util.Stack;

public class Interpreter {

	private final Validator validationVisitor = new Validator();
	private final Executor executionVisitor = new Executor();

	public void validate(ASTNode node) throws Exception {
		node.accept(validationVisitor);
	}

	public void visit(ASTNode node) throws Exception {
		node.accept(executionVisitor);
	}

	public Map<String, Variable> getEnvironment() {
		return executionVisitor.getEnvironment();
	}

	public Stack<Literal> getStack() {
		return executionVisitor.getStack();
	}

	public Validator getValidationVisitor() {
		return validationVisitor;
	}

	public Executor getExecutionVisitor() {
		return executionVisitor;
	}
}
