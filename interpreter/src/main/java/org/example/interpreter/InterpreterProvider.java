//package org.example.interpreter;
//
//import org.example.ASTNode;
//import org.example.PrintScriptIterator;
//import org.example.PrintScriptIteratorTest;
//import org.example.interpreter.handlers.ASTNodeHandler;
//import org.example.interpreter.handlers.HandlerFactory;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class InterpreterProvider {
//
//	public static Interpreter provide10(PrintScriptIterator<ASTNode> nodeIterator){
//		Map<String, ASTNodeHandler> handlersmap = HandlerFactory.createHandlers("1.0");
//		return new Interpreter(nodeIterator, handlersmap);
//	}
//
//	public static Interpreter provide11(PrintScriptIterator<ASTNode> nodeIterator){
//		Map<String, ASTNodeHandler> handlersmap = HandlerFactory.createHandlers("1.1");
//		return new Interpreter(nodeIterator, handlersmap);
//	}
//}
