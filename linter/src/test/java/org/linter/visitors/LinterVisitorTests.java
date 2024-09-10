package org.linter.visitors;

import org.example.*;
import org.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LinterVisitorTests {


	@Test
	public void successfulProgramVisitor() throws Exception {

		Position position = new Position(14, 7, 1, 14);
		Program program = new Program(
				List.of(new VariableDeclaration(
							new Identifier("a", new Position(4,1,1, 4)),
							new Type("string", new Position(6,6,1, 6)),
							Optional.of(new TextLiteral("america", position))),
						new Method(
							new Identifier("println", new Position(23,7,2, 23)),
							List.of(new Identifier("a", new Position(31, 1,2, 31))))));

		Report report = new Report();
		OneArgFunRules oneArgFunRules = new OneArgFunRules(true, report, "println");
		IdentifierRules identifierRules = new IdentifierRules(Case.CAMEL_CASE, report);

		LinterVisitor linterVisitor = new LinterVisitor(
				List.of(oneArgFunRules, identifierRules));

		linterVisitor.visit(program);

		assertTrue(report.getReportLines().isEmpty());
	}

	@Test
	public void doesNotChangeWithOtherNodes() throws Exception {
		Position position = new Position(0, 0, 0,0 );
		Identifier identifier = new Identifier("hi", position);
		Assignation assignation = new Assignation(identifier, identifier, position);
		TextLiteral lit = new TextLiteral("a", position);
		NumericLiteral num = new NumericLiteral(1.0, position);
		BinaryExpression bin = new BinaryExpression(identifier, "+", identifier);
		Parenthesis parenthesis = new Parenthesis(bin);
		UnaryExpression un = new UnaryExpression(bin, "+", position);

		CollectorVisitor collectorVisitor = new CollectorVisitor();
		LinterVisitor visitor = new LinterVisitor(List.of(collectorVisitor));

		visitor.visit(identifier);
		visitor.visit(lit);
		visitor.visit(num);
		visitor.visit(bin);
		visitor.visit(parenthesis);
		visitor.visit(un);

		assertTrue(collectorVisitor.getVisitedNodes().isEmpty());
	}


}
