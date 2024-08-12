package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExpressionNodeConstructor implements NodeConstructor {

    private final List<TokenType> operators;
    private final List<TokenType> expressions;

    public ExpressionNodeConstructor(List<TokenType> operators, List<TokenType> expressions) {
        this.operators = operators;
        this.expressions = expressions;
    }

    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {
        if (!tokenBuffer.hasAnyTokensLeft()) {
            return new NodeConstructionResponse(new Try<>(Optional.empty()), tokenBuffer);
        }
        Token currentToken = tokenBuffer.getToken().get();
        tokenBuffer = tokenBuffer.consumeToken();
        TokenType tokenType = currentToken.type();

        if (tokenBuffer.isNextTokenOfAnyOfThisTypes(operators)){
            return buildExpression(tokenBuffer, currentToken);
        } else if (expressions.contains(tokenType)) {
            return new NodeConstructionResponse(new Try<>(Optional.of(createExpressionFromToken(currentToken))), tokenBuffer);
        } else {
            return new NodeConstructionResponse(new Try<>(Optional.empty()), tokenBuffer);
        }
    }

    private NodeConstructionResponse buildExpression(TokenBuffer tokenBuffer, Token leftToken) {
        Expression leftExpression = createExpressionFromToken(leftToken);

        while (tokenBuffer.hasAnyTokensLeft() && isOperator(tokenBuffer.getToken().get().type())) {
            Token operatorToken = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();

            if (!tokenBuffer.hasAnyTokensLeft()) {
                throw new IllegalStateException("Expected expression after operator");
            }

            Token rightToken = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();
            Expression rightExpression = createExpressionFromToken(rightToken);

            leftExpression = new BinaryExpression(leftExpression, operatorToken.associatedString(), rightExpression);
        }

        return new NodeConstructionResponse(new Try<>(Optional.of(leftExpression)), tokenBuffer);
    }

    private boolean isOperator(TokenType tokenType) {
        return operators.contains(tokenType);
    }

    private Expression createExpressionFromToken(Token token) {
        TokenType tokenType = token.type();
        if (Objects.equals(tokenType, NativeTokenTypes.IDENTIFIER.toTokenType())) {
            return new Identifier(token.associatedString());
        } else if (Objects.equals(tokenType, NativeTokenTypes.NUMBER.toTokenType())) {
            return new NumericLiteral(Double.parseDouble(token.associatedString()));
        } else if (Objects.equals(tokenType, NativeTokenTypes.STRING.toTokenType())) {
            return new TextLiteral(token.associatedString());
        } else {
            throw new IllegalArgumentException("Unexpected token type: " + tokenType);
        }
    }
}
