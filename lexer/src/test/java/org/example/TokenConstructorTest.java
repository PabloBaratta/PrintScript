package org.example;

import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenConstructorTest {


    TokenConstructor keywordConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMap());
    TokenConstructor operatorConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap());

    TokenConstructor separatorConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMap());

    @Test
    void keywordConstructorLetTest() {
        String input = "let a : string = \"string\"";
        int offset = 0;
        int line = 0;
        String associatedString = "let";
        Token expectedToken = new Token(NativeTokenTypes.LET.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, keywordConstructor, line);
    }

    @Test
    void keywordConstructorStringTest() {
        String input = "let a : string = \"string\"";
        int offset = 8;
        int line = 0;
        String associatedString = "string";
        Token expectedToken = new Token(NativeTokenTypes.STRING_TYPE.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, keywordConstructor, line);
    }

    @Test
    void keywordConstructorNumberTest() {
        String input = "let a : number = 5";
        int offset = 8;
        int line = 0;
        String associatedString = "number";
        Token expectedToken = new Token(NativeTokenTypes.NUMBER_TYPE.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, keywordConstructor, line);
    }

    @Test
    void operatorConstructorPlusTest() {
        String input = "let a : number = 5 + 5";
        int offset = 19;
        int line = 0;
        String associatedString = "+";
        Token expectedToken = new Token(NativeTokenTypes.PLUS.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
        String inputWithoutSpaceBetween = "let a : number = 5 +5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
    }

    @Test
    void operatorConstructorMinusTest() {
        String input = "let a : number = 5 - 5";
        int offset = 19;
        int line = 0;
        String associatedString = "-";
        Token expectedToken = new Token(NativeTokenTypes.MINUS.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
        String inputWithoutSpaceBetween = "let a : number = 5 -5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
    }

    @Test
    void operatorConstructorMultiplicationTest() {
        String input = "let a : number = 5 * 5";
        int offset = 19;
        int line = 0;
        String associatedString = "*";
        Token expectedToken = new Token(NativeTokenTypes.ASTERISK.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
        String inputWithoutSpaceBetween = "let a : number = 5 *5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
    }

    @Test
    void operatorConstructorSlashTest() {
        String input = "let a : number = 5 / 5";
        int offset = 19;
        int line = 0;
        String associatedString = "/";
        Token expectedToken = new Token(NativeTokenTypes.SLASH.toTokenType(), associatedString, new Position(offset, associatedString.length(), line));
        tokenAssertionMethod(input, offset, expectedToken, operatorConstructor, line);
        String inputWithoutSpaceBetween = "let a : number = 5 /5";
        tokenAssertionMethod(inputWithoutSpaceBetween, offset, expectedToken, operatorConstructor, line);
    }




/*    private void tokenAssertionMethod(String input, int offset, Token expectedToken,
                                      TokenConstructor constructor, int line) {

        Optional<Token> optionalToken = constructor.constructToken(input.substring(offset), offset, line);

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();

        assertEquals(expectedToken, token);
    }*/

    private void tokenAssertionMethod(String input, int offset, Token expectedToken,
                                      TokenConstructor constructor, int line) {

        Optional<Token> optionalToken = constructor.constructToken(input.substring(offset), offset, line);

        assertTrue(optionalToken.isPresent());

        Token actualToken = optionalToken.get();

        assertEquals(expectedToken.type(), actualToken.type());
        assertEquals(expectedToken.associatedString(), actualToken.associatedString());
        assertEquals(expectedToken.position().getOffset(), actualToken.position().getOffset());
        assertEquals(expectedToken.position().getLength(), actualToken.position().getLength());
        assertEquals(expectedToken.position().getLine(), actualToken.position().getLine());
    }



}