package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.Token;
import org.example.TokenType;
import org.example.lexer.utils.Try;


import java.util.List;
import java.util.Optional;
import java.util.Queue;

public interface NodeConstructor {

    Try<Optional<ASTNode>, Exception> build(Token token, Queue<Token> tokenBuffer);
    static TokenType getType(Token token) {
        return token.type();
    }

    static boolean isThisTokenType(Token token, List<TokenType> types){
        return types.contains(getType(token));
    }



}
