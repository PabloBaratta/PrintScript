package org.linter;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.PrintLineConfiguration;
import org.linter.visitors.LinterVisitor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LinterConfiguratorTest {

	@Test
	public void camelCaseFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		Map<String, String> stringStringMap = jsonReader.readJsonToMap("src/test/resources/camel_case.json");

		successfulCase(stringStringMap, "amparoBreak", getDefaultExpressionForTests());
		unsuccessfulCase(stringStringMap, "hello_world", getDefaultExpressionForTests());
	}

	@Test
	public void snakeCaseFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		Map<String, String> stringStringMap = jsonReader.readJsonToMap("src/test/resources/snake_case.json");

		successfulCase(stringStringMap, "amparo_break",  getDefaultExpressionForTests());
		unsuccessfulCase(stringStringMap, "lionelMessi",  getDefaultExpressionForTests());
	}

	@Test
	public void printLnFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		Map<String, String> stringStringMap = jsonReader.readJsonToMap("src/test/resources/println.json");

		successfulCase(stringStringMap, "amparo_break",  getDefaultExpressionForTests());


		BinaryExpression binaryExpression = new BinaryExpression(
				new TextLiteral("a", new Position(8, 1, 1)),
				"+",
				new TextLiteral("a", new Position(10, 1, 1))
		);

		unsuccessfulCase(stringStringMap, "lionelMessi",  binaryExpression);
	}

	@Test
	public void turnedOffPrintLnFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		Map<String, String> stringStringMap = jsonReader.readJsonToMap("src/test/resources/println2.json");

		successfulCase(stringStringMap, "amparo_break",  getDefaultExpressionForTests());


		BinaryExpression binaryExpression = new BinaryExpression(
				new TextLiteral("a", new Position(8, 1, 1)),
				"+",
				new TextLiteral("a", new Position(10, 1, 1))
		);

		successfulCase(stringStringMap, "lionelMessi",  binaryExpression);
	}

	private static void successfulCase(Map<String, String> stringStringMap, String identifierString, Expression expression) {
		LinterConfigurator linterConfigurator = new LinterConfigurator(List.of(new IdentifierConfiguration(),
				new PrintLineConfiguration()));

		Report report = new Report();

		assertDoesNotThrow(() -> {
		linterConfigurator.getLinterFromConfig(stringStringMap, report);
		});

		try {
			LinterVisitor linterFromConfig = linterConfigurator.getLinterFromConfig(stringStringMap, report);
			Program program = getProgram(identifierString, expression);

			linterFromConfig.visit(program);
			assertTrue(report.getReportLines().isEmpty());
		} catch (Exception e) {

		}
	}

	private static void unsuccessfulCase(Map<String, String> stringStringMap, String identifierString, Expression expression) {
		LinterConfigurator linterConfigurator = new LinterConfigurator(List.of(new IdentifierConfiguration(),
				new PrintLineConfiguration()));

		Report report = new Report();

		assertDoesNotThrow(() -> {
			linterConfigurator.getLinterFromConfig(stringStringMap, report);
		});

		try {
			LinterVisitor linterFromConfig = linterConfigurator.getLinterFromConfig(stringStringMap, report);
			Program program = getProgram(identifierString, expression);

			linterFromConfig.visit(program);
			assertFalse(report.getReportLines().isEmpty());
		} catch (Exception e) {

		}
	}

	private static Program getProgram(String identifierString, Expression expression) {
		int length = identifierString.length();
		Program program = new Program(
				List.of(new VariableDeclaration(
						new Identifier(identifierString, new Position(4, length, 1)),
						new Type("string", new Position(5 + length, 6, 1)),
						Optional.of(new TextLiteral("america", new Position(14 + length, 7, 1)))),
						new Method(
								new Identifier("println", new Position(23,7,2)),
								List.of(expression))));
		return program;
	}

	private static Expression getDefaultExpressionForTests() {
		return new Identifier("a", new Position(31, 1,2));
	}


}
