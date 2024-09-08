package org.example;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.example.nodeconstructors.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.example.lexer.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;


public class ParserTest {

	@Test
	public void testSimpleAddExpression() throws Exception {
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
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("1.0 + 2.0", node.toString());
	}

	@Test
	public void testNumberStringOperatorCombination() throws Exception {
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
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("1.0 + 2.0 * hola", node.toString());
	}

	@Test
	public void testOnlyNumber() throws Exception {
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("1.0", node.toString());
	}

	@Test
	public void testOnlyString() throws Exception {
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
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
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
	public void testOnlyIdentifier() throws Exception {
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NUMBER.toTokenType());
		expressions.add(STRING.toTokenType());
		expressions.add(IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(IDENTIFIER.toTokenType(), "si", new Position(1, 2, 1, 1)));
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertEquals("si", node.toString());
	}

	@Test
	public void testVariableDeclarationWithParentheses() throws Exception {

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

		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
		ExpressionNodeConstructor e1 = getExpressionNodeConstructor(expressions);
		Parser parser = new Parser(new LinkedList<>(List.of(e1)), new ArrayList<>(), tokenBuffer);

		// Parsear la expresión
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();

		// Verifica que la expresión se haya parseado correctamente
		assertEquals("((1.0) + 2.0)", node.toString());

	}

	@Test
	public void method() throws Exception {
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(IDENTIFIER.toTokenType(), "readInput", new Position(1, 2, 1, 1)));
		tokens.add(new Token(LEFT_PARENTHESIS.toTokenType(), "(", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NUMBER.toTokenType(), "1", new Position(3, 1, 1, 3)));
		tokens.add(new Token(PLUS.toTokenType(), "+", new Position(4, 1, 1, 4)));
		tokens.add(new Token(NUMBER.toTokenType(), "2", new Position(5, 1, 1, 5)));
		tokens.add(new Token(RIGHT_PARENTHESES.toTokenType(), ")", new Position(6, 1, 1, 6)));
		tokens.add(new Token(SEMICOLON.toTokenType(), ";", new Position(6, 1, 1, 6)));

		Accumulator accumulator = new Accumulator(tokens);
		Parser parser = ParserProvider.provide10(accumulator);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		assertInstanceOf(Method.class, node);
		Method method = (Method) node;
		assertEquals(method.getVariable().toString(), "readInput");
		assertEquals(method.getArguments().get(0).toString(), "1.0 + 2.0");

	}

	@Test
	public void methodWithMethods() throws Exception {
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
		tokens.add(new Token(SEMICOLON.toTokenType(), ";", new Position(9, 1, 1, 9)));

		Accumulator accumulator = new Accumulator(tokens);
		Parser parser = ParserProvider.provide10(accumulator);

		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(Method.class, astNode);
		Method print = (Method) astNode;
		assertEquals(1, print.getArguments().size());

		Expression readinput = print.getArguments().get(0);
		assertInstanceOf(BinaryExpression.class, readinput);
		assertInstanceOf(Method.class, ((BinaryExpression) readinput).getRight());

	}


	@Test
	public void testEntireProgram11() throws Exception {
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
		Accumulator accumulator = new Accumulator(TokenTestUtil.getTokens(tokenTypes));
		Parser parser = ParserProvider.provide11(accumulator);

		assertTrue(parser.hasNext());
		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(VariableDeclaration.class, astNode);

		assertTrue(parser.hasNext());

		Try<ASTNode, Exception> methodOrTry = parser.parseExpression();
		assertTrue(methodOrTry.isSuccess());
		ASTNode method = methodOrTry.getSuccess().get();

		assertInstanceOf(Method.class, method);

		assertTrue(parser.hasNext());

		Try<ASTNode, Exception> ifOrTry = parser.parseExpression();
		assertTrue(ifOrTry.isSuccess());
		ASTNode ifStatement = ifOrTry.getSuccess().get();

		assertInstanceOf(IfStatement.class, ifStatement);

		List<ASTNode> thenBlock =((IfStatement) ifStatement).getThenBlock();

		assertEquals(3, thenBlock.size());
		assertInstanceOf(VariableDeclaration.class, thenBlock.get(0));
		assertInstanceOf(VariableDeclaration.class, thenBlock.get(1));
		assertInstanceOf(IfStatement.class, thenBlock.get(2));
	}

	@Test
	public void testIfElse11() throws Exception {
		NativeTokenTypes[] tokenTypes = new NativeTokenTypes[] {

				IF, LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES, LEFT_BRACE,
				LET, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				RIGHT_BRACE,
				ELSE, LEFT_BRACE,
				LET, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				RIGHT_BRACE
		};

		Accumulator accumulator = new Accumulator(TokenTestUtil.getTokens(tokenTypes));
		Parser parser = ParserProvider.provide11(accumulator);

		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(IfStatement.class, astNode);

		IfStatement ifStatement = (IfStatement) astNode;

		List<ASTNode> thenBlock = ifStatement.getThenBlock();

		assertEquals(1, thenBlock.size());
		assertInstanceOf(VariableDeclaration.class, thenBlock.getFirst());

		List<ASTNode> elseBlock = ifStatement.getElseBlock();

		assertEquals(1, elseBlock.size());
		assertInstanceOf(VariableDeclaration.class, elseBlock.getFirst());
	}

	@Test
	public void testIfConst11() throws Exception {
		NativeTokenTypes[] tokenTypes = new NativeTokenTypes[] {

				IF, LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES, LEFT_BRACE,
				CONST, IDENTIFIER, COLON, NUMBER_TYPE, EQUALS, NUMBER, PLUS, NUMBER, SEMICOLON,
				RIGHT_BRACE,

		};

		Accumulator accumulator = new Accumulator(TokenTestUtil.getTokens(tokenTypes));
		Parser parser = ParserProvider.provide11(accumulator);

		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(IfStatement.class, astNode);


		IfStatement ifStatement = (IfStatement) astNode;

		List<ASTNode> thenBlock = ifStatement.getThenBlock();

		assertEquals(1, thenBlock.size());
		assertInstanceOf(ConstDeclaration.class, thenBlock.getFirst());

		ConstDeclaration constDeclaration = (ConstDeclaration) thenBlock.getFirst();

		assertInstanceOf(BinaryExpression.class, constDeclaration.getExpression());

	}

	@Test
	public void test() throws Exception {
		NativeTokenTypes[] tokenTypes = new NativeTokenTypes[] {
				PRINTLN, LEFT_PARENTHESIS, READINPUT, LEFT_PARENTHESIS, STRING, RIGHT_PARENTHESES,
				RIGHT_PARENTHESES, SEMICOLON
		};

		Accumulator accumulator = new Accumulator(TokenTestUtil.getTokens(tokenTypes));
		Parser parser = ParserProvider.provide11(accumulator);

		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(Method.class, astNode);
		Method print = (Method) astNode;
		assertEquals(1, print.getArguments().size());

		Expression readinput = print.getArguments().get(0);
		assertInstanceOf(Method.class, readinput);
	}

	@Test
	public void readInputSentence() throws Exception {
		NativeTokenTypes[] tokenTypes = new NativeTokenTypes[] {
				LET, IDENTIFIER, COLON, STRING_TYPE, EQUALS,
				READINPUT, LEFT_PARENTHESIS, STRING, RIGHT_PARENTHESES, SEMICOLON
		};

		Accumulator accumulator = new Accumulator(TokenTestUtil.getTokens(tokenTypes));
		Parser parser = ParserProvider.provide11(accumulator);

		assertTrue(parser.hasNext());
		Try<ASTNode, Exception> astNodeExceptionTry = parser.parseExpression();
		assertTrue(astNodeExceptionTry.isSuccess());
		ASTNode astNode = astNodeExceptionTry.getSuccess().get();
		assertInstanceOf(VariableDeclaration.class, astNode);

		Optional<Expression> callExpression = ((VariableDeclaration) astNode).getExpression();
		assertTrue(callExpression.isPresent());
		Expression callNode = callExpression.get();
		assertInstanceOf(Method.class, callNode);

		assertFalse(parser.hasNext());
	}


	private static Map<TokenType, Integer> mapOperatorPrecedence() {
		return Map.of(PLUS.toTokenType(), 1,
				MINUS.toTokenType(), 1,
				ASTERISK.toTokenType(), 2,
				SLASH.toTokenType(), 2);
	}


}
