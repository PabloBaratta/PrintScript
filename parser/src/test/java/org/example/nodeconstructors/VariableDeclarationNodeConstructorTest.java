package org.example.nodeconstructors;


import org.example.ASTNode;
import org.example.TokenBuffer;
import org.example.VariableDeclaration;
import org.token.NativeTokenTypes;
import org.token.Token;
import org.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.TokenTestUtil.getTokens;
import static org.example.TokenTestUtil.getaTokenFromTokenType;
import static org.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;

class VariableDeclarationNodeConstructorTest {

	@Test
	public void doesNotRecognizeOtherTokens() {
		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		VariableDeclarationNodeConstructor builder = getVDNodeConst(collector);

		Arrays.stream(values())
				.filter(type -> type != LET)
				.forEach(type ->
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
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());
				});
	}

	@Test
	public void successfulStringVariableDeclarationAssignation() throws Exception {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, SEMICOLON
		};

		List<Token> tokens = getTokens(nativeTokenTypes);
		int intermediateTokens = 1;
		successfulVarDeclAss(tokens, intermediateTokens);

		nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, PLUS, STRING, SEMICOLON
		};

		tokens = getTokens(nativeTokenTypes);
		intermediateTokens = 3;
		successfulVarDeclAss(tokens, intermediateTokens);
	}


	@Test
	public void alwaysMissingTokenTypes() throws Exception {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, SEMICOLON
		};

		List<Token> defaultCorrectSequence = getTokens(nativeTokenTypes);
		int originalSize = defaultCorrectSequence.size();
		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		VariableDeclarationNodeConstructor builder = getVDNodeConst(collector);
		for (int i = 1; i < originalSize; i++) {
			defaultCorrectSequence.removeLast();
			Accumulator accumulator = new Accumulator(defaultCorrectSequence);
			TokenBuffer tokenBuffer = new TokenBuffer(accumulator);
			NodeResponse build = builder.build(tokenBuffer);
			assertTrue(build.possibleNode().isFail());
			System.out.println(build.possibleNode().getFail().get().getMessage());
		}
	}

	@Test
	public void incorrectSituations() throws Exception {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, STRING_TYPE, EQUALS, STRING, SEMICOLON
		};

		assertIncorrectSituations(nativeTokenTypes);

		nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, STRING, SEMICOLON
		};
		assertIncorrectSituations(nativeTokenTypes);
	}

	private static void assertIncorrectSituations(NativeTokenTypes[] nativeTokenTypes) throws Exception {
		List<Token> defaultCorrectSequence = getTokens(nativeTokenTypes);
		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		VariableDeclarationNodeConstructor builder = getVDNodeConst(collector);

		Accumulator accumulator = new Accumulator(defaultCorrectSequence);
		NodeResponse build = builder.build(new TokenBuffer(accumulator));

		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void successfulVariableDeclaration() throws Exception {
		List<Token> tokens = getDefaultCorrectSequenceForVarDecl();
		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		NodeConstructor variableDeclarationNodeConstructor = getVDNodeConst(collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = variableDeclarationNodeConstructor.build(new TokenBuffer(accumulator));


		assertTrue(build.possibleNode().isSuccess());
		//consumes all tokens
		assertFalse(build.possibleBuffer().hasAnyTokensLeft());


		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();

		assertTrue(optionalASTNode.isPresent());

		ASTNode astNode = optionalASTNode.get();
		assertInstanceOf(VariableDeclaration.class, astNode);

		VariableDeclaration node = (VariableDeclaration) astNode;

		assertTrue(node.getExpression().isEmpty());
		assertTrue(collector.collectedTokens.isEmpty());
	}






	private static void successfulVarDeclAss(List<Token> tokens, int numberOfExpressionTokens)
			throws Exception {


		CollectorNodeConstructor collector = new CollectorNodeConstructor();

		NodeConstructor constructor = getVDNodeConst(collector);

		Accumulator accumulator = new Accumulator(tokens);
		NodeResponse build = constructor.build(new TokenBuffer(accumulator));


		assertTrue(build.possibleNode().isSuccess(), "Has Parsed Right");
		//consumes all tokens
		assertFalse(build.possibleBuffer().hasAnyTokensLeft(), "Inner Buffer consumes all tokens");


		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();

		assertTrue(optionalASTNode.isPresent(), "Recognized Expression");

		ASTNode astNode = optionalASTNode.get();
		assertInstanceOf(VariableDeclaration.class, astNode);

		VariableDeclaration node = (VariableDeclaration) astNode;

		assertTrue(node.getExpression().isPresent());

		assertEquals(numberOfExpressionTokens, collector.collectedTokens.size());

		collector.collectedTokens.forEach( token ->
				assertNotEquals(SEMICOLON.toTokenType(), token.type())
		);
	}



	private static List<Token> getDefaultCorrectSequenceForVarDecl() {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, SEMICOLON
		};

		return getTokens(nativeTokenTypes);
	}



	private static VariableDeclarationNodeConstructor getVDNodeConst(NodeConstructor exp) {
		TokenType let = LET.toTokenType();
		TokenType str = STRING_TYPE.toTokenType();
		TokenType num = NUMBER_TYPE.toTokenType();
		return new VariableDeclarationNodeConstructor(exp,
				List.of(let), List.of(str, num));
	}


}
