package org.example;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.nodeconstructors.CallExpressionNodeConstructor;
import org.example.nodeconstructors.ExpressionCollectorNodeConstructor;
import org.example.nodeconstructors.NodeResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.TokenTestUtil.getaTokenFromTokenType;
import static org.example.lexer.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;

public class CallExpressionTest {

	@Test
	public void onlyAcceptsItsTokens() {

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(true, collector);

		// does not start with an identifier

		Arrays.stream(NativeTokenTypes.values()).filter(
				type -> !type.equals(IDENTIFIER)
		).forEach(type ->
				{TokenBuffer tokenBuffer = new TokenBuffer(List.of(getaTokenFromTokenType(type)));
					NodeResponse build = builder.build(tokenBuffer);
					assertTrue(build.possibleNode().isSuccess());
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());}
		);

		Arrays.stream(NativeTokenTypes.values()).filter(
				type -> !type.equals(LEFT_PARENTHESIS)
		).forEach(type ->
				{TokenBuffer tokenBuffer = new TokenBuffer(List.of( getaTokenFromTokenType(IDENTIFIER),
						getaTokenFromTokenType(type)
						));
					NodeResponse build = builder.build(tokenBuffer);
					assertTrue(build.possibleNode().isSuccess());
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());}
		);
	}

	@Test
	public void simplePrintLnTest() {

		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
			LEFT_PARENTHESIS, STRING, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		successfulAssertions(terminal, tokens, 1, 1);
	}

	@Test
	public void insideExpressionPrintLnTest() {
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, PLUS, STRING, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		successfulAssertions(terminal, tokens, 1, 3);
	}

	@Test
	public void twoArgumentsExpressionPrintLnTest() {
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, COMMA, STRING, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		successfulAssertions(terminal, tokens, 2, 2);
	}

	@Test
	public void multipleParenthesisExpressionsPrintLnTest() {
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				LEFT_PARENTHESIS,
				NUMBER,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		successfulAssertions(terminal, tokens, 1, 7);
	}

	@Test
	public void nonTerminalMultipleParenthesisExpressionsPrintLnTest() {
		boolean terminal = false;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				LEFT_PARENTHESIS,
				NUMBER,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		successfulAssertions(terminal, tokens, 1, 7);
	}

	@Test
	public void notSuccessfulSimplePrintLnTest() {
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		assertMissingLastArguments(terminal, tokens);

		inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				LEFT_PARENTHESIS,
				NUMBER,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES
		};
		tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		assertMissingLastArguments(terminal, tokens);
	}

	@Test
	public void multipleConsecutiveCommas() {

		// println("hi",,)
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, COMMA, COMMA, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void missingArgumentAfterCommas() {

		// println("hi",)
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, COMMA, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void argumentsAfterFunctionInTerminal() {
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				LEFT_PARENTHESIS,
				NUMBER,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				IDENTIFIER,
				SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));


		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void methodWithIdentifier() {
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(IDENTIFIER, "readEnv");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				LEFT_PARENTHESIS,
				NUMBER,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				PLUS,
				NUMBER,
				RIGHT_PARENTHESES,
				IDENTIFIER,
				SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void methodWithMethods(){
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(IDENTIFIER, "readInput");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				IDENTIFIER,
				LEFT_PARENTHESIS,
				NUMBER,
				RIGHT_PARENTHESES,
				RIGHT_PARENTHESES,
				SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isSuccess());
	}

	@Test
	public void methodWithOperations(){
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(IDENTIFIER, "readInput");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				NUMBER,
				PLUS,
				IDENTIFIER,
				LEFT_PARENTHESIS,
				NUMBER,
				RIGHT_PARENTHESES,
				RIGHT_PARENTHESES,
				SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isSuccess());
	}

	@Test
	public void methodWithOperations2(){
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(IDENTIFIER, "readInput");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS,
				STRING,
				PLUS,
				IDENTIFIER,
				LEFT_PARENTHESIS,
				STRING,
				RIGHT_PARENTHESES,
				RIGHT_PARENTHESES,
				SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = builder.build(new TokenBuffer(tokens));
		assertTrue(build.possibleNode().isSuccess());
	}

		private static void assertMissingLastArguments(boolean terminal, LinkedList<Token> tokens) {
		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor builder = new CallExpressionNodeConstructor(terminal, collector);

		int originalTokenListSize = tokens.size();
		for (int i = 2; i < originalTokenListSize; i++) {
			tokens.removeLast();
			NodeResponse build = builder.build(new TokenBuffer(tokens));
			assertTrue(build.possibleNode().isFail());
		}
	}


	private static void successfulAssertions(boolean terminal, LinkedList<Token> tokens,
											int n, int withoutComma) {
		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		CallExpressionNodeConstructor constructor = new CallExpressionNodeConstructor(terminal, collector);

		NodeResponse build = constructor.build(new TokenBuffer(tokens));

		assertFalse(build.possibleBuffer().hasAnyTokensLeft());
		assertTrue(build.possibleNode().isSuccess());

		Optional<ASTNode> possibleNode = build.possibleNode().getSuccess().get();

		assertTrue(possibleNode.isPresent());

		ASTNode astNode = possibleNode.get();

		assertInstanceOf(Method.class, astNode);

		Method method = (Method) astNode;

		List<Expression> arguments = method.getArguments();

		assertEquals(n, arguments.size());
		assertEquals("println", method.getVariable().getName());
		assertEquals(withoutComma, collector.getCollectedTokens().size());
	}
}
