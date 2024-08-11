package org.example.nodeconstructors;

import org.example.*;
import org.example.NativeTokenTypes;
import org.example.Token;
import org.example.TokenType;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import static org.example.nodeconstructors.NodeConstructionResponse.response;

//TODO refactor
public class AssignationNodeConstructor implements NodeConstructor{

    private final ExpressionNodeConstructor expressionNodeConstructor;

    AssignationNodeConstructor(ExpressionNodeConstructor expressionNodeConstructor
    ) {
        this.expressionNodeConstructor = expressionNodeConstructor;
    }
    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {
        boolean hasIdentifier = tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType());

        Token identifierToken = tokenBuffer.getToken().get();
        TokenBuffer tokenBufferWithoutIdentifier = tokenBuffer.consumeToken();

        if (hasIdentifier &&
            tokenBufferWithoutIdentifier.hasAnyTokensLeft() &&
            !tokenBufferWithoutIdentifier.isNextTokenOfType(NativeTokenTypes.EQUALS.toTokenType())) {
            return new NodeConstructionResponse(new Try<>(Optional.empty()), tokenBufferWithoutIdentifier);
        }


        Token equals = tokenBufferWithoutIdentifier.getToken().get();
        TokenBuffer tokenBufferWithoutEquals = tokenBufferWithoutIdentifier.consumeToken();

        if (!tokenBufferWithoutEquals.hasAnyTokensLeft()) {
            return response(new SemanticErrorException(equals, "was expecting closing after"),
                    tokenBufferWithoutEquals);
        }

        return handleEqualsToken(identifierToken, equals, tokenBufferWithoutEquals);
    }

    private NodeConstructionResponse handleEqualsToken(Token identifierToken, Token equalsToken, TokenBuffer tokenBuffer) {
        List<Token> tokens = new LinkedList<>();

        Token currentToken = equalsToken;
        while (!tokenBuffer.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

            tokenBuffer = tokenBuffer.consumeToken();

            if (!tokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(currentToken, "was expecting closing after"),
                        tokenBuffer);
            }

            currentToken = tokenBuffer.getToken().get();
            tokens.add(currentToken);
        }

        boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();

        if (noTokensBetweenEqualsAndSemiColon){
            return response(new SemanticErrorException(equalsToken, "was expecting assignation"),
                    tokenBuffer);
        }

        TokenBuffer expressionTokenBuffer = new TokenBuffer(tokens);

        NodeConstructionResponse buildResult = expressionNodeConstructor.build(expressionTokenBuffer);

        if (buildResult.possibleNode().isFail()) {
            return buildResult;
        }

        ASTNode astNode = buildResult.possibleNode().getSuccess().get().get();

        return response(getAssignation(identifierToken, (Expression) astNode), tokenBuffer);
    }

    private static Assignation getAssignation(Token identifierToken, Expression expression) {
        Identifier identifier = new Identifier(identifierToken.associatedString());
        return new Assignation(identifier, expression);
    }


}
