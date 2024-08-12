package org.example;


import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenConstructorTest {


    private PrintScriptTokenConfig PrintScriptTokenConfig;
    TokenConstructor keywordConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMap());
    TokenConstructor operatorConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap());

    TokenConstructor separatorConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMap());

    @Test
    void keywordConstructorLetTest() {
        String input = "let a : string = \"string\"";
        int offset = 0;
        String associatedString = "let";
        Token expectedToken = new Token(NativeTokenTypes.LET.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, keywordConstructor);
    }

    @Test
    void keywordConstructorStringTest() {
        String input = "let a : string = \"string\"";
        int offset = 8;
        String associatedString = "string";
        Token expectedToken = new Token(NativeTokenTypes.STRING_TYPE.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, keywordConstructor);
    }

    @Test
    void keywordConstructorNumberTest() {
        String input = "let a : number = 5";
        int offset = 8;
        String associatedString = "number";
        Token expectedToken = new Token(NativeTokenTypes.NUMBER_TYPE.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, keywordConstructor);
    }

    @Test
    void operatorConstructorPlusTest() {
        String input = "let a : number = 5 + 5";
        int offset = 19;
        String associatedString = "+";
        Token expectedToken = new Token(NativeTokenTypes.PLUS.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor);
        String inputWithoutSpaceBetween = "let a : number = 5 +5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor);
    }

    @Test
    void operatorConstructorMinusTest() {
        String input = "let a : number = 5 - 5";
        int offset = 19;
        String associatedString = "-";
        Token expectedToken = new Token(NativeTokenTypes.MINUS.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor);
        String inputWithoutSpaceBetween = "let a : number = 5 -5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor);
    }

    @Test
    void operatorConstructorMultiplicationTest() {
        String input = "let a : number = 5 * 5";
        int offset = 19;
        String associatedString = "*";
        Token expectedToken = new Token(NativeTokenTypes.ASTERISK.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor);
        String inputWithoutSpaceBetween = "let a : number = 5 *5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor);
    }

    @Test
    void operatorConstructorSlashTest() {
        String input = "let a : number = 5 / 5";
        int offset = 19;
        String associatedString = "/";
        Token expectedToken = new Token(NativeTokenTypes.SLASH.toTokenType(), associatedString, offset, associatedString.length());
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor);
        String inputWithoutSpaceBetween = "let a : number = 5 /5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor);
    }




    private void tokenAssertionMethod(String input, int offset, Token expectedToken,
                                      TokenConstructor constructor) {

        Optional<Token> optionalToken = constructor.constructToken(input.substring(offset), offset);

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();

        assertEquals(expectedToken, token);
    }
}