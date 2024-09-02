package org.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Formatter {

	private final Map<String, Rule> rules;

	public Formatter(Map<String, Rule> rules) {
		this.rules = rules;
	}

	public String format(Program program) throws Exception {
		StringBuilder result = new StringBuilder();
		formatNode(program, result);
		return result.toString();
	}

	private void formatNode(ASTNode child, StringBuilder result) {
		switch (child) {
			case Program program:
				List<ASTNode> children = program.getChildren();
				for (ASTNode child1 : children) {
					formatNode(child1, result);
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
				result.append(formatMethod(method));
				break;
			case IfStatement ifStatement:
				result.append(formatIfStatement(ifStatement));
				break;
			default:
				String s = "Unknown node type: ";
				throw new IllegalArgumentException(s + child.getClass());
		}
	}

	private StringBuilder formatIfStatement(IfStatement ifStatement) {
		Expression condition = ifStatement.getCondition();
		List<ASTNode> thenBlock = ifStatement.getThenBlock();
		List<ASTNode> elseBlock = ifStatement.getElseBlock();
		StringBuilder result = new StringBuilder();
		result.append("if (").append(condition.toString()).append(") { \n");
		formatChildren(thenBlock, result);
		result.append("}");
		if (elseBlock.isEmpty()){
			result.append("\n");
			return result;
		}
		result.append(" else { \n");
		formatChildren(elseBlock, result);
		result.append("}\n");
		return result;
	}

	private void formatChildren(List<ASTNode> thenBlock, StringBuilder result) {
		for (ASTNode node : thenBlock) {
			checkSpaces(result);
			formatNode(node, result);
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

	private StringBuilder formatMethod(Method method) {
		Identifier identifier = method.getVariable();
		List<Expression> arguments = method.getArguments();
		StringBuilder result = new StringBuilder();
		if (identifier.toString().equals("println")) {
			checkNewLines(result);
			result.append(identifier.toString()).append("(");
			addNewLines(arguments, result);
			result.append(");\n");
		}
		return result;
	}

	private static void addNewLines(List<Expression> arguments, StringBuilder result) {
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

	private void checkSpaces(StringBuilder result) {
		if (rules.get("indentation").getRule()) {
			int qty = rules.get("indentation").getQty().get();
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
