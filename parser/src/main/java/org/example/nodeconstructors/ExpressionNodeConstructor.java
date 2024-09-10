package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import functional.Try;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.lexer.token.NativeTokenTypes.*;
import static org.example.nodeconstructors.NodeResponse.emptyResponse;
import static org.example.nodeconstructors.NodeResponse.response;

public class ExpressionNodeConstructor implements NodeConstructor {

	private final List<TokenType> operators;
	private final List<TokenType> expressions;
	private final List<Function<TokenBuffer, NodeResponse>> functions;
	private final CallExpressionNodeConstructor callExpressionConstructor;

	public ExpressionNodeConstructor(Map<TokenType, Integer> mapOperatorsToPrecedence, List<TokenType> operands,
									CallExpressionNodeConstructor callConstructor) {
		this.operators = mapOperatorsToPrecedence.keySet().stream().toList();
		this.expressions = operands;
		this.functions = getFunctions(mapOperatorsToPrecedence);
		this.callExpressionConstructor = callConstructor.setExpressionParser(this);
	}

	private List<Function<TokenBuffer, NodeResponse>> getFunctions(Map<TokenType, Integer> opsPrecedence){
		Map<Integer, List<TokenType>> groupedByPrecedence = opsPrecedence.entrySet().stream()
				.collect(Collectors.groupingBy(
						Map.Entry::getValue,
						() -> new TreeMap<>(Collections.reverseOrder()),
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())
				));
		int functionsSize = groupedByPrecedence.size();
		List<Function<TokenBuffer, NodeResponse>> functions = new ArrayList<>(functionsSize);

		Function<TokenBuffer, NodeResponse> func = this::unary;
		Collection<List<TokenType>> values = groupedByPrecedence.values();
		for (List<TokenType> value : values) {
			Function<TokenBuffer, NodeResponse> finalFunc = func; //cannot pass a global variable
			functions.addLast((tokenBuffer -> parseBE(tokenBuffer, finalFunc, value)));
			func = functions.getLast();
		}
		return functions;
	}


	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {

		// statement should start with an opening parenthesis, operator or a single expression
		if (!isThisExpression(tokenBuffer)) {
			return emptyResponse(tokenBuffer);
		}
		return getLeastPrecedenceFun().apply(tokenBuffer);
	}

	private boolean isThisExpression(TokenBuffer tokenBuffer) {
		return tokenBuffer.peekTokenType(operators)
				|| tokenBuffer.peekTokenType(expressions)
				|| tokenBuffer.peekTokenType(LEFT_PARENTHESIS)
				|| tokenBuffer.peekTokenType(callExpressionConstructor.functions());
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

		while (newTokenBuffer.peekTokenType(operatorTypes)) {
			Token operator = newTokenBuffer.getToken().getSuccess().get();

			if (!newTokenBuffer.hasAnyTokensLeft()) {
				String message = "expected expression after operator";
				SemanticErrorException exception = new SemanticErrorException(operator, message);
				return response(exception, newTokenBuffer);
			}

			NodeResponse possibleRightExpression = hpp.apply(newTokenBuffer);
			Try<Optional<ASTNode>> aTry = possibleRightExpression.possibleNode();
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
		Token operator = tokenBuffer.getToken().getSuccess().get();

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
									BiFunction<String, Position, Expression> ec)
									{
		Token token = tb.getToken().getSuccess().get();
		return response(ec.apply(token.associatedString(), token.position()), tb);
	}

	private NodeResponse parseParenthesisExpression(TokenBuffer tokenBuffer) {
		Token leftParToken = tokenBuffer.getToken().getSuccess().get();

		Function<TokenBuffer, NodeResponse> fun = getLeastPrecedenceFun();
		NodeResponse possibleExpression = fun.apply(tokenBuffer);

		if (possibleExpression.possibleNode().isFail()) {
			return possibleExpression;
		}

		tokenBuffer = possibleExpression.possibleBuffer();
		Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

		Try<Token> tokenTry = tokenBuffer.consumeToken(RIGHT_PARENTHESES);

		if (tokenTry.isFail()) {
			return response(tokenTry.getFail().get(), tokenBuffer);
		}

		return response(new Parenthesis(expression), tokenBuffer);
	}

	private Function<TokenBuffer, NodeResponse> getLeastPrecedenceFun() {
		return this.functions.getLast();
	}

	private NodeResponse unary(TokenBuffer tokenBuffer) {
		if (tokenBuffer.peekTokenType(MINUS)) {
			return parseUnaryExpression(tokenBuffer);
		}
		return primary(tokenBuffer);
	}

	// number, string, identifier and left parenthesis
	private NodeResponse primary(TokenBuffer tokenBuffer)  {
		if (tokenBuffer.peekTokenType(NUMBER)) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				NumericLiteral numericLiteral = new NumericLiteral(Double.parseDouble(s), position);
				return numericLiteral;
			});
		} else if (tokenBuffer.peekTokenType(STRING)) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				TextLiteral textLiteral = new TextLiteral(s.substring(1, s.length() - 1), position);
				return textLiteral;
			});
		} else if (tokenBuffer.peekTokenType(BOOLEAN)) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				BooleanLiteral booleanLiteral = new BooleanLiteral(Boolean.parseBoolean(s), position);
				return booleanLiteral;
			});
		} else if (tokenBuffer.peekTokenType(IDENTIFIER)) {

			if (tokenBuffer.lookaheadType(1, LEFT_PARENTHESIS)) {
				return callExpressionConstructor.build(tokenBuffer);
			}

			return parseLiteral(tokenBuffer, Identifier::new);
		} else if (tokenBuffer.peekTokenType(callExpressionConstructor.functions())) {
			return callExpressionConstructor.build(tokenBuffer);
		} else if (tokenBuffer.peekTokenType(LEFT_PARENTHESIS)) {
			return parseParenthesisExpression(tokenBuffer);
		}
		return response(new SemanticErrorException(tokenBuffer.getToken().getSuccess().get(),
				"expecting valid expression"), tokenBuffer);
	}
}
