import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FormatterTest {

	String jsonRulesV10 = """
				{
				"spaceBeforeColon": { "rule": true },
				"spaceAfterColon": { "rule": true },
				"spaceBeforeAssignation": { "rule": true },
				"spaceAfterAssignation": { "rule": true },
				"newLineBeforePrintln": { "rule": true, "qty": 2 }
				}
		""";

	String jsonRulesV11 = """
				{
				"spaceBeforeColon": { "rule": true },
				"spaceAfterColon": { "rule": true },
				"spaceBeforeAssignation": { "rule": true },
				"spaceAfterAssignation": { "rule": true },
				"newLineBeforePrintln": { "rule": true, "qty": 2 },
				"indentation": { "rule": true, "qty": 2 }
				}
		""";

	Position position = new Position(0, 0, 0, 0);

	@Test
	public void testVariableDeclaration() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV10);
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

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV10);
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

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV10);
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

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV10);
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

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV10);
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

	@Test
	public void testIfStatement() throws Exception {

		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV11);
		Formatter formatter = new Formatter(rules);

		Expression condition = new BinaryExpression(
				new Identifier("x", position),
				">",
				new NumericLiteral(10.0, position)
		);
		Identifier hola = new Identifier("hola", position);
		Type string = new Type("string", position);
		VariableDeclaration var1 = new VariableDeclaration(hola, string, Optional.empty());
		List<ASTNode> thenBlock = List.of(var1);
		List<ASTNode> elseBlock = new ArrayList<>();
		IfStatement ifStatement = new IfStatement(condition, thenBlock, elseBlock, position);

		Program program = new Program(List.of(ifStatement));
		String result = formatter.format(program);

		String expected =
				"if (x > 10.0) { \n" +
				"  let hola : string;\n" +
				"}\n";
		assertEquals(expected, result);
	}

	@Test
	public void testElseStatement() throws Exception {
		Map<String, Rule> rules = JsonReader.readRulesFromJson(jsonRulesV11);
		Formatter formatter = new Formatter(rules);

		Expression condition = new BinaryExpression(
				new Identifier("x", position),
				">",
				new NumericLiteral(10.0, position)
		);
		Identifier hola = new Identifier("hola", position);
		Type string = new Type("string", position);
		List<ASTNode> thenBlock = List.of(new VariableDeclaration(hola, string, Optional.empty()));
		Identifier adios = new Identifier("adios", position);
		List<ASTNode> elseBlock = List.of(new VariableDeclaration(adios, string, Optional.empty()));
		IfStatement ifStatement = new IfStatement(condition, thenBlock, elseBlock, position);

		Program program = new Program(List.of(ifStatement));
		String result = formatter.format(program);

		String expected =
				"if (x > 10.0) { \n" +
						"  let hola : string;\n" +
						"} else { \n" +
						"  let adios : string;\n" +
						"}\n";

		assertEquals(expected, result);
	}
}
