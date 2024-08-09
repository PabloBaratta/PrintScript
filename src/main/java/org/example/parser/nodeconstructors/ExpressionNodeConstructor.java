package org.example.parser.nodeconstructors;

import org.example.Token;
import org.example.Try;
import org.example.parser.AST.ASTNode;


import java.util.Optional;
import java.util.Queue;

public class ExpressionNodeConstructor implements NodeConstructor {
    @Override
    public Try<Optional<ASTNode>, Exception> build(Token token, Queue<Token> tokenBuffer) {
        return null;
    }
}
