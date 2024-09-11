package org.example.interpreter;

import org.example.ASTNode;
import org.example.PrintScriptIterator;
import org.example.PrintScriptIteratorTest;
import org.example.interpreter.handlers.ASTNodeHandler;
import org.example.interpreter.handlers.HandlerFactory;

import java.util.ArrayList;
import java.util.Map;

public class InterpreterProvider {

	public static Interpreter provideV10(PrintScriptIterator<ASTNode> nodeIterator, InputProvider inputProvider){
		Map<String, ASTNodeHandler> handlersmap = HandlerFactory.createHandlers("1.0");
		OutputCapture outputCapture = new OutputCapture();
		return new Interpreter(nodeIterator, handlersmap, inputProvider, outputCapture);
	}

	public static Interpreter provideV11(PrintScriptIterator<ASTNode> nodeIterator, InputProvider inputProvider){
		Map<String, ASTNodeHandler> handlersmap = HandlerFactory.createHandlers("1.1");
		OutputCapture outputCapture = new OutputCapture();
		return new Interpreter(nodeIterator, handlersmap, inputProvider, outputCapture);
	}
}
