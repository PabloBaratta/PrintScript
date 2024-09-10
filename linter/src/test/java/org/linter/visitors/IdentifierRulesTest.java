package org.linter.visitors;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.util.Arrays;
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
        List<String> identifierStrings = Arrays.asList(
                "variable_name",
                "variable_name_with_numbers_123",
                "a",
                "snake_case",
                "another_test_case"
        );

        for (String identifierString : identifierStrings) {
            assertConstSnakeCase(identifierString);
            assertVarDeclSnakeCase(identifierString);
        }

	}

	@Test
	public void unsuccessfulSnakeCaseScenarios() throws Exception {

        List<String> identifierStrings = Arrays.asList(
                "_leading_underscore",
                "trailing_underscore_",
                "multiple__underscores",
                "123_variable",
                "mixedCASE",
                "variable-name",
                " variable_with_space"
        );
        for (String identifierString : identifierStrings) {
          assertConstSnakeCaseFalse(identifierString);
          assertVarDeclSnakeCaseFalse(identifierString);
        }
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

	@Test
	public void testIfStatement() throws Exception {
		Report report = new Report();
		IdentifierRules identifierRules = new IdentifierRules(Case.CAMEL_CASE, report);

		Identifier condition = new Identifier("condition", new Position(0, 0, 0, 0));
		Identifier thenIdentifier = new Identifier("thenVar", new Position(1, 0, 0, 1));
		Identifier elseIdentifier = new Identifier("elseVar", new Position(2, 0, 0, 2));

		Type string1 = new Type("string", new Position(1, 6, 1, 1));
		VariableDeclaration thenVarDecl = new VariableDeclaration(thenIdentifier, string1, Optional.empty());
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

    private static Report getReportFromConstDecl(String identifierString, Case identifierCase) throws Exception {
        int length = identifierString.length();

        // Crear una instancia de ConstDeclaration
        ConstDeclaration constDeclaration = new ConstDeclaration(
                new Identifier(identifierString, new Position(4, length, 1, 4)),
                new Type("string", new Position(5 + length, 6, 1, 5 + length)),
                new TextLiteral("america", new Position(14 + length, 7, 1, 14 + length))
        );

        Report report = new Report();
        IdentifierRules identifierRules = new IdentifierRules(identifierCase, report);

        // Pasar la ConstDeclaration al visitor
        identifierRules.visit(constDeclaration);
        return report;
    }


    private static void assertConstSnakeCaseFalse(String identifierString) throws Exception {
        Report report = getReportFromConstDecl(identifierString, Case.SNAKE_CASE);
        assertFalse(report.getReportLines().isEmpty());
    }

    private static void assertConstSnakeCase(String identifierString) throws Exception {
        Report report = getReportFromConstDecl(identifierString, Case.SNAKE_CASE);
        assertTrue(report.getReportLines().isEmpty());
    }


}
