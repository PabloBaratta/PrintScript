package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.Assignation;
import org.example.TokenBuffer;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.example.TokenTestUtil.getTokens;
import static org.example.TokenTestUtil.getaTokenFromTokenType;
import static org.example.lexer.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;

public class AssignationNodeConstructorTest {

	@Test
	public void doesNotRecognizeOtherTokens() {

		NodeConstructor builder = new AssignationNodeConstructor(new CollectorNodeConstructor());

		Arrays.stream(NativeTokenTypes.values()).filter(
				type -> !type.equals(NativeTokenTypes.IDENTIFIER)
		).forEach(type ->
				{
				Accumulator accumulator = new Accumulator(List.of(getaTokenFromTokenType(type)));
					TokenBuffer tokenBuffer = null;
					try {
						tokenBuffer = new TokenBuffer(accumulator);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					TokenBuffer finalTokenBuffer = tokenBuffer;
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
				type -> !type.equals(NativeTokenTypes.EQUALS)).
				forEach(type ->
				{
					Token e1 = getaTokenFromTokenType(IDENTIFIER);
					Token e2 = getaTokenFromTokenType(type);
					Accumulator accumulator = new Accumulator(List.of(e1, e2));
					TokenBuffer tokenBuffer = null;
					try {
						tokenBuffer = new TokenBuffer(accumulator);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					TokenBuffer finalTokenBuffer = tokenBuffer;
					NodeResponse build = null;
					try {
						build = builder.build(tokenBuffer);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					assertTrue(build.possibleNode().isSuccess());
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());});
	}

	@Test
	public void successfulScenarios() throws Exception {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				IDENTIFIER, EQUALS, STRING, SEMICOLON
		};

		List<Token> tokens = getTokens(nativeTokenTypes);

		int intermediateTokens = 1;

		assertSuccess(tokens, intermediateTokens);

		nativeTokenTypes = new NativeTokenTypes[]{
				IDENTIFIER, EQUALS, STRING, PLUS, NUMBER, SEMICOLON
		};

		tokens = getTokens(nativeTokenTypes);

		intermediateTokens = 3;

		assertSuccess(tokens, intermediateTokens);
	}

	@Test
	public void syntaxErrors() throws Exception {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				IDENTIFIER, EQUALS, STRING, SEMICOLON
		};

		NodeConstructor builder = new AssignationNodeConstructor(new CollectorNodeConstructor());


		List<Token> tokens = getTokens(nativeTokenTypes);

		int originalTokenListSize = tokens.size();
		for (int i = 2; i < originalTokenListSize; i++) {
			tokens.removeLast();
			Accumulator accumulator = new Accumulator(tokens);
			NodeResponse build = builder.build(new TokenBuffer(accumulator));
			assertTrue(build.possibleNode().isFail());
		}

	}

	private void assertSuccess(List<Token> tokens, int intermediateTokens) throws Exception {
		CollectorNodeConstructor collector = new CollectorNodeConstructor();
		NodeConstructor assignationNodeConstructor = new AssignationNodeConstructor(collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = assignationNodeConstructor.build(new TokenBuffer(accumulator));

		assertTrue(build.possibleNode().isSuccess());
		assertFalse(build.possibleBuffer().hasAnyTokensLeft());
		assertTrue(build.possibleNode().getSuccess().isPresent());
		ASTNode astNode = build.possibleNode().getSuccess().get().get();
		assertInstanceOf(Assignation.class, astNode);
		assertEquals(intermediateTokens, collector.collectedTokens.size());
		collector.collectedTokens.forEach( token ->
				assertNotEquals(SEMICOLON.toTokenType(), token.type())
		);
	}
}
