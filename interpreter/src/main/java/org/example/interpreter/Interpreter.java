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
	private final OutputEmitter outputEmitter;
	private final PrintScriptIterator<ASTNode> nodeIterator;


	public Interpreter(PrintScriptIterator<ASTNode> n,
					Map<String, ASTNodeHandler> h,
					InputProvider i,
					OutputEmitter o) {
		this.nodeIterator = n;
		this.handlers = h;
		this.inputProvider = i;
		this.outputEmitter = o;
		this.validationVisitor = new Validator(handlers, inputProvider);
		this.executionVisitor = new Executor(handlers, inputProvider, outputEmitter);
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

	public OutputEmitter getOutputEmitter() {
		return outputEmitter;
	}

	public PrintScriptIterator<ASTNode> getNodeIterator() {
		return nodeIterator;
	}
}
