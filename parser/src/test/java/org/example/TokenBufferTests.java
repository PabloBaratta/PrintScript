package org.example;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TokenBufferTests {
    @Test
    public void emptyBufferTests() {
        TokenBuffer tokenBuffer = new TokenBuffer(List.of());

        emptyBufferAssertions(tokenBuffer);

        TokenBuffer anotherEmptyBuffer = tokenBuffer.consumeToken();

        emptyBufferAssertions(anotherEmptyBuffer);
    }

    @Test
    public void immutabilityTest() {
        TokenBuffer buffer = new TokenBuffer(getListOfDifferentTokenTypes());

        assertGetsTokenType(buffer);
        assertGetsTokenType(buffer);
    }

    @Test
    public void oneElementBuffer() {
        TokenBuffer bufferWithOne = new TokenBuffer(
                List.of(
                        getaTokenFromTokenType(NativeTokenTypes.SEMICOLON))
        );


        assertGetsTokenType(bufferWithOne);
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
                    assertFalse(tokenBuffer.isNextTokenOfAnyOfThisTypes(List.of(type.toTokenType())));
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
        return new Token(tokenType.toTokenType(), "", new Position(0, 0, 0));
    }
}
