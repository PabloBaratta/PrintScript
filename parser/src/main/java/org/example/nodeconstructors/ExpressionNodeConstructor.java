package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.Token;
import org.example.lexer.utils.Try;


import java.util.Optional;
import java.util.Queue;

public class ExpressionNodeConstructor implements NodeConstructor {
    @Override
    public Try<Optional<ASTNode>, Exception> build(Token token, Queue<Token> tokenBuffer) {
        return null;
    }
}
