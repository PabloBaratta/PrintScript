package org.linter.visitors;

import org.example.*;
import org.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OneArgFunTests {

	@Test
	public void unsuccessfulPrintLn() throws Exception {
		unsuccessfulScenarios("println");
	}

	@Test
	public void unsuccessfulReadInput() throws Exception {
		unsuccessfulScenarios("readInput");
	}

	private static void unsuccessfulScenarios(String methodName) throws Exception {
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
		Expression arguments = new TextLiteral("a", new Position(8, 1, 1, 8));
		assertSuccessfulScenarioInVarDecl("readInput", arguments);
	}


	private static void successfulScenarios(String methodName) throws Exception {
		Expression arguments = new TextLiteral("a", new Position(8, 1, 1, 8));

		assertSuccessfulScenario(arguments, methodName);

		arguments = new NumericLiteral(BigDecimal.valueOf(5.0), new Position(8, 1, 1, 8));

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

	@Test
	public void testIfStatement() throws Exception {
		Report report = new Report();
		IdentifierRules identifierRules = new IdentifierRules(Case.CAMEL_CASE, report);

		Identifier condition = new Identifier("condition", new Position(0, 0, 0, 0));
		Identifier thenIdentifier = new Identifier("thenVar", new Position(1, 0, 0, 1));
		Identifier elseIdentifier = new Identifier("elseVar", new Position(2, 0, 0, 2));

		Type string = new Type("string", new Position(1, 6, 1, 1));
		VariableDeclaration thenVarDecl = new VariableDeclaration(thenIdentifier, string, Optional.empty());
		Type string2 = new Type("string", new Position(2, 6, 1, 2));
		VariableDeclaration elseVarDecl = new VariableDeclaration(elseIdentifier, string2, Optional.empty());

		IfStatement ifStatement = new IfStatement(
				condition,
				List.of(thenVarDecl),
				List.of(elseVarDecl),
				new Position(0, 0, 0, 0)
		);

		ifStatement.accept(identifierRules);

		assertTrue(report.getReportLines().isEmpty(), "Report should be empty for valid identifiers");
	}

	@Test
	public void testIfStatementWithBinaryCondition() throws Exception {
		Report report = new Report();
		OneArgFunRules oneArgFunRules = new OneArgFunRules(true, report, "println");

		// Create a BinaryExpression for the condition using the > operator
		BinaryExpression condition = new BinaryExpression(
				new Identifier("a", new Position(0, 0, 0, 0)),
				">",
				new Identifier("b", new Position(0, 0, 0, 0))
		);

		Identifier thenIdentifier = new Identifier("thenVar", new Position(1, 0, 0, 1));
		Identifier elseIdentifier = new Identifier("elseVar", new Position(2, 0, 0, 2));

		Identifier println = new Identifier("println", new Position(1, 7, 1, 1));
		Method thenMethod = new Method(println, List.of(thenIdentifier));
		Identifier println1 = new Identifier("println", new Position(2, 7, 1, 2));
		Method elseMethod = new Method(println1, List.of(elseIdentifier));

		IfStatement ifStatement = new IfStatement(
				condition,
				List.of(thenMethod),
				List.of(elseMethod),
				new Position(0, 0, 0, 0)
		);

		ifStatement.accept(oneArgFunRules);

		assertTrue(report.getReportLines().isEmpty(), "Report should be empty for valid method calls");
	}



	private static void assertUnsuccessfulScenario(Expression arguments, String methodName) throws Exception {
		Method program = getMethod(arguments, methodName);

		Report report = new Report();
		OneArgFunRules printLnRule = new OneArgFunRules(true, report, methodName);

		printLnRule.visit(program);
		assertFalse(report.getReportLines().isEmpty());
	}

	private static void assertSuccessfulScenario(Expression arguments, String methodName) throws Exception {
		Method program = getMethod(arguments, methodName);
		visitProgram(methodName, program);
	}

	private static void visitProgram(String methodName, ASTNode program) throws Exception {
		Report report = new Report();
		OneArgFunRules funRule = new OneArgFunRules(true, report, methodName);

		program.accept(funRule);
		assertTrue(report.getReportLines().isEmpty());
	}

	private static Method getMethod(Expression arguments, String methodName) {
		return new Method(
				new Identifier(methodName, new Position(0, 7, 1, 0)),
				List.of(
						arguments));
	}

	private static void assertSuccessfulScenarioInVarDecl(String methodName, Expression arguments)
			throws Exception {
		VariableDeclaration variableDeclaration = new VariableDeclaration(
				new Identifier("", new Position(0,0,0,0)),
				new Type("", new Position(0,0,0,0)),
				Optional.of(getMethod(arguments, methodName))
		);

		visitProgram(methodName, variableDeclaration);
	}
}
