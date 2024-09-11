import org.example.*;
import org.token.Position;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewFormatterTest {

	Position position = new Position(0, 0, 0, 0);

	@Test
	public void testVariableDeclaration() throws Exception {
		Map<String, Rule> rules = Ruler.rulesV10();
		VariableDeclaration variableDeclaration = new VariableDeclaration(
				new Identifier("x", position),
				new Type("number", position),
				Optional.of(new NumericLiteral(BigDecimal.valueOf(10.0), position))
		);
		Program program = new Program(List.of(variableDeclaration));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();
		String expected = "let x : number = 10.0;\n";
		assertEquals(expected, result);
	}

	@Test
	public void testAssignation() throws Exception {
		Map<String, Rule> rules = Ruler.rulesV10();
		Assignation assignation = new Assignation(
				new Identifier("x", position),
				new NumericLiteral(BigDecimal.valueOf(20.0), position), position
		);
		Program program = new Program(List.of(assignation));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();
		String expected = "x = 20.0;\n";
		assertEquals(expected, result);
	}

	@Test
	public void testMethodPrintln() throws Exception {
		Map<String, Rule> rules = Ruler.rulesV10();
		Method method = new Method(
				new Identifier("println", position),
				List.of(new TextLiteral("\"Hello, World!\"", position))
		);
		Program program = new Program(List.of(method));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();
		String expected = "\n\nprintln(\"Hello, World!\");\n";
		assertEquals(expected, result);
	}

	@Test
	public void testElseStatement() throws Exception {
		Map<String, Rule> rules = Ruler.rulesV11();
		Expression condition = new BinaryExpression(
				new Identifier("x", position),
				">",
				new NumericLiteral(BigDecimal.valueOf(10.0), position)
		);
		Identifier hola = new Identifier("hola", position);
		Type string = new Type("string", position);
		List<ASTNode> thenBlock = List.of(new VariableDeclaration(hola, string, Optional.empty()));
		Identifier adios = new Identifier("adios", position);
		List<ASTNode> elseBlock = List.of(new VariableDeclaration(adios, string, Optional.empty()));
		IfStatement ifStatement = new IfStatement(condition, thenBlock, elseBlock, position);
		Program program = new Program(List.of(ifStatement));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();
		String expected =
				"if (x > 10.0) {\n" +
						"  let hola : string;\n" +
						"} else {\n" +
						"  let adios : string;\n" +
						"}\n";
		assertEquals(expected, result);
	}

	@Test
	public void testNestedIfStatement() throws Exception {
		Map<String, Rule> rules = Ruler.rulesV11();
		Expression condition1 = new BinaryExpression(
				new Identifier("x", position),
				">",
				new NumericLiteral(BigDecimal.valueOf(10.0), position)
		);
		Expression condition2 = new BinaryExpression(
				new Identifier("y", position),
				"<",
				new NumericLiteral(BigDecimal.valueOf(5.0), position)
		);
		Identifier hola = new Identifier("hola", position);
		Type string = new Type("string", position);
		VariableDeclaration var1 = new VariableDeclaration(hola, string, Optional.empty());
		IfStatement nestedIf = new IfStatement(condition2, List.of(var1), List.of(), position);
		IfStatement ifStatement = new IfStatement(condition1, List.of(nestedIf), List.of(), position);
		Program program = new Program(List.of(ifStatement));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();
		String expected =
				"if (x > 10.0) {\n" +
						"  if (y < 5.0) {\n" +
						"    let hola : string;\n" +
						"  }\n" +
						"}\n";
		assertEquals(expected, result);
	}

	@Test
	public void testConstDeclarationWithoutValue() throws Exception {
		Map<String, Rule> rules = Ruler.rulesV11();
		ConstDeclaration constDeclaration = new ConstDeclaration(
				new Identifier("MAX_SIZE", position),
				new Type("number", position),
				new NumericLiteral(BigDecimal.valueOf(10.0), position)
		);
		Program program = new Program(List.of(constDeclaration));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();
		String expected = "const MAX_SIZE : number = 10.0;\n";
		assertEquals(expected, result);
	}


}
