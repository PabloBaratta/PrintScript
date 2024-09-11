package org.example.interpreter;

import org.example.*;
import org.example.interpreter.handlers.ASTNodeHandler;

import java.util.Map;
import java.util.Stack;

public class Interpreter {

	private final Validator validationVisitor;
	private final Executor executionVisitor;
	private final Map<String, ASTNodeHandler> handlers;
	private final PrintScriptIterator<ASTNode> nodeIterator;

	public Interpreter(PrintScriptIterator<ASTNode> nodeIterator, Map<String, ASTNodeHandler> handlers) {
		this.nodeIterator = nodeIterator;
		this.handlers = handlers;

		this.validationVisitor = new Validator(handlers);
		this.executionVisitor = new Executor(handlers);
	}

	public void validate() throws Exception {
		while (nodeIterator.hasNext()) {
			ASTNode validatonNode = nodeIterator.getNext();
			validatonNode.accept(validationVisitor);
		}
	}

	public void execute() throws Exception {
		while (nodeIterator.hasNext()) {
			ASTNode executionNode = nodeIterator.getNext();
			executionNode.accept(executionVisitor);
		}
	}


	public Map<String, Variable> getExecutorEnvironment() {
		return executionVisitor.getEnvironment();
	}
	public Map<String, Variable> getValidatorEnvironment() {
		return validationVisitor.getEnvironment();
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
