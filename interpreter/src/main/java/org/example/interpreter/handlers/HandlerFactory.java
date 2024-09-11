package org.example.interpreter.handlers;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {

	public static Map<String, ASTNodeHandler> createHandlers(String version) {
		Map<String, ASTNodeHandler> handlers = new HashMap<>();

		if ("1.0".equals(version)) {
			handlers.put("VariableDeclaration", new VariableDeclarationHandler());
			handlers.put("Assignation", new AssignationHandler());
			handlers.put("BinaryExpression", new BinaryExpressionHandler());
			handlers.put("UnaryExpression", new UnaryExpressionHandler());
			handlers.put("Identifier", new IdentifierHandler());
			handlers.put("NumericLiteral", new NumericLiteralHandler());
			handlers.put("TextLiteral", new TextLiteralHandler());
			handlers.put("Parenthesis", new ParenthesisHandler());
			handlers.put("println", new PrintlnHandler());
		} else if ("1.1".equals(version)) {
			handlers.put("VariableDeclaration", new VariableDeclarationHandler());
			handlers.put("Assignation", new AssignationHandler());
			handlers.put("BinaryExpression", new BinaryExpressionHandler());
			handlers.put("UnaryExpression", new UnaryExpressionHandler());
			handlers.put("Identifier", new IdentifierHandler());
			handlers.put("NumericLiteral", new NumericLiteralHandler());
			handlers.put("TextLiteral", new TextLiteralHandler());
			handlers.put("Parenthesis", new ParenthesisHandler());
			handlers.put("println", new PrintlnHandler());
			handlers.put("IfStatement", new IfStatementHandler());
			handlers.put("BooleanLiteral", new BooleanHandler());
			handlers.put("ConstDeclaration", new ConstDeclarationHandler());
			handlers.put("readInput", new ReadInputHandler());
			handlers.put("readEnv", new ReadEnvHandler());

		} else {
			throw new IllegalArgumentException("Unsupported version: " + version);
		}

		return handlers;
	}
}
