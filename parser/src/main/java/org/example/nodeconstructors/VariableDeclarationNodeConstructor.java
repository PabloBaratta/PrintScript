package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.SemanticErrorException;
import org.example.NativeTokenTypes;
import org.example.VariableDeclaration;
import org.example.Type;
import org.example.Identifier;
import org.example.Expression;
import org.example.Token;
import org.example.TokenType;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class VariableDeclarationNodeConstructor implements NodeConstructor {

    private final ExpressionNodeConstructor expressionNodeConstructor;
    private final List<TokenType> variableDeclarationTokenTypes;
    private final List<TokenType> literalTypes;


    VariableDeclarationNodeConstructor(ExpressionNodeConstructor expressionNodeConstructor,
                                       List<TokenType> variableDeclarationTokenTypes,
                                       List<TokenType> literalType
    ) {
        this.expressionNodeConstructor = expressionNodeConstructor;
        this.variableDeclarationTokenTypes = variableDeclarationTokenTypes;
        this.literalTypes = literalType;
    }

    //TODO refactor
    @Override
    public Try<Optional<ASTNode>, Exception> build(Token token, Queue<Token> tokenBuffer) {

        if (!NodeConstructor.isThisTokenType(token, variableDeclarationTokenTypes)) {
            return new Try<>(Optional.empty());
        }

        Token identifierToken = tokenBuffer.poll();
        if (!NodeConstructor.isTokenType(identifierToken, NativeTokenTypes.IDENTIFIER.toTokenType())) {
            return new Try<>(new SemanticErrorException(identifierToken, "was expecting identifier"));
        }

        Token typeAssignationOperator = tokenBuffer.poll();
        if (!NodeConstructor.isTokenType(typeAssignationOperator, NativeTokenTypes.COLON.toTokenType())) {
            return new Try<>(new SemanticErrorException(typeAssignationOperator, "was expecting type assignation operation"));
        }

        Token type = tokenBuffer.poll();
        if (!NodeConstructor.isThisTokenType(type, literalTypes)) {
            return new Try<>(new SemanticErrorException(type, "was expecting a valid type"));
        }

        Token proxUnknownToken = tokenBuffer.poll();
        if (proxUnknownToken == null) {
            return new Try<>(new Exception("was expecting closing after " + type.associatedString() + " in character " + type.offset()));
        }

        if (NodeConstructor.isTokenType(proxUnknownToken, NativeTokenTypes.SEMICOLON.toTokenType())) {
            return new Try<>(Optional.of(getVariableDeclaration(identifierToken, type, Optional.empty())));
        }

        if (NodeConstructor.isTokenType(proxUnknownToken, NativeTokenTypes.EQUALS.toTokenType())) {
            return handleEqualsToken(identifierToken, type, proxUnknownToken, tokenBuffer);
        }

        return new Try<>(new SemanticErrorException(type, "was expecting variable declaration or assignation"));
    }


    private Try<Optional<ASTNode>, Exception> handleEqualsToken(Token identifierToken, Token type, Token equalsToken, Queue<Token> tokenBuffer) {
        Queue<Token> tokens = new LinkedList<>();
        Token proxUnknownToken = equalsToken;

        while (!NodeConstructor.isTokenType(proxUnknownToken, NativeTokenTypes.SEMICOLON.toTokenType())) {
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