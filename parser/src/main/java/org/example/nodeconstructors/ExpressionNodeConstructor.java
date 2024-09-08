package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;

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
	private final List<ThrowingFunction<TokenBuffer, NodeResponse>> functions;
	private final CallExpressionNodeConstructor callExpressionConstructor;

	public ExpressionNodeConstructor(Map<TokenType, Integer> mapOperatorsToPrecedence, List<TokenType> operands,
									CallExpressionNodeConstructor callConstructor) {
		this.operators = mapOperatorsToPrecedence.keySet().stream().toList();
		this.expressions = operands;
		this.functions = getFunctions(mapOperatorsToPrecedence);
		this.callExpressionConstructor = callConstructor.setExpressionParser(this);
	}

	private List<ThrowingFunction<TokenBuffer, NodeResponse>> getFunctions(Map<TokenType, Integer> opsPrecedence){
		Map<Integer, List<TokenType>> groupedByPrecedence = opsPrecedence.entrySet().stream()
				.collect(Collectors.groupingBy(
						Map.Entry::getValue,
						() -> new TreeMap<>(Collections.reverseOrder()),
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())
				));
		int functionsSize = groupedByPrecedence.size();
		List<ThrowingFunction<TokenBuffer, NodeResponse>> functions = new ArrayList<>(functionsSize);

		ThrowingFunction<TokenBuffer, NodeResponse> func = this::unary;
		Collection<List<TokenType>> values = groupedByPrecedence.values();
		for (List<TokenType> value : values) {
			ThrowingFunction<TokenBuffer, NodeResponse> finalFunc = func; //cannot pass a global variable
			functions.addLast((tokenBuffer -> parseBE(tokenBuffer, finalFunc, value)));
			func = functions.getLast();
		}
		return functions;
	}


	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) throws Exception {

		// statement should start with an opening parenthesis, operator or a single expression
		if (!isThisExpression(tokenBuffer)) {
			return emptyResponse(tokenBuffer);
		}
		return getLeastPrecedenceFun().apply(tokenBuffer);
	}

	private boolean isThisExpression(TokenBuffer tokenBuffer) {
		return tokenBuffer.isNextTokenOfAnyOfThisTypes(operators)
				|| tokenBuffer.isNextTokenOfAnyOfThisTypes(expressions)
				|| tokenBuffer.isNextTokenOfType(LEFT_PARENTHESIS.toTokenType())
				|| tokenBuffer.isNextTokenOfAnyOfThisTypes(callExpressionConstructor.functions());
	}

	private NodeResponse parseBE(TokenBuffer tb,
								ThrowingFunction<TokenBuffer, NodeResponse> hpp,
								List<TokenType> operatorTypes) throws Exception {
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
	private NodeResponse parseUnaryExpression(TokenBuffer tokenBuffer) throws Exception {
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
									BiFunction<String, Position, Expression> ec)
									throws Exception {
		Token token = tb.getToken().get();
		tb = tb.consumeToken();
		return response(ec.apply(token.associatedString(), token.position()), tb);
	}

	private NodeResponse parseParenthesisExpression(TokenBuffer tokenBuffer) throws Exception {
		Token leftParToken = tokenBuffer.getToken().get();
		tokenBuffer = tokenBuffer.consumeToken();

		ThrowingFunction<TokenBuffer, NodeResponse> fun = getLeastPrecedenceFun();
		NodeResponse possibleExpression = fun.apply(tokenBuffer);

		if (possibleExpression.possibleNode().isFail()) {
			return possibleExpression;
		}

		tokenBuffer = possibleExpression.possibleBuffer();
		Expression expression = (Expression) possibleExpression.possibleNode().getSuccess().get().get();

		if (tokenBuffer.isNextTokenOfType(RIGHT_PARENTHESES.toTokenType())) {
			tokenBuffer = tokenBuffer.consumeToken();
		} else {
			String message = "expecting closing of this parenthesis";
			SemanticErrorException exception = new SemanticErrorException(leftParToken, message);
			return response(exception, tokenBuffer);
		}

		return response(new Parenthesis(expression), tokenBuffer);
	}

	private ThrowingFunction<TokenBuffer, NodeResponse> getLeastPrecedenceFun() {
		return this.functions.getLast();
	}

	private NodeResponse unary(TokenBuffer tokenBuffer) throws Exception {
		if (tokenBuffer.isNextTokenOfType(MINUS.toTokenType())) {
			return parseUnaryExpression(tokenBuffer);
		}
		return primary(tokenBuffer);
	}

	// number, string, identifier and left parenthesis
	private NodeResponse primary(TokenBuffer tokenBuffer) throws Exception {
		if (tokenBuffer.isNextTokenOfType(NUMBER.toTokenType())) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				NumericLiteral numericLiteral = new NumericLiteral(Double.parseDouble(s), position);
				return numericLiteral;
			});
		} else if (tokenBuffer.isNextTokenOfType(STRING.toTokenType())) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				TextLiteral textLiteral = new TextLiteral(s.substring(1, s.length() - 1), position);
				return textLiteral;
			});
		} else if (tokenBuffer.isNextTokenOfType(BOOLEAN.toTokenType())) {
			return parseLiteral(tokenBuffer, (s, position) -> {
				BooleanLiteral booleanLiteral = new BooleanLiteral(Boolean.parseBoolean(s), position);
				return booleanLiteral;
			});
		} else if (tokenBuffer.isNextTokenOfType(IDENTIFIER.toTokenType())) {


			Token peekParenthesis = tokenBuffer.peekNext();

			if (TokenBuffer.isThisTokenType(peekParenthesis, LEFT_PARENTHESIS.toTokenType())) {
				return callExpressionConstructor.build(tokenBuffer);
			}

			return parseLiteral(tokenBuffer, Identifier::new);
		} else if (tokenBuffer.isNextTokenOfAnyOfThisTypes(callExpressionConstructor.functions())) {
			return callExpressionConstructor.build(tokenBuffer);
		} else if (tokenBuffer.isNextTokenOfType(LEFT_PARENTHESIS.toTokenType())) {
			return parseParenthesisExpression(tokenBuffer);
		}
		return response(new SemanticErrorException(tokenBuffer.getToken().get(),
				"expecting valid expression"), tokenBuffer);
	}
}
