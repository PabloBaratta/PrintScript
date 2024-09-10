package org.example;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.nodeconstructors.Accumulator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TokenBufferTests {
	@Test
	public void emptyBufferTests() throws Exception {
		Accumulator accumulator = new Accumulator(List.of());
		assertDoesNotThrow(() -> new TokenBuffer(accumulator));
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);

		emptyBufferAssertions(tokenBuffer);

		TokenBuffer anotherEmptyBuffer = tokenBuffer.consumeToken();

		emptyBufferAssertions(anotherEmptyBuffer);
	}
/*
	@Test
	public void immutabilityTest() {
		List<Token> tokens = getListOfDifferentTokenTypes();
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer buffer = new TokenBuffer(accumulator);

		assertGetsTokenType(buffer);
		assertGetsTokenType(buffer);
	}
*/
	@Test
	public void oneElementBuffer() throws Exception {
		List<Token> tokens = List.of(getaTokenFromTokenType(NativeTokenTypes.SEMICOLON));
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer bufferWithOne = new TokenBuffer(accumulator);


		assertGetsTokenType(bufferWithOne);

		TokenBuffer tokenBuffer = bufferWithOne.consumeToken();

		emptyBufferAssertions(tokenBuffer);
	}

	private static void assertGetsTokenType(TokenBuffer buffer) {

		assertTrue(buffer.hasAnyTokensLeft());
		assertTrue(buffer.isNextTokenOfAnyOfThisTypes(List.of(NativeTokenTypes.SEMICOLON.toTokenType())));
		assertTrue(buffer.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType()));

		Optional<Token> optionalToken = buffer.getToken();

		assertTrue(optionalToken.isPresent());

		Token token = optionalToken.get();

		assertEquals(NativeTokenTypes.SEMICOLON.toTokenType(), token.type());
	}


	private static void emptyBufferAssertions(TokenBuffer tokenBuffer) {
		assertFalse(tokenBuffer.hasAnyTokensLeft());

		Arrays.stream(NativeTokenTypes.values())
				.forEach(type -> {
					assertFalse(tokenBuffer.isNextTokenOfType(type.toTokenType()));
							List<TokenType> types = List.of(type.toTokenType());
							assertFalse(tokenBuffer.isNextTokenOfAnyOfThisTypes(types));
				}
				);

		assertTrue(tokenBuffer.getToken().isEmpty());
	}

	private static List<Token> getListOfDifferentTokenTypes(){
		List<Token> listOfTokens = new LinkedList<>();
		Arrays.stream(NativeTokenTypes.values()).forEach(
				tokenType -> listOfTokens.add(getaTokenFromTokenType(tokenType))
		);
		return listOfTokens;
	}

	private static Token getaTokenFromTokenType(NativeTokenTypes tokenType) {
		return new Token(tokenType.toTokenType(), "", new Position(0, 0, 0, 0));
	}
}
