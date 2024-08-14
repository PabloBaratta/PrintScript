package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.TokenBuffer;
import org.example.lexer.utils.Try;

import java.util.Optional;

public record NodeConstructionResponse(
        Try<Optional<ASTNode>, Exception> possibleNode,
        TokenBuffer possibleBuffer
) {

    static NodeConstructionResponse response(Exception exception, TokenBuffer buffer){
        return new NodeConstructionResponse(
                new Try<>(exception),
                buffer
        );
    }

    static NodeConstructionResponse response(ASTNode node, TokenBuffer buffer){
        return new NodeConstructionResponse(
                new Try<>(Optional.of(node)),
                buffer
        );
    }

    static NodeConstructionResponse emptyResponse(TokenBuffer buffer){
        return new NodeConstructionResponse(
                new Try<>(Optional.empty()),
                buffer
        );
    }
}
