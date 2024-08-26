import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FormatterTest {

	String jsonRules = """
				{
				"spaceBeforeColon": { "rule": true },
				"spaceAfterColon": { "rule": true },
				"spaceBeforeAssignation": { "rule": true },
				"spaceAfterAssignation": { "rule": true },
				"newLineBeforePrintln": { "rule": true, "qty": 2 }
				}
		""";

	Position position = new Position(0, 0, 0, 0);

	@Test
	public void testVariableDeclaration() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRules);
		Formatter formatter = new Formatter(rules);

		VariableDeclaration variableDeclaration = new VariableDeclaration(
				new Identifier("x", position),
				new Type("number", position),
				Optional.of(new NumericLiteral(10.0, position))
		);

		Program program = new Program(List.of(variableDeclaration));
		String result = formatter.format(program);

		String expected = "let x : number = 10.0;\n";
		assertEquals(expected, result);
	}

	@Test
	public void testAssignation() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRules);
		Formatter formatter = new Formatter(rules);

		Assignation assignation = new Assignation(
				new Identifier("x", position),
				(new NumericLiteral(20.0, position)), position
		);

		Program program = new Program(List.of(assignation));
		String result = formatter.format(program);

		String expected = "x = 20.0;\n";
		assertEquals(expected, result);
	}

	@Test
	public void testMethodPrintln() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRules);
		Formatter formatter = new Formatter(rules);

		Method method = new Method(
				new Identifier("println", position),
				List.of(new TextLiteral("\"Hello, World!\"", position))
		);

		Program program = new Program(List.of(method));
		String result = formatter.format(program);

		String expected = "\n\nprintln(\"Hello, World!\");\n";
		assertEquals(expected, result);
	}

	@Test
	public void testMultipleRules() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRules);
		Formatter formatter = new Formatter(rules);

		VariableDeclaration variableDeclaration = new VariableDeclaration(
				new Identifier("x", position),
				new Type("number", position),
				Optional.of(new NumericLiteral(10.0, position))
		);

		Assignation assignation = new Assignation(
				new Identifier("x", position),
				new NumericLiteral(20.0, position), position
		);

		Method method = new Method(
				new Identifier("println", position),
				List.of(new TextLiteral("\"Test Complete\"", position))
		);

		Program program = new Program(List.of(variableDeclaration, assignation, method));
		String result = formatter.format(program);

		String expected = """
		let x : number = 10.0;
		x = 20.0;


		println("Test Complete");
		""";

		assertEquals(expected, result);
	}

	@Test
	public void testAssignationWithBinaryExpression() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRules);
		Formatter formatter = new Formatter(rules);

		BinaryExpression binaryExpression = new BinaryExpression(
				new NumericLiteral(10.0, position),
				"+",
				new NumericLiteral(20.0, position)
		);

		Assignation assignation = new Assignation(
				new Identifier("result", position),
				binaryExpression, position
		);

		Program program = new Program(List.of(assignation));
		String result = formatter.format(program);

		String expected = "result = 10.0 + 20.0;\n";

		assertEquals(expected, result);
	}
}
