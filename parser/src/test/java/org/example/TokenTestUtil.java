package org.example;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TokenTestUtil {

    public static List<Token> getTokens(NativeTokenTypes[] nativeTokenTypes) {
        List<Token> tokens = new LinkedList<>();

        Arrays.stream(nativeTokenTypes).forEach(type -> tokens.add(getaTokenFromTokenType(type)));
        return tokens;
    }

    public static Token getaTokenFromTokenType(NativeTokenTypes tokenType) {
        return new Token(tokenType.toTokenType(), "", new Position(0, 0, 0));
    }
}
