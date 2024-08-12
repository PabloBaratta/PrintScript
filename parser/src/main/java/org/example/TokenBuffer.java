package org.example;



import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TokenBuffer {


    private final Token token;
    private final List<Token> tokens;

    public TokenBuffer(List<Token> tokens) {
        this.tokens = tokens;
        this.token = tokens.isEmpty() ? null : tokens.getFirst();
    }

    public boolean hasAnyTokensLeft(){
        return !tokens.isEmpty();
    }

    public boolean isNextTokenOfType(TokenType expectedType){
        return isThisTokenType(this.token, expectedType);
    }

    public boolean isNextTokenOfAnyOfThisTypes(List<TokenType> possibleTypes){
        return isThisTokenType(this.token, possibleTypes);
    }


    static boolean isThisTokenType(Token token, List<TokenType> types){
        return token != null && types.contains(getType(token));
    }

    static boolean isThisTokenType(Token token, TokenType expectedType) {
        return token != null && getType(token).equals(expectedType);
    }

    public Optional<Token> getToken() {
        return Optional.ofNullable(token);
    }

    public TokenBuffer consumeToken(){
        List<Token> newTokenList = getTokensWithoutFirst();
        return new TokenBuffer(newTokenList);
    }

    private List<Token> getTokensWithoutFirst() {
        List<Token> newTokenList = new LinkedList<>();
        for (int i = 1; i < tokens.size(); i++) {
            newTokenList.add(this.tokens.get(i));
        }
        return newTokenList;
    }

    private static TokenType getType(Token token) {
        return token.type();
    }
}
