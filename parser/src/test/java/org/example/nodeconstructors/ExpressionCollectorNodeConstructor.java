package org.example.nodeconstructors;

import org.example.TextLiteral;
import org.example.TokenBuffer;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExpressionCollectorNodeConstructor implements NodeConstructor{

    List<Token> collectedTokens = new LinkedList<>();
    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {
        while (tokenBuffer.hasAnyTokensLeft()) {
            collectedTokens.add(tokenBuffer.getToken().get());
            tokenBuffer = tokenBuffer.consumeToken();
        }

        return new NodeConstructionResponse(
                new Try<>(Optional.of(new TextLiteral("default value"))),
                tokenBuffer
        );
    }

    public List<Token> getCollectedTokens() {
        return collectedTokens;
    }
}
