/*
package org.example;

import org.example.lexer.token.Position;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
	public static void main(String[] args) throws Exception {

		Position position = new Position(0, 0, 0, 0);
		Map<String, Rule> rules = Ruler.rulesV11();

		Identifier readEnv1 = new Identifier("readEnv", position);
		TextLiteral text = new TextLiteral("\"envVar\"", position);
		Method readEnv = new Method(readEnv1, List.of(text));

		VariableDeclaration varDec = new VariableDeclaration(
				new Identifier("envValue", position),
				new Type("string", position),
				Optional.of(readEnv)
		);

		Method printlnMethod = new Method(
				new Identifier("println", position),
				List.of(new TextLiteral("\"Hello, World!\"", position))
		);

		Expression condition = new BinaryExpression(
				new Identifier("x", position),
				">",
				new NumericLiteral(10.0, position)
		);

		Method println = new Method(
				new Identifier("println", position),
				List.of(new TextLiteral("\"Hello, World!\"", position))
		);
		List<ASTNode> thenBlock = List.of(varDec, printlnMethod);
		IfStatement ifStatement = new IfStatement(condition, thenBlock, List.of(), position);

		Program program = new Program(List.of(ifStatement, println));
		PrintScriptIterator<ASTNode> nodes = new PrintScriptIteratorTest<>(program.getChildren());
		Formatter formatter = new Formatter(rules, nodes);
		String result = formatter.format();

		System.out.println(result);
	}
}
*/
