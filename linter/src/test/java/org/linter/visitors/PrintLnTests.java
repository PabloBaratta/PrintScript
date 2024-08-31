package org.linter.visitors;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrintLnTests {

	@Test
	public void unsuccessfulPrintLn() throws Exception {
		function("println");
	}

	@Test
	public void unsuccessfulReadInput() throws Exception {
		function("readInput");
	}

	private static void function(String methodName) throws Exception {
		//println(a+a)
		Expression arguments = new BinaryExpression(
				new TextLiteral("a", new Position(8, 1, 1, 8)),
				"+",
				new TextLiteral("a", new Position(10, 1, 1, 10))
		);

		assertUnsuccessfulScenario(arguments, methodName);

		//println(+a)
		arguments = new UnaryExpression(
				new TextLiteral("a", new Position(8, 1, 1, 8)),
				"+",
				new Position(7, 1, 1, 7));

		assertUnsuccessfulScenario(arguments, methodName);

		//println((a))
		arguments = new Parenthesis(
				new TextLiteral("a", new Position(8, 1, 1, 8)));

		assertUnsuccessfulScenario(arguments, methodName);
	}

	@Test
	public void printLineSuccessful() throws Exception {

		successfulScenarios("println");

	}

	@Test
	public void readInputSuccessful() throws Exception {

		successfulScenarios("readInput");

	}


	private static void successfulScenarios(String methodName) throws Exception {
		Expression arguments = new TextLiteral("a", new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments, methodName);

		arguments = new NumericLiteral(5.0, new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments, methodName);

		arguments = new Identifier("a", new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments, methodName);
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
		OneArgFunRules printLnRules = new OneArgFunRules(true, report, "println");

		printLnRules.visit(variableDeclaration);

		assertTrue(report.getReportLines().isEmpty());
	}



	private static void assertUnsuccessfulScenario(Expression arguments, String methodName) throws Exception {
		Method program = new Method(
				new Identifier(methodName, new Position(0,7,1, 0)),
				List.of(
						arguments));

		Report report = new Report();
		OneArgFunRules printLnRule = new OneArgFunRules(true, report, methodName);

		printLnRule.visit(program);
		assertFalse(report.getReportLines().isEmpty());
	}

	private static void assertSuccessfulScenario(Expression arguments, String methodName) throws Exception {
		Method program = new Method(
				new Identifier(methodName, new Position(0,7,1, 0)),
				List.of(
						arguments));

		Report report = new Report();
		OneArgFunRules printLnRule = new OneArgFunRules(true, report, methodName);

		printLnRule.visit(program);
		assertTrue(report.getReportLines().isEmpty());
	}
}
