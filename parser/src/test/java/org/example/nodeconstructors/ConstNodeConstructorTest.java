package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.ConstDeclaration;
import org.example.TokenBuffer;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.TokenTestUtil.getTokens;
import static org.example.TokenTestUtil.getaTokenFromTokenType;
import static org.example.lexer.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConstNodeConstructorTest {

	@Test
	public void constDoesNotRecognizeOtherTokens() {
		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		ConstNodeConstructor builder = getConstNodeConst(collector);

		Arrays.stream(values())
				.filter(type -> type != CONST)
				.forEach(type ->
				{
					List<Token> tokens = List.of(getaTokenFromTokenType(type));
					Accumulator accumulator = new Accumulator(tokens);
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
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());
				});
	}


	@Test
	public void testConstCorrectSituations() throws Exception {

		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				CONST, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, SEMICOLON
		};

		List<Token> tokens = getTokens(nativeTokenTypes);
		int intermediateTokens = 1;
		successfulConstDeclAss(tokens, intermediateTokens);

		nativeTokenTypes = new NativeTokenTypes[]{
				CONST, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, PLUS, STRING, SEMICOLON
		};

		tokens = getTokens(nativeTokenTypes);
		intermediateTokens = 3;
		successfulConstDeclAss(tokens, intermediateTokens);
	}

	@Test
	public void testConstInCorrectSituations() throws Exception {

		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				CONST, IDENTIFIER, COLON, STRING_TYPE, SEMICOLON
		};

		assertIncorrectSituations(nativeTokenTypes);

		nativeTokenTypes = new NativeTokenTypes[]{
				CONST, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, PLUS, STRING
		};

		assertIncorrectSituations(nativeTokenTypes);

		nativeTokenTypes = new NativeTokenTypes[]{
				CONST, IDENTIFIER, COLON, STRING_TYPE, EQUALS, SEMICOLON
		};

		assertIncorrectSituations(nativeTokenTypes);
	}


	private static void successfulConstDeclAss(List<Token> tokens, int numberOfExpressionTokens)
                                                throws Exception {


		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		NodeConstructor constructor = getConstNodeConst(collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = constructor.build(new TokenBuffer(accumulator));


		assertTrue(build.possibleNode().isSuccess(), "Has Parsed Right");
		//consumes all tokens
		assertFalse(build.possibleBuffer().hasAnyTokensLeft(), "Inner Buffer consumes all tokens");


		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();

		assertTrue(optionalASTNode.isPresent(), "Recognized Expression");

		ASTNode astNode = optionalASTNode.get();
		assertInstanceOf(ConstDeclaration.class, astNode);

		assertEquals(numberOfExpressionTokens, collector.collectedTokens.size());

		collector.collectedTokens.forEach( token ->
				assertNotEquals(SEMICOLON.toTokenType(), token.type())
		);
	}

	private static void assertIncorrectSituations(NativeTokenTypes[] nativeTokenTypes) throws Exception {
		List<Token> defaultCorrectSequence = getTokens(nativeTokenTypes);
		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		ConstNodeConstructor builder = getConstNodeConst(collector);

		Accumulator accumulator = new Accumulator(defaultCorrectSequence);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));

		assertTrue(build.possibleNode().isFail());
	}
	private static ConstNodeConstructor getConstNodeConst(NodeConstructor exp) {
		TokenType str = STRING_TYPE.toTokenType();
		TokenType num = NUMBER_TYPE.toTokenType();
		TokenType bool = BOOLEAN_TYPE.toTokenType();
		return new ConstNodeConstructor(exp, List.of(str, num, bool));
	}

}
