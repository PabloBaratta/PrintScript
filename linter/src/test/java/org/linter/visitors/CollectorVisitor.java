package org.linter.visitors;

import org.example.*;

import java.util.LinkedList;
import java.util.List;

public class CollectorVisitor implements ASTVisitor {

	private final List<ASTNode> visitedNodes = new LinkedList<>();
	@Override
	public void visit(Assignation assignation) throws Exception {
		visitedNodes.add(assignation);
	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		visitedNodes.add(variableDeclaration);
	}

	@Override
	public void visit(Identifier identifier) throws Exception {
		visitedNodes.add(identifier);
	}

	@Override
	public void visit(TextLiteral textLiteral) {
		visitedNodes.add(textLiteral);
	}

	@Override
	public void visit(NumericLiteral numericLiteral) {
		visitedNodes.add(numericLiteral);
	}

	@Override
	public void visit(Method method) throws Exception {
		visitedNodes.add(method);
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {
		visitedNodes.add(unaryExpression);
	}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {
		visitedNodes.add(binaryExpression);
	}

	@Override
	public void visit(Program program) throws Exception {
		visitedNodes.add(program);
	}

	@Override
	public void visit(Parenthesis parenthesis) throws Exception {
		visitedNodes.add(parenthesis);
	}

	@Override
	public void visit(BooleanLiteral booleanLiteral) throws Exception {

	}

	@Override
	public void visit(IfStatement ifStatement) throws Exception {

	}

	public List<ASTNode> getVisitedNodes() {
		return visitedNodes;
	}
}
