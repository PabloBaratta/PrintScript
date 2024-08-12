package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.SemanticErrorException;
import org.example.NativeTokenTypes;
import org.example.VariableDeclaration;
import org.example.Type;
import org.example.Identifier;
import org.example.Expression;
import org.example.TokenType;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class VariableDeclarationNodeConstructor implements NodeConstructor {

    private final ExpressionNodeConstructor expressionNodeConstructor;
    private final List<TokenType> declarationTypes;
    private final List<TokenType> literalTypes;


    VariableDeclarationNodeConstructor(ExpressionNodeConstructor expressionNodeConstructor,
                                       List<TokenType> declarationTypes,
                                       List<TokenType> literalType
    ) {
        this.expressionNodeConstructor = expressionNodeConstructor;
        this.declarationTypes = declarationTypes;
        this.literalTypes = literalType;
    }

    //TODO refactor
    @Override
    public Try<Optional<ASTNode>, Exception> build(Token token, Queue<Token> tokenBuffer) {

        if (!NodeConstructor.isThisTokenType(token, declarationTypes)) {
            return new Try<>(Optional.empty());
        }

        Token identifierToken = tokenBuffer.poll();
        if (!isValidToken(identifierToken, NativeTokenTypes.IDENTIFIER)) {
            return new Try<>(new SemanticErrorException(identifierToken, "was expecting identifier"));
        }

        Token typeAssignationOperator = tokenBuffer.poll();
        if (!isValidToken(typeAssignationOperator, NativeTokenTypes.COLON)) {
            return new Try<>(new SemanticErrorException(typeAssignationOperator, "was expecting type assignation operation"));
        }

        Token type = tokenBuffer.poll();
        if (!isValidToken(type, literalTypes)) {
            return new Try<>(new SemanticErrorException(type, "was expecting a valid type"));
        }

        Token proxUnknownToken = tokenBuffer.poll();
        if (proxUnknownToken == null) {
            return new Try<>(new Exception("was expecting closing after " + type.associatedString() + " in character " + type.position().getOffset()));
        }

        if (isTokenType(proxUnknownToken, NativeTokenTypes.SEMICOLON)) {
            return new Try<>(Optional.of(getVariableDeclaration(identifierToken, type, Optional.empty())));
        }

        if (isTokenType(proxUnknownToken, NativeTokenTypes.EQUALS)) {
            return handleEqualsToken(identifierToken, type, proxUnknownToken, tokenBuffer);
        }

        return new Try<>(new SemanticErrorException(type, "was expecting variable declaration or assignation"));
    }

    private boolean isValidToken(Token token, NativeTokenTypes expectedType) {
        return token != null && NodeConstructor.getType(token).equals(expectedType.toTokenType());
    }

    private boolean isValidToken(Token token, List<TokenType> expectedTypes) {
        return token != null && NodeConstructor.isThisTokenType(token, expectedTypes);
    }

    private boolean isTokenType(Token token, NativeTokenTypes expectedType) {
        return NodeConstructor.getType(token).equals(expectedType.toTokenType());
    }

    private Try<Optional<ASTNode>, Exception> handleEqualsToken(Token identifierToken, Token type, Token equalsToken, Queue<Token> tokenBuffer) {
        Queue<Token> tokens = new LinkedList<>();
        Token proxUnknownToken = equalsToken;

        while (!isTokenType(proxUnknownToken, NativeTokenTypes.SEMICOLON)) {
            tokens.add(proxUnknownToken);
            proxUnknownToken = tokenBuffer.poll();
            if (proxUnknownToken == null) {
                return new Try<>(new SemanticErrorException(equalsToken, "was expecting closing after"));
            }
        }

        Try<Optional<ASTNode>, Exception> buildResult = expressionNodeConstructor.build(tokens.poll(), tokens);
        if (buildResult.isFail()) {
            return buildResult;
        }

        return new Try<>(Optional.of(getVariableDeclaration(identifierToken, type, Optional.of((Expression) buildResult.getSuccess().get().get()))));
    }

    private static VariableDeclaration getVariableDeclaration(Token identifierToken, Token typeToken, Optional<Expression> optionalExpression) {
        Identifier identifier = new Identifier(identifierToken.associatedString());
        Type type = new Type(typeToken.associatedString());
        return new VariableDeclaration(identifier, type, optionalExpression);
    }
}