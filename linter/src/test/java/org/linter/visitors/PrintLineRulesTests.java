package org.linter.visitors;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrintLineRulesTests {

	@Test
	public void unsuccessfulPrintLn() throws Exception {

		//println(a+a)
		Expression arguments = new BinaryExpression(
				new TextLiteral("a", new Position(8, 1, 1, 8)),
				"+",
				new TextLiteral("a", new Position(10, 1, 1, 10))
		);

		assertUnsuccessfulScenario(arguments);

		//println(+a)
		arguments = new UnaryExpression(
				new TextLiteral("a", new Position(8, 1, 1, 8)),
				"+",
				new Position(7, 1, 1, 7));

		assertUnsuccessfulScenario(arguments);

		//println((a))
		arguments = new Parenthesis(
				new TextLiteral("a", new Position(8, 1, 1, 8)));

		assertUnsuccessfulScenario(arguments);
	}

	@Test
	public void successfulScenarios() throws Exception {

		Expression arguments = new TextLiteral("a", new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments);

		arguments = new NumericLiteral(5.0, new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments);

		arguments = new Identifier("a", new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments);

	}

	@Test
	public void notPertinentStatements() throws Exception {
		Position varPosition = new Position(4, 1, 1, 4);
		Identifier identifier = new Identifier("a", varPosition);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier,
				new Type("string", new Position(7, 6, 1, 7)),
				Optional.of(new BinaryExpression(
						new TextLiteral("a", new Position(8, 1, 1, 8)),
						"+",
						new TextLiteral("a", new Position(10, 1, 1, 10))
				)));

		Report report = new Report();
		PrintLineRules printLineRules = new PrintLineRules(true, report);

		printLineRules.visit(variableDeclaration);

		assertTrue(report.getReportLines().isEmpty());
	}



	private static void assertUnsuccessfulScenario(Expression arguments) throws Exception {
		Method program = new Method(
				new Identifier("println", new Position(0,7,1, 0)),
				List.of(
						arguments));

		Report report = new Report();
		PrintLineRules printLineRules = new PrintLineRules(true, report);

		printLineRules.visit(program);
		assertFalse(report.getReportLines().isEmpty());
	}

	private static void assertSuccessfulScenario(Expression arguments) throws Exception {
		Method program = new Method(
				new Identifier("println", new Position(0,7,1, 0)),
				List.of(
						arguments));

		Report report = new Report();
		PrintLineRules printLineRules = new PrintLineRules(true, report);

		printLineRules.visit(program);
		assertTrue(report.getReportLines().isEmpty());
	}
}
