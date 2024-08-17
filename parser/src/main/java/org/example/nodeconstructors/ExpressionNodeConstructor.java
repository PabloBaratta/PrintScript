package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.example.nodeconstructors.NodeConstructionResponse.emptyResponse;
import static org.example.nodeconstructors.NodeConstructionResponse.response;

public class ExpressionNodeConstructor implements NodeConstructor {

    private final List<TokenType> operators;
    private final List<TokenType> expressions;

    public ExpressionNodeConstructor(List<TokenType> operators, List<TokenType> expressions) {
        this.operators = operators;
        this.expressions = expressions;
    }


    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {

        // statement should start with an opening parenthesis, operator or a single expression
        if (!isThisExpression(tokenBuffer)) {
            return emptyResponse(tokenBuffer);
        }
        return term(tokenBuffer);
    }

    private boolean isThisExpression(TokenBuffer tokenBuffer) {
        return tokenBuffer.isNextTokenOfAnyOfThisTypes(operators)
                || tokenBuffer.isNextTokenOfAnyOfThisTypes(expressions)
                || tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType());
    }

    private NodeConstructionResponse parseBinaryExpression(TokenBuffer tokenBuffer,
                                                           Function<TokenBuffer, NodeConstructionResponse> higherPrecedenceParser,
                                                           List<TokenType> operatorTypes) {
        NodeConstructionResponse possibleExpression = higherPrecedenceParser.apply(tokenBuffer);
        if (possibleExpression.possibleNode().isFail()) {
            return possibleExpression;
        }

        Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();
        TokenBuffer newTokenBuffer = possibleExpression.possibleBuffer();

        while (newTokenBuffer.isNextTokenOfAnyOfThisTypes(operatorTypes)) {
            Token operator = newTokenBuffer.getToken().get();
            newTokenBuffer = newTokenBuffer.consumeToken();

            if (!newTokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(operator, "expected expression after operator"), newTokenBuffer);
            }

            NodeConstructionResponse possibleRightExpression = higherPrecedenceParser.apply(newTokenBuffer);
            if (possibleRightExpression.possibleNode().isFail()) {
                return possibleRightExpression;
            }

            Expression rightExpression = (Expression) possibleRightExpression.possibleNode().getSuccess().get().get();
            newTokenBuffer = possibleRightExpression.possibleBuffer();
            expression = new BinaryExpression(expression, operator.associatedString(), rightExpression);
        }

        return response(expression, newTokenBuffer);
    }
    //TODO modify to accept post operators
    private NodeConstructionResponse parseUnaryExpression(TokenBuffer tokenBuffer) {
        Token operator = tokenBuffer.getToken().get();
        tokenBuffer = tokenBuffer.consumeToken();

        if (!tokenBuffer.hasAnyTokensLeft()) {
            return response(new SemanticErrorException(operator, "expected expression after operator"), tokenBuffer);
        }

        NodeConstructionResponse possibleExpression = unary(tokenBuffer);
        if (possibleExpression.possibleNode().isFail()) {
            return possibleExpression;
        }

        Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();
        tokenBuffer = possibleExpression.possibleBuffer();
        return response(new UnaryExpression(expression, operator.associatedString()), tokenBuffer);
    }

    private NodeConstructionResponse parseLiteral(TokenBuffer tokenBuffer,
                                                  Function<String, Expression> expressionConstructor) {
        Token token = tokenBuffer.getToken().get();
        tokenBuffer = tokenBuffer.consumeToken();
        return response(expressionConstructor.apply(token.associatedString()), tokenBuffer);
    }

    private NodeConstructionResponse parseParenthesisExpression(TokenBuffer tokenBuffer) {
        Token leftParToken = tokenBuffer.getToken().get();
        tokenBuffer = tokenBuffer.consumeToken();
        NodeConstructionResponse possibleExpression = term(tokenBuffer);

        if (possibleExpression.possibleNode().isFail()) {
            return possibleExpression;
        }

        tokenBuffer = possibleExpression.possibleBuffer();
        Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

        if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
            tokenBuffer = tokenBuffer.consumeToken();
        } else {
            return response(new SemanticErrorException(leftParToken, "expecting closing of this parenthesis"), tokenBuffer);
        }

        return response(new Parenthesis(expression), tokenBuffer);
    }

    private NodeConstructionResponse term(TokenBuffer tokenBuffer) {
        return parseBinaryExpression(tokenBuffer, this::factor, List.of(NativeTokenTypes.PLUS.toTokenType(),
                NativeTokenTypes.MINUS.toTokenType()));
    }

    private NodeConstructionResponse factor(TokenBuffer tokenBuffer) {
        return parseBinaryExpression(tokenBuffer, this::unary, List.of(NativeTokenTypes.SLASH.toTokenType(),
                NativeTokenTypes.ASTERISK.toTokenType()));
    }

    private NodeConstructionResponse unary(TokenBuffer tokenBuffer) {
        if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.MINUS.toTokenType())) {
            return parseUnaryExpression(tokenBuffer);
        }
        return primary(tokenBuffer);
    }

    // number, string, identifier and left parenthesis
    private NodeConstructionResponse primary(TokenBuffer tokenBuffer) {
        if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.NUMBER.toTokenType())) {
            return parseLiteral(tokenBuffer, s -> new NumericLiteral(Double.parseDouble(s)));
        } else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.STRING.toTokenType())) {
            return parseLiteral(tokenBuffer, s -> new TextLiteral(s.substring(1, s.length() - 1)));
        } else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())) {
            return parseLiteral(tokenBuffer, Identifier::new);
        } else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())) {
            return parseParenthesisExpression(tokenBuffer);
        }
        return response(new SemanticErrorException(tokenBuffer.getToken().get(),
                "expecting valid expression"), tokenBuffer);
    }
}

