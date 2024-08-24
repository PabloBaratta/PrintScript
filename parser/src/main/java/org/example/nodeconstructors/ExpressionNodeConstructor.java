package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.example.nodeconstructors.NodeResponse.emptyResponse;
import static org.example.nodeconstructors.NodeResponse.response;

public class ExpressionNodeConstructor implements NodeConstructor {

	private final List<TokenType> operators;
	private final List<TokenType> expressions;

	public ExpressionNodeConstructor(List<TokenType> operators, List<TokenType> expressions) {
		this.operators = operators;
		this.expressions = expressions;
	}


	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {

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

	private NodeResponse parseBE(TokenBuffer tb,
								Function<TokenBuffer, NodeResponse> hpp,
								List<TokenType> operatorTypes) {
		NodeResponse possibleExpression = hpp.apply(tb);
		if (possibleExpression.possibleNode().isFail()) {
			return possibleExpression;
		}

		Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();
		TokenBuffer newTokenBuffer = possibleExpression.possibleBuffer();

		while (newTokenBuffer.isNextTokenOfAnyOfThisTypes(operatorTypes)) {
			Token operator = newTokenBuffer.getToken().get();
			newTokenBuffer = newTokenBuffer.consumeToken();

			if (!newTokenBuffer.hasAnyTokensLeft()) {
				String message = "expected expression after operator";
				SemanticErrorException exception = new SemanticErrorException(operator, message);
				return response(exception, newTokenBuffer);
			}

			NodeResponse possibleRightExpression = hpp.apply(newTokenBuffer);
			Try<Optional<ASTNode>, Exception> aTry = possibleRightExpression.possibleNode();
			if (aTry.isFail()) {
				return possibleRightExpression;
			}

			Expression rightExpression = (Expression) aTry.getSuccess().get().get();
			newTokenBuffer = possibleRightExpression.possibleBuffer();
			expression = new BinaryExpression(expression, operator.associatedString(), rightExpression);
		}

		return response(expression, newTokenBuffer);
	}
	//TODO modify to accept post operators
	private NodeResponse parseUnaryExpression(TokenBuffer tokenBuffer) {
		Token operator = tokenBuffer.getToken().get();
		tokenBuffer = tokenBuffer.consumeToken();

		if (!tokenBuffer.hasAnyTokensLeft()) {
			String message = "expected expression after operator";
			SemanticErrorException exception = new SemanticErrorException(operator, message);
			return response(exception, tokenBuffer);
		}

		NodeResponse possibleExpression = unary(tokenBuffer);
		if (possibleExpression.possibleNode().isFail()) {
			return possibleExpression;
		}

		Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();
		tokenBuffer = possibleExpression.possibleBuffer();
		String operator1 = operator.associatedString();
		UnaryExpression node = new UnaryExpression(expression, operator1, operator.position());
		return response(node, tokenBuffer);
	}

	private NodeResponse parseLiteral(TokenBuffer tb,
									BiFunction<String, Position, Expression> ec) {
		Token token = tb.getToken().get();
		tb = tb.consumeToken();
		return response(ec.apply(token.associatedString(), token.position()), tb);
	}

	private NodeResponse parseParenthesisExpression(TokenBuffer tokenBuffer) {
		Token leftParToken = tokenBuffer.getToken().get();
		tokenBuffer = tokenBuffer.consumeToken();
		NodeResponse possibleExpression = term(tokenBuffer);

		if (possibleExpression.possibleNode().isFail()) {
			return possibleExpression;
		}

		tokenBuffer = possibleExpression.possibleBuffer();
		Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

		if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
			tokenBuffer = tokenBuffer.consumeToken();
		} else {
			String message = "expecting closing of this parenthesis";
			SemanticErrorException exception = new SemanticErrorException(leftParToken, message);
			return response(exception, tokenBuffer);
		}

		return response(new Parenthesis(expression), tokenBuffer);
	}

	private NodeResponse term(TokenBuffer tokenBuffer) {
		return parseBE(tokenBuffer, this::factor, List.of(NativeTokenTypes.PLUS.toTokenType(),
				NativeTokenTypes.MINUS.toTokenType()));
	}

	private NodeResponse factor(TokenBuffer tokenBuffer) {
		return parseBE(tokenBuffer, this::unary, List.of(NativeTokenTypes.SLASH.toTokenType(),
				NativeTokenTypes.ASTERISK.toTokenType()));
	}

	private NodeResponse unary(TokenBuffer tokenBuffer) {
		if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.MINUS.toTokenType())) {
			return parseUnaryExpression(tokenBuffer);
		}
		return primary(tokenBuffer);
	}

	// number, string, identifier and left parenthesis
	private NodeResponse primary(TokenBuffer tokenBuffer) {
		if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.NUMBER.toTokenType())) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				NumericLiteral numericLiteral = new NumericLiteral(Double.parseDouble(s), position);
				return numericLiteral;
			});
		} else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.STRING.toTokenType())) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				TextLiteral textLiteral = new TextLiteral(s.substring(1, s.length() - 1), position);
				return textLiteral;
			});
		} else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())) {
			return parseLiteral(tokenBuffer, Identifier::new);
		} else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())) {
			return parseParenthesisExpression(tokenBuffer);
		}
		return response(new SemanticErrorException(tokenBuffer.getToken().get(),
				"expecting valid expression"), tokenBuffer);
	}
}
