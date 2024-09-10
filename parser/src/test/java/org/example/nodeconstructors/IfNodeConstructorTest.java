package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.IfStatement;
import org.example.TokenBuffer;
import org.example.TokenTestUtil;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.TokenTestUtil.getaTokenFromTokenType;
import static org.example.lexer.token.NativeTokenTypes.*;
import static org.example.lexer.token.NativeTokenTypes.SEMICOLON;
import static org.junit.jupiter.api.Assertions.*;

public class IfNodeConstructorTest {

	@Test
	public void successfulIfNodeConstruction() throws Exception {
		Token ifToken = getaTokenFromTokenType(IF, "if");

		NativeTokenTypes[][] successfulTestCases = new NativeTokenTypes[][]{
				{
						LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES,
						LEFT_BRACE, LET, IDENTIFIER, COLON,
						STRING_TYPE, EQUALS, STRING, SEMICOLON,
						RIGHT_BRACE
				},
				{
						LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES,
						LEFT_BRACE, LET, IDENTIFIER, COLON,
						STRING_TYPE, EQUALS, STRING, SEMICOLON,
						RIGHT_BRACE, ELSE, LEFT_BRACE, LET,
						IDENTIFIER, COLON, STRING_TYPE, EQUALS,
						STRING, SEMICOLON, RIGHT_BRACE
				}
		};

		for (NativeTokenTypes[] testCase : successfulTestCases) {
			LinkedList<Token> tokens = new LinkedList<>(List.of(ifToken));
			tokens.addAll(TokenTestUtil.getTokens(testCase));
			assertSuccessfulCase(tokens);
		}
	}

	@Test
	public void unsuccessfulTestCase() throws Exception {
		Token ifToken = getaTokenFromTokenType(IF, "if");

		NativeTokenTypes[][] testCases = new NativeTokenTypes[][]{
				{
						LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES,
						LEFT_BRACE, LET, IDENTIFIER, COLON,
						STRING_TYPE, EQUALS, STRING, SEMICOLON
				},
				{
						LEFT_PARENTHESIS, BOOLEAN, RIGHT_PARENTHESES,
						LET, IDENTIFIER, COLON,
						STRING_TYPE, EQUALS, STRING, SEMICOLON
				},
				{
						LEFT_PARENTHESIS, BOOLEAN,
						LET, IDENTIFIER, COLON,
						STRING_TYPE, EQUALS, STRING, SEMICOLON
				},
				{
						BOOLEAN, LET, IDENTIFIER, COLON,
						STRING_TYPE, EQUALS, STRING, SEMICOLON
				}
		};

		for (NativeTokenTypes[] testCase : testCases) {
			LinkedList<Token> tokens = new LinkedList<>(List.of(ifToken));
			tokens.addAll(TokenTestUtil.getTokens(testCase));
			asssertUnSuccessfulCase(tokens);
		}
	}

	private static void asssertUnSuccessfulCase(LinkedList<Token> tokens) throws Exception {
		setUpTests result = getSetUpTests(tokens);

		assertTrue(result.optionalExceptionTry().isFail());
	}

	private static void assertSuccessfulCase(LinkedList<Token> tokens) throws Exception {
		setUpTests result = getSetUpTests(tokens);

		assertTrue(result.optionalExceptionTry().isSuccess());
		Optional<ASTNode> optionalASTNode = result.optionalExceptionTry().getSuccess().get();
		assertTrue(optionalASTNode.isPresent());
		assertInstanceOf(IfStatement.class, optionalASTNode.get());
		assertFalse(result.build().possibleBuffer().hasAnyTokensLeft());
	}

	private static setUpTests getSetUpTests(LinkedList<Token> tokens) throws Exception {
		ExpressionCollector expression = new ExpressionCollector();
		ScopeCollector scope = new ScopeCollector();
		IfNodeConstructor ifNodeConstructor = new IfNodeConstructor(expression);
		ifNodeConstructor.acceptInnerConstructor(scope);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = ifNodeConstructor.build(new TokenBuffer(accumulator));


		Try<Optional<ASTNode>, Exception> optionalExceptionTry = build.possibleNode();
		return new setUpTests(build, optionalExceptionTry);
	}

	private record setUpTests(NodeResponse build, Try<Optional<ASTNode>, Exception> optionalExceptionTry) {
	}


}
