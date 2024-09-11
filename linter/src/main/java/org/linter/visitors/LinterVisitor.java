package org.linter.visitors;

import org.example.*;

import java.util.List;

public class LinterVisitor implements ASTVisitor {


	private final List<ASTVisitor> visitors;

	public LinterVisitor(List<ASTVisitor> visitors) {
		this.visitors = visitors;
	}


	@Override
	public void visit(Assignation assignation) throws Exception {
		for (ASTVisitor visitor : visitors) {
			visitor.visit(assignation);
		}
	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		for (ASTVisitor visitor : visitors) {
			visitor.visit(variableDeclaration);
		}
	}

	@Override
	public void visit(Identifier identifier) throws Exception {}

	@Override
	public void visit(TextLiteral textLiteral) {}

	@Override
	public void visit(NumericLiteral numericLiteral) {}

	@Override
	public void visit(Method method) throws Exception {
		for (ASTVisitor visitor : visitors) {
			visitor.visit(method);
		}
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {}

	@Override
	public void visit(Program program) throws Exception {
		List<ASTNode> children = program.getChildren();
		for (ASTNode child : children) {
			child.accept(this);
		}
	}

	public void visitAll(PrintScriptIterator<ASTNode> iterator) throws Exception {

		while (iterator.hasNext()) {
			ASTNode node = iterator.getNext();
			node.accept(this);
		}
	}

	@Override
	public void visit(Parenthesis parenthesis) throws Exception {}

	@Override
	public void visit(BooleanLiteral booleanLiteral) throws Exception {}

	@Override
	public void visit(IfStatement ifStatement) throws Exception {
		for (ASTVisitor visitor : visitors) {
			visitor.visit(ifStatement);
		}
	}

	@Override
	public void visit(ConstDeclaration constDeclaration) throws Exception {
		for (ASTVisitor visitor : visitors) {
			visitor.visit(constDeclaration);
		}
	}
}
