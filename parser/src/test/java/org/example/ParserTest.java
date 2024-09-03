package org.example;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.example.nodeconstructors.CallExpressionNodeConstructor;
import org.example.nodeconstructors.ExpressionNodeConstructor;
import org.example.nodeconstructors.NodeConstructor;
import org.example.nodeconstructors.VariableDeclarationNodeConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.example.lexer.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;


public class ParserTest {

	@Test
	public void testSimpleAddExpression(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		tokens.add(new Token(PLUS.toTokenType(), "+", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NUMBER.toTokenType(), "2", new Position(3, 1, 1, 3)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("1.0 + 2.0", node.toString());
	}

	@Test
	public void testNumberStringOperatorCombination(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		tokens.add(new Token(PLUS.toTokenType(), "+", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NUMBER.toTokenType(), "2", new Position(3, 1, 1, 3)));
		tokens.add(new Token(ASTERISK.toTokenType(), "*", new Position(4, 1, 1, 4)));
		tokens.add(new Token(STRING.toTokenType(), "\"hola\"", new Position(5, 6, 1, 5)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("1.0 + 2.0 * hola", node.toString());
	}

	@Test
	public void testOnlyNumber(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("1.0", node.toString());
	}

	@Test
	public void testOnlyString(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		TokenType str = STRING.toTokenType();
		expressions.add(str);
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		Position pos = new Position(1, 20, 1, 1);
		tokens.add(new Token(str, "\"hola buenas tardes\"", pos));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("hola buenas tardes", node.toString());
	}

	private static ExpressionNodeConstructor getExpressionNodeConstructor(List<TokenType> expressions) {

		CallExpressionNodeConstructor callExpression = new CallExpressionNodeConstructor(false, null,
				List.of(PRINTLN.toTokenType(),
				READENV.toTokenType(),
				READINPUT.toTokenType()));
		return new ExpressionNodeConstructor(mapOperatorPrecedence(), expressions, callExpression);
	}

	@Test
	public void testOnlyIdentifier(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(IDENTIFIER.toTokenType(), "si", new Position(1, 2, 1, 1)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("si", node.toString());
	}

	@Test
	public void testVariableDeclarationWithParentheses() {

		List<TokenType> expressions = new ArrayList<>();

		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());

		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 17, 6)));
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 17, 6)));
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(7, 1, 17, 7)));
		TokenType rightPar = RIGHT_PARENTHESES.toTokenType();
		tokens.add(new Token(rightPar, ")", new Position(10, 1, 17, 10)));

		tokens.add(new Token(PLUS.toTokenType(), "+", new Position(8, 1, 17, 8)));
		tokens.add(new Token(NUMBER.toTokenType(), "2", new Position(9, 1, 17, 9)));
		tokens.add(new Token(rightPar, ")", new Position(10, 1, 17, 10)));

		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		ExpressionNodeConstructor e1 = getExpressionNodeConstructor(expressions);
		Parser parser = new Parser(new LinkedList<>(List.of(e1)), new ArrayList<>(), tokenBuffer);

		// Parsear la expresión
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();

		// Verifica que la expresión se haya parseado correctamente
		assertEquals("((1.0) + 2.0)", node.toString());

	}

	@Test
	public void method(){

		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());

		List<NodeConstructor> list = new ArrayList<>();
		list.add(getExpressionNodeConstructor(expressions));

		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(IDENTIFIER.toTokenType(), "readInput", new Position(1, 2, 1, 1)));
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(3, 1, 1, 3)));
		tokens.add(new Token(PLUS.toTokenType(), "+", new Position(4, 1, 1, 4)));
		tokens.add(new Token(NUMBER.toTokenType(), "2", new Position(5, 1, 1, 5)));
		tokens.add(new Token(RIGHT_PARENTHESES.toTokenType(), ")", new Position(6, 1, 1, 6)));

		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertTrue(node instanceof Program);
		Method method = (Method) ((Program) node).getChildren().get(0);
		assertEquals(method.getVariable().toString(), "readInput");
		assertEquals(method.getArguments().get(0).toString(), "1.0 + 2.0");

	}

	@Test
	public void methodWithMethods(){

		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());

		List<NodeConstructor> list = new ArrayList<>();
		list.add(getExpressionNodeConstructor(expressions));

		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(IDENTIFIER.toTokenType(), "readInput", new Position(1, 2, 1, 1)));
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(3, 1, 1, 3)));
		tokens.add(new Token(PLUS.toTokenType(), "+", new Position(4, 1, 1, 4)));
		tokens.add(new Token(IDENTIFIER.toTokenType(), "readInput", new Position(5, 2, 1, 5)));
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 1, 6)));
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(7, 1, 1, 7)));
		tokens.add(new Token(RIGHT_PARENTHESES.toTokenType(), ")", new Position(8, 1, 1, 8)));
		tokens.add(new Token(RIGHT_PARENTHESES.toTokenType(), ")", new Position(9, 1, 1, 9)));

		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertTrue(node instanceof Program);
		Method method = (Method) ((Program) node).getChildren().get(0);
		assertEquals(method.getVariable().toString(), "readInput");
		ASTNode optionalBinary = method.getArguments().get(0);
		assertTrue(optionalBinary instanceof BinaryExpression);
		BinaryExpression binary = (BinaryExpression) optionalBinary;
		assertEquals(binary.getLeft().toString(), "1.0");
		assertTrue(binary.getRight() instanceof Method);
		Method method2 = (Method) binary.getRight();
		assertEquals(method2.getVariable().toString(), "readInput");
		assertEquals(method2.getArguments().get(0).toString(), "1.0");
	}

	@Test
	public void varWithMethods(){


		TokenType let = LET.toTokenType();

		TokenType num = NUMBER_TYPE.toTokenType();

		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(let, "let", new Position(1, 2, 1, 1)));
		tokens.add(new Token(IDENTIFIER.toTokenType(), "a", new Position(2, 1, 1, 2)));
		tokens.add(new Token(COLON.toTokenType(), ":", new Position(3, 1, 1, 3)));
		tokens.add(new Token(num, "number", new Position(4, 6, 1, 4)));
		tokens.add(new Token(EQUALS.toTokenType(), "=", new Position(5, 1, 1, 5)));
		tokens.add(new Token(IDENTIFIER.toTokenType(), "readInput", new Position(6, 2, 1, 6)));
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(7, 1, 1, 7)));
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(8, 1, 1, 8)));
		tokens.add(new Token(RIGHT_PARENTHESES.toTokenType(), ")", new Position(9, 1, 1, 9)));
		tokens.add(new Token(SEMICOLON.toTokenType(), ";", new Position(10, 1, 1, 10)));


		Parser parser = ParserProvider.provide10(tokens);

		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertInstanceOf(Program.class, node);
		VariableDeclaration var = (VariableDeclaration) ((Program) node).getChildren().get(0);
		assertEquals(var.getIdentifier().toString(), "a");
		assertEquals(var.getType().getTypeName(), "number");
		assertTrue(var.getExpression().isPresent());
		ASTNode optionalMethod = var.getExpression().get();
		assertInstanceOf(Method.class, optionalMethod);
		Method method = (Method) optionalMethod;
		assertEquals(method.getVariable().toString(), "readInput");
		assertEquals(method.getArguments().get(0).toString(), "1.0");
	}

	@Test
	public void testEntireProgram11() {
		NativeTokenTypes[] tokenTypes = new NativeTokenTypes[] {
			LET, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, SEMICOLON,
			PRINTLN, LEFT_PARENTHESIS, IDENTIFIER, RIGHT_PARENTHESES, SEMICOLON,
			IF, LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES, LEFT_BRACE,
				LET, IDENTIFIER, COLON, BOOLEAN_TYPE, EQUALS, BOOLEAN, SEMICOLON,
				LET, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				IF, LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES, LEFT_BRACE,
					LET, IDENTIFIER, COLON, BOOLEAN_TYPE, EQUALS, BOOLEAN, SEMICOLON,
					LET, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				RIGHT_BRACE,
			RIGHT_BRACE
		};

		Parser parser = ParserProvider.provide11(TokenTestUtil.getTokens(tokenTypes));

		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(Program.class, astNode);
		Program program = (Program) astNode;
		List<ASTNode> children = program.getChildren();
		assertEquals(3, children.size());
		assertInstanceOf(VariableDeclaration.class, children.get(0));
		assertInstanceOf(Method.class, children.get(1));
		assertInstanceOf(IfStatement.class, children.get(2));

		IfStatement ifStatement = (IfStatement) children.get(2);

		List<ASTNode> thenBlock = ifStatement.getThenBlock();

		assertEquals(3, thenBlock.size());
		assertInstanceOf(VariableDeclaration.class, thenBlock.get(0));
		assertInstanceOf(VariableDeclaration.class, thenBlock.get(1));
		assertInstanceOf(IfStatement.class, thenBlock.get(2));
	}

	@Test
	public void testIfElse11() {
		NativeTokenTypes[] tokenTypes = new NativeTokenTypes[] {

				IF, LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES, LEFT_BRACE,
				LET, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				RIGHT_BRACE,
				ELSE, LEFT_BRACE,
				LET, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				RIGHT_BRACE
		};

		Parser parser = ParserProvider.provide11(TokenTestUtil.getTokens(tokenTypes));

		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(Program.class, astNode);
		Program program = (Program) astNode;
		List<ASTNode> children = program.getChildren();
		assertEquals(1, children.size());
		assertInstanceOf(IfStatement.class, children.getFirst());

		IfStatement ifStatement = (IfStatement) children.getFirst();

		List<ASTNode> thenBlock = ifStatement.getThenBlock();

		assertEquals(1, thenBlock.size());
		assertInstanceOf(VariableDeclaration.class, thenBlock.getFirst());

		List<ASTNode> elseBlock = ifStatement.getElseBlock();

		assertEquals(1, elseBlock.size());
		assertInstanceOf(VariableDeclaration.class, elseBlock.getFirst());
	}


	private static Map<TokenType, Integer> mapOperatorPrecedence() {
		return Map.of(PLUS.toTokenType(), 1,
				MINUS.toTokenType(), 1,
				ASTERISK.toTokenType(), 2,
				SLASH.toTokenType(), 2);
	}


}
