package org.example;

import org.example.lexer.Lexer;
import org.example.lexer.NoMoreTokensAvailableException;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private final List<Character> whiteSpaces = Arrays.asList(' ', '\t', '\n');

    @Test
    public void testHasNext() {
        TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
                NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
        ));

        Collection<TokenConstructor> tokenConstructors = List.of(new TokenConstructorImpl(Map.of(
                NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType(), NativeTokenTypes.SEMICOLON.getRegex(), NativeTokenTypes.SEMICOLON.toTokenType()
        )));

        Lexer lexerWithTokens = new Lexer("let my_variable;", tokenConstructors, keywordConstructor, whiteSpaces);
        assertTrue(lexerWithTokens.hasNext());

        lexerWithTokens.getNext();
        assertTrue(lexerWithTokens.hasNext());

        lexerWithTokens.getNext();
        lexerWithTokens.getNext();
        assertFalse(lexerWithTokens.hasNext());

        Lexer emptyLexer = new Lexer("", tokenConstructors, keywordConstructor, whiteSpaces);
        assertFalse(emptyLexer.hasNext());
    }

    @Test
    public void testKeywordToken() {
        TokenConstructorImpl keywordConstructor = new TokenConstructorImpl(Map.of(
                NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
        ));

        Lexer lexer = new Lexer("let a = 5", Collections.singletonList(new TokenConstructorImpl(Map.of())), keywordConstructor, whiteSpaces);

        Try<Token, Exception> result = lexer.getNext();

        assertTrue(result.isSuccess());
        Token token = result.getSuccess().orElseThrow();

        assertEquals(NativeTokenTypes.LET.toTokenType(), token.type(), "Expected token: KEYWORD");

        assertEquals("let", token.associatedString(), "Expected associated string: 'let'");
    }

    @Test
    public void testNoMoreTokens() {

        TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
                NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
        ));

        Lexer lexer = new Lexer("", Collections.singletonList(new TokenConstructorImpl(Map.of())), keywordConstructor, whiteSpaces);

        Try<Token, Exception> result = lexer.getNext();

        assertTrue(result.isFail());
        assertTrue(result.getFail().get() instanceof NoMoreTokensAvailableException);
    }

    @Test
    public void testKeywordAndIdentifier() {

        TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
                NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType()
        ));

        Lexer lexer = new Lexer("let letter", Collections.singletonList(new TokenConstructorImpl(Map.of(
                NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType()
        ))), keywordConstructor, whiteSpaces);

        Try<Token, Exception> result1 = lexer.getNext();
        assertTrue(result1.isSuccess());
        Token token1 = result1.getSuccess().orElseThrow();
        assertEquals(NativeTokenTypes.LET.toTokenType(), token1.type(), "Expected token type: KEYWORD");
        assertEquals("let", token1.associatedString(), "Expected associated string: 'let'");

        Try<Token, Exception> result2 = lexer.getNext();
        assertTrue(result2.isSuccess());
        Token token2 = result2.getSuccess().orElseThrow();
        assertEquals(NativeTokenTypes.IDENTIFIER.toTokenType(), token2.type(), "Expected token type: IDENTIFIER");
        assertEquals("letter", token2.associatedString(), "Expected associated string: 'letter'");
    }


    @Test
    public void testVariableDeclaration() {

        TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
                NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType(),
                NativeTokenTypes.STRING_TYPE.getRegex(), NativeTokenTypes.STRING_TYPE.toTokenType()
        ));

        Collection<TokenConstructor> tokenConstructors = List.of(new TokenConstructorImpl(Map.of(
                NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType(),
                NativeTokenTypes.EQUALS.getRegex(), NativeTokenTypes.EQUALS.toTokenType(),
                NativeTokenTypes.STRING.getRegex(), NativeTokenTypes.STRING.toTokenType(),
                NativeTokenTypes.SEMICOLON.getRegex(), NativeTokenTypes.SEMICOLON.toTokenType(),
                NativeTokenTypes.COLON.getRegex(), NativeTokenTypes.COLON.toTokenType()
        )));

        Lexer lexer = new Lexer("let my_cool_variable: string = \"ciclon\";", tokenConstructors, keywordConstructor, whiteSpaces);

        List<Token> expectedTokens = Arrays.asList(
                new Token(NativeTokenTypes.LET.toTokenType(), "let", 0, 3),
                new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "my_cool_variable", 4, 16),
                new Token(NativeTokenTypes.COLON.toTokenType(), ":", 20, 1),
                new Token(NativeTokenTypes.STRING_TYPE.toTokenType(), "string", 22, 6),
                new Token(NativeTokenTypes.EQUALS.toTokenType(), "=", 29, 1),
                new Token(NativeTokenTypes.STRING.toTokenType(), "\"ciclon\"", 31, 8),
                new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", 39, 1)
        );

        List<Token> actualTokens = new ArrayList<>();
        Try<Token, Exception> result;

        while (lexer.hasNext()) {
            result = lexer.getNext();
            if (result.isSuccess()) {
                actualTokens.add(result.getSuccess().orElseThrow());
            } else {
                result.getFail().orElseThrow();
            }
        }

        assertEquals(expectedTokens, actualTokens, "Token lists do not match");
    }

    @Test
    public void testPrintln() {

        TokenConstructor keywordConstructor = new TokenConstructorImpl(Map.of(
                NativeTokenTypes.PRINTLN.getRegex(), NativeTokenTypes.PRINTLN.toTokenType()
        ));

        Collection<TokenConstructor> tokenConstructors = List.of(new TokenConstructorImpl(Map.of(
                NativeTokenTypes.IDENTIFIER.getRegex(), NativeTokenTypes.IDENTIFIER.toTokenType(),
                NativeTokenTypes.EQUALS.getRegex(), NativeTokenTypes.EQUALS.toTokenType(),
                NativeTokenTypes.STRING.getRegex(), NativeTokenTypes.STRING.toTokenType(),
                NativeTokenTypes.SEMICOLON.getRegex(), NativeTokenTypes.SEMICOLON.toTokenType(),
                NativeTokenTypes.LEFT_PARENTHESIS.getRegex(), NativeTokenTypes.LEFT_PARENTHESIS.toTokenType(),
                NativeTokenTypes.RIGHT_PARENTHESES.getRegex(), NativeTokenTypes.RIGHT_PARENTHESES.toTokenType(),
                NativeTokenTypes.COLON.getRegex(), NativeTokenTypes.COLON.toTokenType()
        )));

        List<Character> whiteSpaces = Arrays.asList(' ', '\t', '\n');
        Lexer lexer = new Lexer("println(my_cool_variable);", tokenConstructors, keywordConstructor, whiteSpaces);

        List<Token> expectedTokens = Arrays.asList(
                new Token(NativeTokenTypes.PRINTLN.toTokenType(), "println", 0, 7),
                new Token(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType(), "(", 7, 1),
                new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "my_cool_variable", 8, 16),
                new Token(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType(), ")", 24, 1),
                new Token(NativeTokenTypes.SEMICOLON.toTokenType(), ";", 25, 1)
        );

        List<Token> actualTokens = new ArrayList<>();
        Try<Token, Exception> result;

        while (lexer.hasNext()) {
            result = lexer.getNext();
            if (result.isSuccess()) {
                actualTokens.add(result.getSuccess().orElseThrow());
            } else {
                result.getFail().orElseThrow();
            }
        }

        assertEquals(expectedTokens, actualTokens, "Token lists do not match");
    }

}