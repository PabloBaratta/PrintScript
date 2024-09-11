package org.example.interpreter;

import org.example.*;
import org.example.interpreter.handlers.ASTNodeHandler;

import java.util.Map;
import java.util.Stack;

public class Interpreter {

	private final Validator validationVisitor;
	private final Executor executionVisitor;
	private final Map<String, ASTNodeHandler> handlers;
	private final InputProvider inputProvider;
	private final OutputCapture outputCapture;
	private final PrintScriptIterator<ASTNode> nodeIterator;


	public Interpreter(PrintScriptIterator<ASTNode> n,
					Map<String, ASTNodeHandler> h,
					InputProvider i,
					OutputCapture o) {
		this.nodeIterator = n;
		this.handlers = h;
		this.inputProvider = i;
		this.outputCapture = o;
		this.validationVisitor = new Validator(handlers, inputProvider);
		this.executionVisitor = new Executor(handlers, inputProvider, outputCapture);
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

	public Map<String, ASTNodeHandler> getHandlers() {
		return handlers;
	}

	public InputProvider getInputProvider() {
		return inputProvider;
	}

	public OutputCapture getOutputCapture() {
		return outputCapture;
	}

	public PrintScriptIterator<ASTNode> getNodeIterator() {
		return nodeIterator;
	}
}
