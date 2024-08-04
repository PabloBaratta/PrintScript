package org.example.lexer;

import org.example.NativeTokenTypes;
import org.example.Token;
import org.example.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenConstructorTest {


    TokenConstructor keywordConstructor = new TokenConstructor(PrintScriptTokenConfig.keywordTokenTypeMap());
    TokenConstructor operatorConstructor = new TokenConstructor(PrintScriptTokenConfig.keywordTokenTypeMap());

    @Test
    void keywordConstructorLetTest() {
        String input = "let a : string = \"string\"";
        int offset = 0;
        Optional<Token> optionalToken = keywordConstructor.constructToken(input, offset);

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();
        Token expectedToken = new Token(NativeTokenTypes.LET.toTokenType(), "let", 0, 3);

        assertEquals(expectedToken, token);
    }

    @Test
    void keywordConstructorStringTest() {
        String input = "let a : string = \"string\"";
        int offset = 8;
        Optional<Token> optionalToken = keywordConstructor.constructToken(input.substring(offset), offset);

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();
        Token expectedToken = new Token(NativeTokenTypes.STRING_TYPE.toTokenType(), "string", 8, 6);

        assertEquals(expectedToken, token);
    }
}