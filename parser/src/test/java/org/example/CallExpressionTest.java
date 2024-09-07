package org.example;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.nodeconstructors.Accumulator;
import org.example.nodeconstructors.CallExpressionNodeConstructor;
import org.example.nodeconstructors.CollectorNodeConstructor;
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

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(true, collector);

		// does not start with an identifier

		Arrays.stream(NativeTokenTypes.values()).filter(
				type -> !type.equals(IDENTIFIER)
		).forEach(type ->
				{
					List<Token> tokens = List.of(getaTokenFromTokenType(type));
					Accumulator accumulator = new Accumulator(tokens);
					assertDoesNotThrow(() -> new TokenBuffer(accumulator));
					TokenBuffer tokenBuffer = null;
					try {
						tokenBuffer = new TokenBuffer(accumulator);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					TokenBuffer finalTokenBuffer = tokenBuffer;
					assertDoesNotThrow(() -> builder.build(finalTokenBuffer));
					NodeResponse build = null;
					try {
						build = builder.build(tokenBuffer);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					assertTrue(build.possibleNode().isSuccess());
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());}
		);

		Arrays.stream(NativeTokenTypes.values()).filter(
				type -> !type.equals(LEFT_PARENTHESIS)
		).forEach(type ->
				{
					List<Token> tokenList = List.of(getaTokenFromTokenType(IDENTIFIER),
							getaTokenFromTokenType(type));
					Accumulator accumulator = new Accumulator(tokenList);
					TokenBuffer tokenBuffer = null;
					try {
						tokenBuffer = new TokenBuffer(accumulator);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					NodeResponse build;
					try {
						build = builder.build(tokenBuffer);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					assertTrue(build.possibleNode().isSuccess());
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());}
		);
	}

	@Test
	public void simplePrintLnTest() throws Exception {

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
	public void insideExpressionPrintLnTest() throws Exception {
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
	public void twoArgumentsExpressionPrintLnTest() throws Exception {
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
	public void multipleParenthesisExpressionsPrintLnTest() throws Exception {
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
	public void nonTerminalMultipleParenthesisExpressionsPrintLnTest() throws Exception {
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
	public void notSuccessfulSimplePrintLnTest() throws Exception {
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
	public void multipleConsecutiveCommas() throws Exception {

		// println("hi",,)
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, COMMA, COMMA, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);


		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void missingArgumentAfterCommas() throws Exception {

		// println("hi",)
		boolean terminal = true;
		Token functionCall = getaTokenFromTokenType(PRINTLN, "println");
		NativeTokenTypes[] inParenthesisInput = new NativeTokenTypes[]{
				LEFT_PARENTHESIS, STRING, COMMA, RIGHT_PARENTHESES, SEMICOLON
		};
		LinkedList<Token> tokens = new LinkedList<>(List.of(functionCall));
		tokens.addAll(TokenTestUtil.getTokens(inParenthesisInput));

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);


		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void argumentsAfterFunctionInTerminal() throws Exception {
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


		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);


		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void methodWithIdentifier() throws Exception {
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

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void methodWithMethods() throws Exception {
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

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isSuccess());
	}

	@Test
	public void methodWithOperations() throws Exception {
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

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);


		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isSuccess());
	}

	@Test
	public void methodWithOperations2() throws Exception {
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

		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));
		assertTrue(build.possibleNode().isSuccess());
	}

		private static void assertMissingLastArguments(boolean terminal, LinkedList<Token> tokens)
                throws Exception {
		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor builder = getCallConstructor(terminal, collector);

		int originalTokenListSize = tokens.size();
		for (int i = 2; i < originalTokenListSize; i++) {
			tokens.removeLast();
			Accumulator accumulator = new Accumulator(tokens);
			NodeResponse build = builder.build(new TokenBuffer(accumulator));
			assertTrue(build.possibleNode().isFail());
		}
	}


	private static void successfulAssertions(boolean terminal, LinkedList<Token> tokens,
											int n, int withoutComma)
                                            throws Exception {
		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		CallExpressionNodeConstructor constructor = getCallConstructor(terminal, collector);


		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = constructor.build(new TokenBuffer(accumulator));

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

	private static CallExpressionNodeConstructor
				getCallConstructor(boolean terminal, CollectorNodeConstructor collector) {
		List<TokenType> nativeFunctions = List.of(PRINTLN.toTokenType(),
				READENV.toTokenType(),
				READINPUT.toTokenType());
		return new CallExpressionNodeConstructor(terminal, collector, nativeFunctions);
	}
}
