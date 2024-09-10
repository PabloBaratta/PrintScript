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

	//TODO refactor
	private NodeResponse handleCallExpression(Token identifier, Token leftPar, TokenBuffer tokenBuffer) {
		// ( --> +1 , ) --> -1
		int parenthesisCount = 1;
		Token lastToken = leftPar;

		List<Expression> listOfArguments = new LinkedList<>();
		List<Token> tokenAcc = new LinkedList<>();
		String message1 = "not part or the desired expression as argument";
		String mess = "not an expression";
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
			Accumulator accumulator = new Accumulator(tokenAcc);
			NodeResponse build = expConst.build(new TokenBuffer(accumulator));

			if (build.possibleNode().isFail()){
				return build;
			}
			else if (build.possibleBuffer().hasAnyTokensLeft()){
				//safe because is a buffer with already proven right nodes
				Token errorToken = build.possibleBuffer().getToken().getSuccess().get();
				return response(new SemanticErrorException(errorToken, message1), tokenBuffer);
			}
			else if (build.possibleNode().getSuccess().isEmpty()) {
				Token errorToken = build.possibleBuffer().getToken().getSuccess().get();
				return response(new SemanticErrorException(errorToken, mess), tokenBuffer);
			}

			listOfArguments.add( (Expression) build.possibleNode().getSuccess().get().get());
			tokenAcc.clear();
		}

		if (terminal) {
			Try<Token> tokenTry = tokenBuffer.consumeToken(SEMICOLON);

			if (tokenTry.isFail()) {
				return response(tokenTry.getFail().get(), tokenBuffer);
			}
		}

		return response(createMethodNode(identifier, listOfArguments), tokenBuffer);
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
