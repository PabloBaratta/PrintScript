package org.example;

import org.token.NativeTokenTypes;
import org.token.Position;
import org.token.Token;
import org.token.TokenType;
import functional.Try;
import org.example.nodeconstructors.Accumulator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.TokenTestUtil.getTokens;
import static org.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;

public class TokenBufferTests {
	@Test
	public void emptyBufferTests() {
		Accumulator accumulator = new Accumulator(List.of());
		assertDoesNotThrow(() -> new TokenBuffer(accumulator));
		TokenBuffer tokenBuffer = new TokenBuffer(accumulator);

		emptyBufferAssertions(tokenBuffer);
	}
/*
	@Test
	public void immutabilityTest() {
		List<org.token.Token> tokens = getListOfDifferentTokenTypes();
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer buffer = new TokenBuffer(accumulator);

		assertGetsTokenType(buffer);
		assertGetsTokenType(buffer);
	}
*/
	@Test
	public void oneElementBuffer()  {
		List<Token> tokens = List.of(getaTokenFromTokenType(SEMICOLON));
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer bufferWithOne = new TokenBuffer(accumulator);
		assertGetsTokenType(bufferWithOne);
		emptyBufferAssertions(bufferWithOne);
	}

	@Test
	public void consumesTest() {
		runConsumeTokenTest(List.of(getaTokenFromTokenType(SEMICOLON)), SEMICOLON, true);
		runConsumeTokenTest(getTokens(new NativeTokenTypes[]{NUMBER, SEMICOLON}), SEMICOLON, false);
		runConsumeTokenTest(List.of(getaTokenFromTokenType(SEMICOLON)), IDENTIFIER, false);
	}

	private void runConsumeTokenTest(List<Token> tokens,
									NativeTokenTypes expectedTokenType,
									boolean shouldSucceed) {
		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer bufferWithOne = new TokenBuffer(accumulator);

		Try<Token> tokenTry = bufferWithOne.consumeToken(expectedTokenType);

		if (shouldSucceed) {
			assertTrue(tokenTry.isSuccess());
			assertEquals(expectedTokenType.toTokenType(), tokenTry.getSuccess().get().type());
		} else {
			assertFalse(tokenTry.isSuccess());
		}
	}


	@Test
	public void consumesUntilTest() {
		runConsumeUntilTest(
				new NativeTokenTypes[]{IDENTIFIER},
				IDENTIFIER,
				true,
				List.of()
		);

		runConsumeUntilTest(
				new NativeTokenTypes[]{NUMBER, PLUS, NUMBER, SEMICOLON},
				SEMICOLON,
				true,
				List.of(NUMBER.toTokenType(), PLUS.toTokenType(), NUMBER.toTokenType())
		);

		runConsumeUntilTest(
				new NativeTokenTypes[]{NUMBER, PLUS, NUMBER},
				SEMICOLON,
				false,
				List.of());

	}

	private void runConsumeUntilTest(NativeTokenTypes[] tokenTypes,
									NativeTokenTypes untilToken,
									boolean shouldSucceed,
									List<TokenType> expectedTokenTypes) {
		List<Token> tokens = getTokens(tokenTypes);
		TokenBuffer tokenBuffer = new TokenBuffer(new Accumulator(tokens));

		Try<List<Token>> listTry = tokenBuffer.consumeUntil(untilToken);

		assertEquals(shouldSucceed, listTry.isSuccess());

		if (shouldSucceed) {
			List<Token> consumedTokens = listTry.getSuccess().get();
			assertEquals(expectedTokenTypes.size(), consumedTokens.size());
			assertEquals(expectedTokenTypes, consumedTokens.stream().map(Token::type).toList());
		}

	}


	private static void assertGetsTokenType(TokenBuffer buffer) {

		assertTrue(buffer.hasAnyTokensLeft());
		assertTrue(buffer.peekTokenType(List.of(SEMICOLON.toTokenType())));
		assertTrue(buffer.peekTokenType(SEMICOLON.toTokenType()));

		Optional<Token> optionalToken = buffer.getToken().getSuccess();

		assertTrue(optionalToken.isPresent());

		Token token = optionalToken.get();

		assertEquals(SEMICOLON.toTokenType(), token.type());
	}


	private static void emptyBufferAssertions(TokenBuffer tokenBuffer) {
		assertFalse(tokenBuffer.hasAnyTokensLeft());

		Arrays.stream(values())
				.forEach(type -> {
					assertFalse(tokenBuffer.peekTokenType(type.toTokenType()));
							List<TokenType> types = List.of(type.toTokenType());
							assertFalse(tokenBuffer.peekTokenType(types));
							assertFalse(tokenBuffer.lookaheadType(1, type));
				}
				);

		Try<Token> token = tokenBuffer.getToken();

		assertTrue(token.getFail().isPresent());
		assertInstanceOf(NoMoreTokensException.class, token.getFail().get());

		Arrays.stream(values())
				.forEach(type -> {
						Try<Token> tokenTry = tokenBuffer.consumeToken(type.toTokenType());
						assertTrue(tokenTry.isFail());
						List<TokenType> types = List.of(type.toTokenType());
						Try<Token> tokenTryList = tokenBuffer.consumeToken(types);
						assertTrue(tokenTryList.isFail());
						}
				);


	}

	private static List<Token> getListOfDifferentTokenTypes(){
		List<Token> listOfTokens = new LinkedList<>();
		Arrays.stream(values()).forEach(
				tokenType -> listOfTokens.add(getaTokenFromTokenType(tokenType))
		);
		return listOfTokens;
	}

	private static Token getaTokenFromTokenType(NativeTokenTypes tokenType) {
		return new Token(tokenType.toTokenType(), "", new Position(0, 0, 0, 0));
	}
}
