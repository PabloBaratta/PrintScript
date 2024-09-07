package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;

import java.util.LinkedList;
import java.util.List;

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
	public NodeResponse build(TokenBuffer tokenBuffer) throws Exception {

		if (!(tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())
			|| tokenBuffer.isNextTokenOfAnyOfThisTypes(nativeFunctions))){
			return emptyResponse(tokenBuffer);
		}

		Token identifier = tokenBuffer.getToken().get();
		Token possibleParenthesis = tokenBuffer.peekNext();

		if (!TokenBuffer.isThisTokenType(possibleParenthesis, NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())){
			return emptyResponse(tokenBuffer);
		}
		//consume id
		tokenBuffer.consumeToken();
		//consum (
		TokenBuffer tokenBufferWithoutLeft = tokenBuffer.consumeToken();
		return handleCallExpression(identifier, tokenBufferWithoutLeft);
	}

	//TODO refactor
	private NodeResponse handleCallExpression(Token identifier, TokenBuffer tokenBuffer) throws Exception {
		// ( --> +1 , ) --> -1
		int parenthesisCount = 1;
		Token lastToken = identifier; // it should be the left parenthesis

		List<Expression> listOfArguments = new LinkedList<>();
		List<Token> tokenAcc = new LinkedList<>();
		String message1 = "not part or the desired expression as argument";
		String mess = "not an expression";
		while (parenthesisCount > 0) {

			if (!tokenBuffer.hasAnyTokensLeft()) {
				return response(new SemanticErrorException(lastToken, "expecting function closure"),
						tokenBuffer);
			}

			if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())) {
				parenthesisCount++;
				lastToken = tokenBuffer.getToken().get();
				tokenAcc.add(lastToken);
				tokenBuffer = tokenBuffer.consumeToken(); //continue

			}
			else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
				parenthesisCount--;
				lastToken = tokenBuffer.getToken().get();
				boolean partOfInsideExpression = parenthesisCount != 0;
				if (partOfInsideExpression) {
					tokenAcc.add(lastToken);
				}
				tokenBuffer = tokenBuffer.consumeToken(); //continue
			}
			else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.COMMA.toTokenType())) {

				String message = "expected expression after comma";
				SemanticErrorException error = new SemanticErrorException(lastToken, message);
				if (tokenAcc.isEmpty()) {
					return response(error,
							tokenBuffer);
				}
				lastToken = tokenBuffer.getToken().get(); //check
				tokenBuffer = tokenBuffer.consumeToken(); //continue

				if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
					return response(error,
							tokenBuffer);
				}
				Accumulator accumulator = new Accumulator(tokenAcc);
				NodeResponse build = expConst.build(new TokenBuffer(accumulator));

				if (build.possibleNode().isFail()){
					return build;
				}
				else if (build.possibleBuffer().hasAnyTokensLeft()){
					Token token = build.possibleBuffer().getToken().get();
					SemanticErrorException exception = new SemanticErrorException(token, message1);
					return response(exception, tokenBuffer);
				}
				else if (build.possibleNode().getSuccess().isEmpty()) {
					Token errorToken = build.possibleBuffer().getToken().get();
					SemanticErrorException ex = new SemanticErrorException(errorToken, mess);
					return response(ex, tokenBuffer);
				}

				listOfArguments.add( (Expression) build.possibleNode().getSuccess().get().get());
				tokenAcc.clear();
			}
			else {
				lastToken = tokenBuffer.getToken().get();
				tokenBuffer = tokenBuffer.consumeToken();
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
				Token errorToken = build.possibleBuffer().getToken().get();
				return response(new SemanticErrorException(errorToken, message1), tokenBuffer);
			}
			else if (build.possibleNode().getSuccess().isEmpty()) {
				Token errorToken = build.possibleBuffer().getToken().get();
				return response(new SemanticErrorException(errorToken, mess), tokenBuffer);
			}

			listOfArguments.add( (Expression) build.possibleNode().getSuccess().get().get());
			tokenAcc.clear();
		}

		if (terminal) {
			if (!tokenBuffer.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())) {
				return response(new SemanticErrorException(lastToken, "expected end of statement"),
						tokenBuffer);
			}
			tokenBuffer = tokenBuffer.consumeToken();
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
