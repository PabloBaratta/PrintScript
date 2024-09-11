package org.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Formatter {

	private final Map<String, Rule> rules;
	private final PrintScriptIterator<ASTNode> nodes;

	public Formatter(Map<String, Rule> rules, PrintScriptIterator<ASTNode> nodes) {
		this.rules = rules;
		this.nodes = nodes;
	}

	public String format() throws Exception {
		StringBuilder result = new StringBuilder();
		while (nodes.hasNext()) {
			ASTNode child = nodes.getNext();
			formatNode(child, result, 0);
		}
		return result.toString();
	}

	private void formatNode(ASTNode child, StringBuilder result, int nestingLevel) {
		switch (child) {
			case Program program:
				List<ASTNode> children = program.getChildren();
				for (ASTNode child1 : children) {
					formatNode(child1, result, nestingLevel);
				}
				break;
			case VariableDeclaration variableDeclaration:
				result.append(formatVarDec(variableDeclaration));
				break;
			case ConstDeclaration constDeclaration:
				result.append(formatConstDec(constDeclaration));
				break;
			case Assignation assignation:
				result.append(formatAssignation(assignation));
				break;
			case Method method:
				result.append(formatMethod(method, nestingLevel));
				break;
			case IfStatement ifStatement:
				result.append(formatIfStatement(ifStatement, nestingLevel));
				break;
			default:
				String s = "Unknown node type: ";
				throw new IllegalArgumentException(s + child.getClass());
		}
	}

	private StringBuilder formatIfStatement(IfStatement ifStatement, int nestingLevel) {
		Expression condition = ifStatement.getCondition();
		List<ASTNode> thenBlock = ifStatement.getThenBlock();
		List<ASTNode> elseBlock = ifStatement.getElseBlock();
		StringBuilder result = new StringBuilder();
		result.append("if (").append(condition.toString()).append(") {\n");
		formatChildren(thenBlock, result, nestingLevel + 1);
		checkSpaces(result, nestingLevel);
		result.append("}");
		if (!elseBlock.isEmpty()) {
			result.append(" else {\n");
			formatChildren(elseBlock, result, nestingLevel + 1);
			checkSpaces(result, nestingLevel);
			result.append("}\n");
		} else {
			result.append("\n");
		}
		return result;
	}

	private void formatChildren(List<ASTNode> children, StringBuilder result, int nestingLevel) {
		for (ASTNode node : children) {
			checkSpaces(result, nestingLevel);
			formatNode(node, result, nestingLevel);
		}
	}

	private StringBuilder formatConstDec(ConstDeclaration constDeclaration) {
		Identifier identifier = constDeclaration.getIdentifier();
		Type type = constDeclaration.getType();
		Expression expression = constDeclaration.getExpression();
		StringBuilder result = new StringBuilder();
		result.append("const ").append(identifier.toString());
		checkRule("spaceBeforeColon", " ", result);
		result.append(":");
		checkRule("spaceAfterColon", " ", result);
		result.append(type.getTypeName());
		checkRule("spaceBeforeAssignation", " ", result);
		result.append("=");
		checkRule("spaceAfterAssignation", " ", result);
		result.append(expression.toString());
		result.append(";\n");
		return result;
	}

	private StringBuilder formatMethod(Method method, int nestingLevel) {
		Identifier identifier = method.getVariable();
		List<Expression> arguments = method.getArguments();
		StringBuilder result = new StringBuilder();
		if (identifier.toString().equals("println")) {
			checkNewLines(result);
		}
		checkSpaces(result, nestingLevel);
		result.append(identifier.toString()).append("(");
		formatArguments(arguments, result);
		result.append(");\n");
		return result;
	}

	private static void formatArguments(List<Expression> arguments, StringBuilder result) {
		for (int i = 0; i < arguments.size(); i++) {
			result.append(arguments.get(i).toString());
			if (i < arguments.size() - 1) {
				result.append(", ");
			}
		}
	}

	private void checkNewLines(StringBuilder result) {
		if (rules.get("newLineBeforePrintln").getRule()) {
			int qty = rules.get("newLineBeforePrintln").getQty().get();
			result.append("\n".repeat(Math.max(0, qty)));
		}
	}

	private void checkSpaces(StringBuilder result, int nestingLevel) {
		if (!rules.containsKey("indentation")) {
			return;
		}
		if (rules.get("indentation").getRule()) {
			int qty = rules.get("indentation").getQty().get() * nestingLevel;
			result.append(" ".repeat(Math.max(0, qty)));
		}
	}

	private void checkRule(String rule, String append, StringBuilder result) {
		if (rules.get(rule).getRule()) {
			result.append(append);
		}
	}

	private StringBuilder formatAssignation(Assignation assignation) {
		Identifier identifier = assignation.getIdentifier();
		Expression expression = assignation.getExpression();
		StringBuilder result = new StringBuilder();
		result.append(identifier.toString());
		checkRule("spaceBeforeAssignation", " ", result);
		result.append("=");
		checkRule("spaceAfterAssignation", " ", result);
		result.append(expression.toString());
		result.append(";\n");
		return result;
	}

	private StringBuilder formatVarDec(VariableDeclaration varDec) {
		Identifier identifier = varDec.getIdentifier();
		Type type = varDec.getType();
		Optional<Expression> expression = varDec.getExpression();
		StringBuilder result = new StringBuilder();
		result.append("let ").append(identifier.toString());
		checkRule("spaceBeforeColon", " ", result);
		result.append(":");
		checkRule("spaceAfterColon", " ", result);
		result.append(type.getTypeName());
		if (expression.isPresent()) {
			checkRule("spaceBeforeAssignation", " ", result);
			result.append("=");
			checkRule("spaceAfterAssignation", " ", result);
			result.append(expression.get().toString());
		}
		result.append(";\n");
		return result;
	}
}
