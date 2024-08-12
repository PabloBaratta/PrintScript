package org.example;

import org.example.lexer.token.NativeTokenTypes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenBufferTests {
    @Test
    public void emptyBufferTests() {
        TokenBuffer tokenBuffer = new TokenBuffer(List.of());

        emptyBufferAssertions(tokenBuffer);

        TokenBuffer anotherEmptyBuffer = tokenBuffer.consumeToken();

        emptyBufferAssertions(anotherEmptyBuffer);
    }

    private static void emptyBufferAssertions(TokenBuffer tokenBuffer) {
        assertFalse(tokenBuffer.hasAnyTokensLeft());

        Arrays.stream(NativeTokenTypes.values())
                .forEach(type -> assertFalse(tokenBuffer.isNextTokenOfType(type.toTokenType())));

        assertTrue(tokenBuffer.getToken().isEmpty());
    }
}
