package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import functional.Try;

import java.util.LinkedList;
import java.util.List;

import static org.example.lexer.token.NativeTokenTypes.*;
import static org.example.nodeconstructors.NodeResponse.*;

public class CallExpressionNodeConstructor implements NodeConstructor {

	//whether it has to check ";" --> Refactor

	private final boolean terminal;
	private final NodeConstructor expConst;
	private final List<TokenType> nativeFunctions;

	public CallExpressionNodeConstructor(boolean terminal,
										NodeConstructor expConst,
										List<TokenType> nativeFunctions){
		this.terminal = terminal;
		this.expConst = expConst;
		this.nativeFunctions = nativeFunctions;
	}



	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {

		if (isNotCallExpression(tokenBuffer)){
			return emptyResponse(tokenBuffer);
		}

		Token identifier = tokenBuffer.getToken().getSuccess().get();
		Token possibleParenthesis = tokenBuffer.getToken().getSuccess().get();

		return handleCallExpression(identifier, possibleParenthesis, tokenBuffer);
	}

	private boolean isNotCallExpression(TokenBuffer tokenBuffer) {
		boolean startsWithIdOrFunName = tokenBuffer.peekTokenType(IDENTIFIER)
				|| tokenBuffer.peekTokenType(nativeFunctions);
		return !(startsWithIdOrFunName
				&& tokenBuffer.lookaheadType(1, LEFT_PARENTHESIS));
	}

	private NodeResponse handleCallExpression(Token identifier, Token leftPar, TokenBuffer tokenBuffer) {
		// ( --> +1 , ) --> -1
		int parenthesisCount = 1;
		Token lastToken = leftPar;

		List<Expression> listOfArguments = new LinkedList<>();
		List<Token> tokenAcc = new LinkedList<>();
		String errorParsingExpression = "not part or the desired expression as argument";
		String expressionError = "not an expression";
		while (parenthesisCount > 0) {

			if (!tokenBuffer.hasAnyTokensLeft()) {
				return response(new SemanticErrorException(lastToken, "expecting function closure"),
						tokenBuffer);
			}

			if (tokenBuffer.peekTokenType(LEFT_PARENTHESIS)) {
				parenthesisCount++;
				lastToken = tokenBuffer.getToken().getSuccess().get();
				tokenAcc.add(lastToken);
			}
			else if (tokenBuffer.peekTokenType(RIGHT_PARENTHESES)) {
				parenthesisCount--;
				lastToken = tokenBuffer.getToken().getSuccess().get();
				boolean partOfInsideExpression = parenthesisCount != 0;
				if (partOfInsideExpression) {
					tokenAcc.add(lastToken);
				}
			}
			else if (tokenBuffer.peekTokenType(COMMA)) {

				String message = "expected expression after comma";
				SemanticErrorException error = new SemanticErrorException(lastToken, message);
				if (tokenAcc.isEmpty()) {
					return response(error, tokenBuffer);
				}
				lastToken = tokenBuffer.consumeToken(COMMA).getSuccess().get();

				if (tokenBuffer.peekTokenType(RIGHT_PARENTHESES)) {
					return response(error, tokenBuffer);
				}

				NodeResponse build = buildExpression(tokenBuffer,
                        tokenAcc, errorParsingExpression, expressionError, listOfArguments);
				if (build != null) return build;
			}
			else {
				Try<Token> token = tokenBuffer.getToken();

				if (token.isFail()) {
					return response(token.getFail().get(), tokenBuffer);
				}

				lastToken = token.getSuccess().get();
				tokenAcc.add(lastToken);
			}
		}

		// f(a,b) --> build the b
		if (!tokenAcc.isEmpty()) {
			NodeResponse build = buildExpression(tokenBuffer, tokenAcc,
                    errorParsingExpression, expressionError, listOfArguments);
			if (build != null) return build;
		}

		if (terminal) {
			Try<Token> tokenTry = tokenBuffer.consumeToken(SEMICOLON);

			if (tokenTry.isFail()) {
				return response(tokenTry.getFail().get(), tokenBuffer);
			}
		}

		return response(createMethodNode(identifier, listOfArguments), tokenBuffer);
	}

	private NodeResponse buildExpression(TokenBuffer tokenBuffer,
                                         List<Token> tokenAcc,
                                         String message1,
                                         String mess,
                                         List<Expression> listOfArguments) {
		Accumulator accumulator = new Accumulator(tokenAcc);
		NodeResponse build = expConst.build(new TokenBuffer(accumulator));

		if (build.possibleNode().isFail()){
			return build;
		}
		else if (build.possibleBuffer().hasAnyTokensLeft()){
			Token token = build.possibleBuffer().getToken().getSuccess().get();
			SemanticErrorException exception = new SemanticErrorException(token, message1);
			return response(exception, tokenBuffer);
		}
		else if (build.possibleNode().getSuccess().isEmpty()) {
			Token errorToken = build.possibleBuffer().getToken().getSuccess().get();
			SemanticErrorException ex = new SemanticErrorException(errorToken, mess);
			return response(ex, tokenBuffer);
		}

		listOfArguments.add( (Expression) build.possibleNode().getSuccess().get().get());
		tokenAcc.clear();
		return null;
	}

	private static Method createMethodNode(Token identifier, List<Expression> listOfArguments) {
		Identifier identifier1 = new Identifier(identifier.associatedString(), identifier.position());
		return new Method(identifier1, listOfArguments);
	}

	public List<TokenType> functions() {
		return this.nativeFunctions;
	}

	public CallExpressionNodeConstructor setExpressionParser(ExpressionNodeConstructor cons) {
		return new CallExpressionNodeConstructor(this.terminal, cons, this.nativeFunctions);
	}
}
