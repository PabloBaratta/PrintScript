package org.linter;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.OneArgFunctionConfiguration;
import org.linter.visitors.LinterVisitor;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LinterConfiguratorTest {

	public static Program getProgramIf(String identifierString, TextLiteral hi) {
		int length = identifierString.length();
		Position position = new Position(14 + length, 7, 1, 14 + length);
		Position position1 = new Position(8, length, 1, 8);
		Position position2 = new Position(9 + length, 6, 1, 9 + length);
		return new Program(
				List.of(new IfStatement(
						new BinaryExpression(
								new TextLiteral("a", new Position(4, 1, 1, 4)),
								"==",
								new TextLiteral("a", new Position(6, 1, 1, 6))),
						List.of(new VariableDeclaration(
								new Identifier(identifierString, position1),
								new Type("string", position2),
								Optional.of(hi))),
						new ArrayList<>(), new Position(0, 0, 0, 0))));

	}

	public static PrintScriptIterator<ASTNode> iteratorProgramIf(String identifierString, TextLiteral hi) {
		return new PrintScriptIteratorTest<>(getProgramIf(identifierString, hi).getChildren());
	}

	@Test
	public void camelCaseFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		String path = "src/test/resources/camel_case.json";
		Map<String, String> stringStringMap = jsonReader.readJsonToMap(path);

		successfulCase(stringStringMap, "amparoBreak", getDefaultExpressionForTests());
		unsuccessfulCase(stringStringMap, "hello_world", getDefaultExpressionForTests());
	}

	@Test
	public void snakeCaseFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		String path = "src/test/resources/snake_case.json";
		Map<String, String> stringStringMap = jsonReader.readJsonToMap(path);

		successfulCase(stringStringMap, "amparo_break",  getDefaultExpressionForTests());
		unsuccessfulCase(stringStringMap, "lionelMessi",  getDefaultExpressionForTests());
	}

	@Test
	public void printLnFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		String path = "src/test/resources/println.json";
		Map<String, String> stringStringMap = jsonReader.readJsonToMap(path);

		successfulCase(stringStringMap, "amparo_break",  getDefaultExpressionForTests());


		BinaryExpression binaryExpression = new BinaryExpression(
				new TextLiteral("a", new Position(8, 1, 1, 8)),
				"+",
				new TextLiteral("a", new Position(10, 1, 1, 10))
		);

		unsuccessfulCase(stringStringMap, "lionelMessi",  binaryExpression);
	}

	@Test
	public void turnedOffPrintLnFromJson() throws IOException {
		JsonReader jsonReader = new JsonReader();
		String path = "src/test/resources/println2.json";
		Map<String, String> stringStringMap = jsonReader.readJsonToMap(path);

		successfulCase(stringStringMap, "amparo_break",  getDefaultExpressionForTests());


		BinaryExpression binaryExpression = new BinaryExpression(
				new TextLiteral("a", new Position(8, 1, 1, 8)),
				"+",
				new TextLiteral("a", new Position(10, 1, 1, 10))
		);

		successfulCase(stringStringMap, "lionelMessi",  binaryExpression);
	}

	private static void successfulCase(Map<String, String> conf, String var, Expression exp) {
		String printLnOption = "printWithIdentifiers";
		String printName = "println";
		LinterConfigurator config = new LinterConfigurator(
				List.of(new IdentifierConfiguration(),
						new OneArgFunctionConfiguration(printLnOption, printName)));

		Report report = new Report();

		assertDoesNotThrow(() -> {
		config.getLinterFromConfig(conf, report);
		});

		try {
			LinterVisitor linterFromConfig = config.getLinterFromConfig(conf, report);
			Program program = getProgram(var, exp);

			linterFromConfig.visit(program);
			assertTrue(report.getReportLines().isEmpty());
		} catch (Exception e) {

		}
	}

	private static void unsuccessfulCase(Map<String, String> conf, String var, Expression exp) {
		String printLnOption = "printWithIdentifiers";
		String printName = "println";
		LinterConfigurator config = new LinterConfigurator(
				List.of(new IdentifierConfiguration(),
						new OneArgFunctionConfiguration(printLnOption, printName)));

		Report report = new Report();

		assertDoesNotThrow(() -> {
			config.getLinterFromConfig(conf, report);
		});

		try {
			LinterVisitor linterFromConfig = config.getLinterFromConfig(conf, report);
			Program program = getProgram(var, exp);

			linterFromConfig.visit(program);
			assertFalse(report.getReportLines().isEmpty());
		} catch (Exception e) {

		}
	}

	public static Program getProgram(String identifierString, Expression expression) {
		int length = identifierString.length();
		Position position = new Position(14 + length, 7, 1, 14 + length);
		Program program = new Program(
				List.of(new VariableDeclaration(
					new Identifier(identifierString, new Position(4, length, 1, 4)),
					new Type("string", new Position(5 + length, 6, 1, 5 + length)),
					Optional.of(new TextLiteral("america", position))),
					new Method(
						new Identifier("println", new Position(23,7,2, 23)),
						List.of(expression))));
		return program;
	}

	public static PrintScriptIterator<ASTNode> iteratorProgram(String identifierString, TextLiteral hi) {
		return new PrintScriptIteratorTest<>(getProgram(identifierString, hi).getChildren());
	}

	private static Expression getDefaultExpressionForTests() {
		return new Identifier("a", new Position(31, 1,2, 31));
	}


}
