package org.linter.visitors;

import org.example.*;
import org.linter.Report;

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

	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {

	}

	@Override
	public void visit(Identifier identifier) throws Exception {

	}

	@Override
	public void visit(TextLiteral textLiteral) {

	}

	@Override
	public void visit(NumericLiteral numericLiteral) {

	}

	@Override
	public void visit(Method method) throws Exception {
		if (!shouldCheck) {
			return;
		}
		if (!method.getVariable().getName().equals(this.methodName)) {
			return;
		}
		Expression first = method.getArguments().getFirst();
		first.accept(this);
	}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {
		if (!shouldCheck) {
			return;
		}
		report.addLine(unaryExpression.getPosition(), error);
	}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {
		if (!shouldCheck) {
			return;
		}
		report.addLine(binaryExpression.getPosition(), error);
	}

	@Override
	public void visit(Program program) throws Exception {

	}

	@Override
	public void visit(Parenthesis parenthesis) throws Exception {
		if (!shouldCheck) {
			return;
		}
		report.addLine(parenthesis.getPosition(), error);
	}
}
