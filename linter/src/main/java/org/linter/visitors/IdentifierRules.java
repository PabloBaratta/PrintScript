package org.linter.visitors;

import org.example.*;
import org.linter.Report;

import java.util.regex.Matcher;

public class IdentifierRules implements ASTVisitor {

	private final Case caseToCheck;
	private final Report report;
	private final String message;

	public IdentifierRules(Case caseToCheck, Report report) {
		this.caseToCheck = caseToCheck;
		this.report = report;
		this.message = "This identifier does not match the desired configuration" + caseToCheck;
	}
	@Override
	public void visit(Assignation assignation) throws Exception {}

	@Override
	public void visit(VariableDeclaration variableDeclaration) throws Exception {
		Identifier identifier = variableDeclaration.getIdentifier();
		Matcher matcher = caseToCheck.getRegex().matcher(identifier.getName());
		if (!matcher.matches()) {
			report.addLine(identifier.getPosition(), message);
		}
	}

	@Override
	public void visit(Identifier identifier) throws Exception {}

	@Override
	public void visit(TextLiteral textLiteral) {}

	@Override
	public void visit(NumericLiteral numericLiteral) {}

	@Override
	public void visit(Method method) throws Exception {}

	@Override
	public void visit(UnaryExpression unaryExpression) throws Exception {}

	@Override
	public void visit(BinaryExpression binaryExpression) throws Exception {}

	@Override
	public void visit(Program program) throws Exception {}

	@Override
	public void visit(Parenthesis parenthesis) throws Exception {}
}
