package org.linter.visitors;

import org.example.*;
import org.linter.Report;

import java.util.List;
import java.util.Optional;

public class OneArgFunRules implements ASTVisitor {

	public final String ERROR_MESSAGE =
			" should only be called with a identifier or a literal";
	private final Report report;
	private final boolean shouldCheck;
	private final String error;
	private final String methodName;

	public OneArgFunRules(boolean shouldCheck, Report report, String methodName) {
		this.report = report;
		this.shouldCheck = shouldCheck;
		this.methodName = methodName;
		this.error = methodName + ERROR_MESSAGE;
	}


	@Override
	public void visit(Assignation assignation) throws Exception {
		assignation.getExpression().accept(this);
	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		Optional<Expression> optionalExpression = variableDeclaration.getExpression();

		if (optionalExpression.isEmpty()) {
			return;
		}

		optionalExpression.get().accept(this);
	}

	@Override
	public void visit(Identifier identifier) throws Exception {}

	@Override
	public void visit(TextLiteral textLiteral) {}

	@Override
	public void visit(NumericLiteral numericLiteral) {}

	@Override
	public void visit(Method method) throws Exception {
		if (!shouldCheck) {
			return;
		}
		if (!method.getVariable().getName().equals(this.methodName)) {
			return;
		}
		Expression first = method.getArguments().getFirst();

		switch (first) {
			case UnaryExpression u -> processCompoundExpression(u);
			case BinaryExpression be -> processCompoundExpression(be);
			case Parenthesis p -> processCompoundExpression(p);
			default -> {}
		}
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {
		unaryExpression.getArgument().accept(this);
	}

	private void processCompoundExpression(Expression expression) {
		if (!shouldCheck) {
			return;
		}
		report.addLine(expression.getPosition(), error);
	}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {
		binaryExpression.getLeft().accept(this);
		binaryExpression.getRight().accept(this);
	}

	@Override
	public void visit(Program program) throws Exception {}

	@Override
	public void visit(Parenthesis parenthesis) throws Exception {
		parenthesis.getExpression().accept(this);
	}

	@Override
	public void visit(BooleanLiteral booleanLiteral) throws Exception {

	}

	@Override
	public void visit(IfStatement ifStatement) throws Exception {
		ifStatement.getCondition().accept(this);
		List<ASTNode> thenBlock = ifStatement.getThenBlock();
		List<ASTNode> elseBlock = ifStatement.getElseBlock();
		visitBlock(thenBlock);
		visitBlock(elseBlock);
	}

	@Override
	public void visit(ConstDeclaration constDeclaration) throws Exception {
		constDeclaration.getExpression().accept(this);
	}

	private void visitBlock(List<ASTNode> block) throws Exception {
		for (ASTNode node : block){
			node.accept(this);
		}
	}
}
