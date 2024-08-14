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
        if ( !(tokenBuffer.isNextTokenOfAnyOfThisTypes(operators)
                    || tokenBuffer.isNextTokenOfAnyOfThisTypes(expressions)
                        || tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType()))) {
            return new NodeConstructionResponse(new Try<>(Optional.empty()),
                                                        tokenBuffer);
        }

        return term(tokenBuffer);
    }
// + -
    private NodeConstructionResponse term(TokenBuffer tokenBuffer) {
        NodeConstructionResponse possibleExpression = factor(tokenBuffer);

        if (possibleExpression.possibleNode().isFail()) {
            return possibleExpression;
        }
// 1 + + 2
        Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

        TokenBuffer newTokenBuffer = possibleExpression.possibleBuffer();

        List<TokenType> termTokenTypes = List.of(NativeTokenTypes.PLUS.toTokenType(),
                NativeTokenTypes.MINUS.toTokenType());
        while (newTokenBuffer.isNextTokenOfAnyOfThisTypes(termTokenTypes)) {

            Token operator = newTokenBuffer.getToken().get();
            newTokenBuffer = newTokenBuffer.consumeToken();

            if (!newTokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(operator, "expected expression after operator"), newTokenBuffer);
            }

            NodeConstructionResponse possibleRightExpression = factor(newTokenBuffer);

            if (possibleRightExpression.possibleNode().isFail()) {
                return possibleExpression;
            }

            Expression rightExpression = (Expression) possibleRightExpression.possibleNode().getSuccess().get().get();

            newTokenBuffer = possibleRightExpression.possibleBuffer();

            expression = new BinaryExpression(expression, operator.associatedString(), rightExpression);
        }

        return response(expression, newTokenBuffer);
    }
// * /

    // -5
    private NodeConstructionResponse factor(TokenBuffer tokenBuffer) {
        NodeConstructionResponse possibleExpression = unary(tokenBuffer);

        if (possibleExpression.possibleNode().isFail()) {
            return possibleExpression;
        }

        Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

        TokenBuffer newTokenBuffer = possibleExpression.possibleBuffer();

        List<TokenType> termTokenTypes = List.of(NativeTokenTypes.SLASH.toTokenType(),
                NativeTokenTypes.ASTERISK.toTokenType());
        while (newTokenBuffer.isNextTokenOfAnyOfThisTypes(termTokenTypes)) {

            Token operator = newTokenBuffer.getToken().get();
            newTokenBuffer = newTokenBuffer.consumeToken();

            if (!newTokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(operator, "expected expression after operator"), newTokenBuffer);
            }

            NodeConstructionResponse possibleRightExpression = unary(newTokenBuffer);

            if (possibleRightExpression.possibleNode().isFail()) {
                return possibleExpression;
            }

            Expression rightExpression = (Expression) possibleRightExpression.possibleNode().getSuccess().get().get();

            newTokenBuffer = possibleRightExpression.possibleBuffer();

            expression = new BinaryExpression(expression, operator.associatedString(), rightExpression);
        }

        return response(expression, newTokenBuffer);
    }

    private NodeConstructionResponse unary(TokenBuffer tokenBuffer) {

        List<TokenType> termTokenTypes = List.of(NativeTokenTypes.MINUS.toTokenType());

        if (tokenBuffer.isNextTokenOfAnyOfThisTypes(termTokenTypes)) {
            Token operator = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();

            if (!tokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(operator, "expected expression after operator"), tokenBuffer);
            }

            NodeConstructionResponse possibleExpression = unary(tokenBuffer);

            if (possibleExpression.possibleNode().isFail()){
                return possibleExpression;
            }

            Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();
            tokenBuffer = possibleExpression.possibleBuffer();
            return response(new UnaryExpression(expression, operator.associatedString()), tokenBuffer);

        }

        return primary(tokenBuffer);

    }

    // number, string, identifier and left parenthesis
    private NodeConstructionResponse primary(TokenBuffer tokenBuffer) {

        if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.NUMBER.toTokenType())){

            Token number = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();
            return response(new NumericLiteral(Double.parseDouble(number.associatedString())), tokenBuffer);

        } else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.STRING.toTokenType())) {

            Token string = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();
            return response(new TextLiteral(string.associatedString().substring(1, string.length()-1)), tokenBuffer);

        } else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())) {

            Token identifier = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();
            return response(new Identifier(identifier.associatedString()), tokenBuffer); // improve to accept functions

        } else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())) {

            Token leftParToken = tokenBuffer.getToken().get();
            tokenBuffer = tokenBuffer.consumeToken();
            NodeConstructionResponse possibleExpression = term(tokenBuffer);

            if (possibleExpression.possibleNode().isFail()){
                return possibleExpression;
            }
            tokenBuffer = possibleExpression.possibleBuffer();
            Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

            if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
                tokenBuffer = tokenBuffer.consumeToken();
            }
            else {
                return response(new SemanticErrorException(leftParToken,
                        "expecting closing of this parenthesis"), tokenBuffer);
            }

            return response(new Parenthesis(expression), tokenBuffer);
        }

        return response(new SemanticErrorException(tokenBuffer.getToken().get(),
                "expecting valid expression"), tokenBuffer);

    }
}

