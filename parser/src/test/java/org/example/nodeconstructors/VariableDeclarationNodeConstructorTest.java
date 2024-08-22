package org.example.nodeconstructors;


import org.example.ASTNode;
import org.example.TokenBuffer;
import org.example.VariableDeclaration;
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

class VariableDeclarationNodeConstructorTest {

	@Test
	public void doesNotRecognizeOtherTokens() {
		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();

		VariableDeclarationNodeConstructor builder = getVDNodeConst(collector);

		Arrays.stream(values())
				.filter(type -> type != LET)
				.forEach(type ->
				{
					List<Token> tokens = List.of(getaTokenFromTokenType(type));
					TokenBuffer tokenBuffer = new TokenBuffer(tokens);
					NodeResponse build = builder.build(tokenBuffer);
					assertTrue(build.possibleNode().isSuccess());
					assertTrue(build.possibleNode().getSuccess().get().isEmpty());
				});
	}

	@Test
	public void successfulStringVariableDeclarationAssignation() {
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
	public void alwaysMissingTokenTypes() {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, EQUALS, STRING, SEMICOLON
		};

		List<Token> defaultCorrectSequence = getTokens(nativeTokenTypes);
		int originalSize = defaultCorrectSequence.size();
		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();

		VariableDeclarationNodeConstructor builder = getVDNodeConst(collector);
		for (int i = 1; i < originalSize; i++) {
			defaultCorrectSequence.removeLast();
			TokenBuffer tokenBuffer = new TokenBuffer(defaultCorrectSequence);
			NodeResponse build = builder.build(tokenBuffer);
			assertTrue(build.possibleNode().isFail());
			System.out.println(build.possibleNode().getFail().get().getMessage());
		}
	}

	@Test
	public void incorrectSituations() {
		NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, STRING_TYPE, EQUALS, STRING, SEMICOLON
		};

		assertIncorrectSituations(nativeTokenTypes);

		nativeTokenTypes = new NativeTokenTypes[]{
				LET, IDENTIFIER, COLON, STRING_TYPE, STRING, SEMICOLON
		};
		assertIncorrectSituations(nativeTokenTypes);
	}

	private static void assertIncorrectSituations(NativeTokenTypes[] nativeTokenTypes) {
		List<Token> defaultCorrectSequence = getTokens(nativeTokenTypes);
		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();

		VariableDeclarationNodeConstructor builder = getVDNodeConst(collector);

		NodeResponse build = builder.build(new TokenBuffer(defaultCorrectSequence));

		assertTrue(build.possibleNode().isFail());
	}

	@Test
	public void successfulVariableDeclaration() {
		List<Token> tokens = getDefaultCorrectSequenceForVarDecl();
		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();

		NodeConstructor variableDeclarationNodeConstructor = getVDNodeConst(collector);

		NodeResponse build = variableDeclarationNodeConstructor.build(new TokenBuffer(tokens));


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

	private static void successfulVarDeclAss(List<Token> tokens, int numberOfExpressionTokens) {


		ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
		NodeConstructor variableDeclarationNodeConstructor = getVDNodeConst(collector);

		NodeResponse build = variableDeclarationNodeConstructor.build(new TokenBuffer(tokens));


		assertTrue(build.possibleNode().isSuccess());
		//consumes all tokens
		assertFalse(build.possibleBuffer().hasAnyTokensLeft());


		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();

		assertTrue(optionalASTNode.isPresent());

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
