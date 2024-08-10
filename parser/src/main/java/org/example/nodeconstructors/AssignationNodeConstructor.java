package org.example.nodeconstructors;

import org.example.*;
import org.example.NativeTokenTypes;
import org.example.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

//TODO refactor
public class AssignationNodeConstructor implements NodeConstructor{

    private final ExpressionNodeConstructor expressionNodeConstructor;

    AssignationNodeConstructor(ExpressionNodeConstructor expressionNodeConstructor
    ) {
        this.expressionNodeConstructor = expressionNodeConstructor;
    }
    @Override
    public Try<Optional<ASTNode>, Exception> build(Token token, Queue<Token> tokenBuffer) {
        if (!NodeConstructor.isTokenType(token, NativeTokenTypes.IDENTIFIER.toTokenType())) {
            return new Try<>(Optional.empty());
        }

        Token identifierToken = tokenBuffer.poll();
        if (!NodeConstructor.isTokenType(identifierToken, NativeTokenTypes.IDENTIFIER.toTokenType())) {
            return new Try<>(new SemanticErrorException(identifierToken, "was expecting identifier"));
        }

        Token equals = tokenBuffer.poll();

        if (NodeConstructor.isTokenType(equals, NativeTokenTypes.EQUALS.toTokenType())) {
            return handleEqualsToken(identifierToken, equals, tokenBuffer);
        }

        return new Try<>(new SemanticErrorException(equals, "was expecting variable assignation"));

    }

    private Try<Optional<ASTNode>, Exception> handleEqualsToken(Token identifierToken, Token equalsToken, Queue<Token> tokenBuffer) {
        Queue<Token> tokens = new LinkedList<>();
        Token proxUnknownToken = equalsToken;

        while (!NodeConstructor.isTokenType(proxUnknownToken, NativeTokenTypes.SEMICOLON.toTokenType())) {
            tokens.add(proxUnknownToken);
            proxUnknownToken = tokenBuffer.poll();
            if (proxUnknownToken == null) {
                return new Try<>(new SemanticErrorException(equalsToken, "was expecting closing after"));
            }
        }

        boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();
        if (noTokensBetweenEqualsAndSemiColon){
            return new Try<>(new SemanticErrorException(equalsToken, "was expecting assignation"));
        }

        Try<Optional<ASTNode>, Exception> buildResult = expressionNodeConstructor.build(tokens.poll(), tokens);
        if (buildResult.isFail()) {
            return buildResult;
        }

        return new Try<>(Optional.of(getAssignation(identifierToken, (Expression) buildResult.getSuccess().get().get())));
    }

    private static Assignation getAssignation(Token identifierToken, Expression expression) {
        Identifier identifier = new Identifier(identifierToken.associatedString());
        return new Assignation(identifier, expression);
    }
}
