package org.linter.visitors;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierRulesTest {

	@Test
	public void successfulCamelCaseScenarios() throws Exception {

		//not supporting aA3a#

		String identifierString = "__myVariable1";
		assertVarDeclCamelCase(identifierString);

		identifierString = "__variable2";
		assertVarDeclCamelCase(identifierString);

		identifierString = "camelCase";
		assertVarDeclCamelCase(identifierString);

		identifierString = "__a1";
		assertVarDeclCamelCase(identifierString);

		identifierString = "variable3";
		assertVarDeclCamelCase(identifierString);
	}

	@Test
	public void unsuccessfulCamelCaseScenarios() throws Exception {

		//not supporting aA3a#

		String identifierString = "_singleUnderscore";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "s_";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "this_is_snake_case";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "PascalCase";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "variable-with-dash";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "1InvalidStartWithNumber";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "no spaces allowed";
		assertVarDeclCamelCaseFalse(identifierString);

		identifierString = "$invalidSymbol";
		assertVarDeclCamelCaseFalse(identifierString);
	}

	@Test
	public void successfulSnakeCaseScenarios() throws Exception {


		String identifierString = "variable_name";
		assertVarDeclSnakeCase(identifierString);

		identifierString = "variable_name_with_numbers_123";
		assertVarDeclSnakeCase(identifierString);

		identifierString = "a";
		assertVarDeclSnakeCase(identifierString);

		identifierString = "snake_case";
		assertVarDeclSnakeCase(identifierString);

		identifierString = "another_test_case";
		assertVarDeclSnakeCase(identifierString);
	}

	@Test
	public void unsuccessfulSnakeCaseScenarios() throws Exception {

		String identifierString = "_leading_underscore";
		assertVarDeclSnakeCaseFalse(identifierString);

		identifierString = "trailing_underscore_";
		assertVarDeclSnakeCaseFalse(identifierString);

		identifierString = "multiple__underscores";
		assertVarDeclSnakeCaseFalse(identifierString);

		identifierString = "123_variable";
		assertVarDeclSnakeCaseFalse(identifierString);

		identifierString = "mixedCASE";
		assertVarDeclSnakeCaseFalse(identifierString);

		identifierString = "variable-name";
		assertVarDeclSnakeCaseFalse(identifierString);

		identifierString = " variable_with_space";
		assertVarDeclSnakeCaseFalse(identifierString);
	}

	@Test
	public void doesNotChangeWithOtherNodes() throws Exception {
		Position position = new Position(0, 0, 0, 0);
		Identifier identifier = new Identifier("hi", position);
		Assignation assignation = new Assignation(identifier, identifier, position);
		Method method = new Method(identifier, List.of(identifier));
		TextLiteral lit = new TextLiteral("a", position);
		NumericLiteral num = new NumericLiteral(1.0, position);
		BinaryExpression bin = new BinaryExpression(identifier, "+", identifier);
		Parenthesis parenthesis = new Parenthesis(bin);
		UnaryExpression un = new UnaryExpression(bin, "+", position);

		Report report = new Report();
		IdentifierRules identifierRules = new IdentifierRules(Case.CAMEL_CASE, report);

		identifierRules.visit(identifier);
		identifierRules.visit(identifier);
		identifierRules.visit(assignation);
		identifierRules.visit(method);
		identifierRules.visit(lit);
		identifierRules.visit(num);
		identifierRules.visit(bin);
		identifierRules.visit(parenthesis);
		identifierRules.visit(un);

		assertTrue(report.getReportLines().isEmpty());
	}

		private static void assertVarDeclCamelCase(String identifierString)
				throws Exception {
			Report report = getReportFromVarDecl(identifierString, Case.CAMEL_CASE);

			assertTrue(report.getReportLines().isEmpty());

	}


	private static void assertVarDeclCamelCaseFalse(String identifierString) throws Exception {
		Report report = getReportFromVarDecl(identifierString, Case.CAMEL_CASE);
		assertFalse(report.getReportLines().isEmpty());
	}

	private static void assertVarDeclSnakeCase(String identifierString) throws Exception {
		Report report = getReportFromVarDecl(identifierString, Case.SNAKE_CASE);

		assertTrue(report.getReportLines().isEmpty());
	}


	private static void assertVarDeclSnakeCaseFalse(String identifierString) throws Exception {
		Report report = getReportFromVarDecl(identifierString, Case.SNAKE_CASE);
		assertFalse(report.getReportLines().isEmpty());
	}

	private static Report getReportFromVarDecl(String identifierString, Case identifierCase)
			throws Exception {
		int length = identifierString.length();
		VariableDeclaration variableDeclaration = new VariableDeclaration(
			new Identifier(identifierString, new Position(4, length, 1, 4)),
			new Type("string", new Position(5 + length, 6, 1, 5 + length)),
			Optional.of(new TextLiteral("america", new Position(14 + length, 7, 1, 14 + length))));

		Report report = new Report();
		IdentifierRules identifierRules = new IdentifierRules(identifierCase, report);

		identifierRules.visit(variableDeclaration);
		return report;
	}


}
