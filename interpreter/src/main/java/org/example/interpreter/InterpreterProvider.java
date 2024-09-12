package org.example.interpreter;

import org.example.ASTNode;
import org.example.InputProvider;
import org.example.OutputEmitter;
import org.example.PrintScriptIterator;
import org.example.interpreter.handlers.ASTNodeHandler;
import org.example.interpreter.handlers.HandlerFactory;

import java.util.Map;

public class InterpreterProvider {

	public static Interpreter provideV10(PrintScriptIterator<ASTNode> nodeIterator,
										InputProvider inputProvider,
										OutputEmitter outputEmitter){
		Map<String, ASTNodeHandler> handlersmap = HandlerFactory.createHandlers("1.0");
		return new Interpreter(nodeIterator, handlersmap, inputProvider, outputEmitter);
	}

	public static Interpreter provideV11(PrintScriptIterator<ASTNode> nodeIterator,
										InputProvider inputProvider,
										OutputEmitter outputEmitter){
		Map<String, ASTNodeHandler> handlersmap = HandlerFactory.createHandlers("1.1");
		return new Interpreter(nodeIterator, handlersmap, inputProvider, outputEmitter);
	}
}
