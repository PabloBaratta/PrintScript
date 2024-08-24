package org.example.nodeconstructors;


import org.example.*;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.nodeconstructors.NodeResponse.response;

//TODO refactor
public class AssignationNodeConstructor implements NodeConstructor{

	private final NodeConstructor expressionNodeConstructor;

	public AssignationNodeConstructor(NodeConstructor expressionNodeConstructor
	) {
		this.expressionNodeConstructor = expressionNodeConstructor;
	}
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {
		boolean hasIdentifier = tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType());

		Token identifierToken = tokenBuffer.getToken().get();
		TokenBuffer tokenBufferWithoutIdentifier = tokenBuffer.consumeToken();

		NodeResponse tokenBufferWithoutIdentifier1 = hasEquals(hasIdentifier, tokenBufferWithoutIdentifier);
		if (tokenBufferWithoutIdentifier1 != null) return tokenBufferWithoutIdentifier1;


		Token equals = tokenBufferWithoutIdentifier.getToken().get();
		TokenBuffer tokenBufferWithoutEquals = tokenBufferWithoutIdentifier.consumeToken();

		NodeResponse equals1 = isEnding(tokenBufferWithoutEquals, equals);
		if (equals1 != null) return equals1;

		return handleEqualsToken(identifierToken, equals, tokenBufferWithoutEquals);
	}

	private static NodeResponse isEnding(TokenBuffer tokenBufferWithoutEquals, Token equals) {
		if (!tokenBufferWithoutEquals.hasAnyTokensLeft()) {
			return response(new SemanticErrorException(equals, "was expecting closing after"),
					tokenBufferWithoutEquals);
		}
		return null;
	}

	private static NodeResponse hasEquals(boolean hasIdentifier, TokenBuffer tokenBufferWithoutIdentifier) {
		if (!(hasIdentifier &&
			tokenBufferWithoutIdentifier.hasAnyTokensLeft() &&
			tokenBufferWithoutIdentifier.isNextTokenOfType(NativeTokenTypes.EQUALS.toTokenType()))) {
			return new NodeResponse(new Try<>(Optional.empty()), tokenBufferWithoutIdentifier);
		}
		return null;
	}

	private NodeResponse handleEqualsToken(Token identifierToken, Token equalsToken, TokenBuffer tokenBuffer) {
		List<Token> tokens = new LinkedList<>();

		Token currentToken = equalsToken;
		while (!tokenBuffer.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

			NodeResponse currentToken1 = isEnding(tokenBuffer, currentToken);
			if (currentToken1 != null) return currentToken1;

			currentToken = tokenBuffer.getToken().get();
			tokens.add(currentToken);
			tokenBuffer = tokenBuffer.consumeToken();
		}

		boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();

		NodeResponse equalsToken1 = isAssignation(equalsToken, tokenBuffer, noTokensBetweenEqualsAndSemiColon);
		if (equalsToken1 != null) return equalsToken1;

		TokenBuffer expressionTokenBuffer = new TokenBuffer(tokens);

		NodeResponse buildResult = expressionNodeConstructor.build(expressionTokenBuffer);

		if (buildResult.possibleNode().isFail()) {
			return buildResult;
		}

		ASTNode astNode = buildResult.possibleNode().getSuccess().get().get();

		return response(getAssignation(identifierToken, (Expression) astNode), tokenBuffer.consumeToken());
	}

	private static NodeResponse isAssignation(Token equalsToken, TokenBuffer tokenBuffer, boolean noTokens) {
		if (noTokens){
			return response(new SemanticErrorException(equalsToken, "was expecting assignation"),
					tokenBuffer);
		}
		return null;
	}

	private static Assignation getAssignation(Token identifierToken, Expression expression) {
		Identifier identifier = new Identifier(identifierToken.associatedString(), identifierToken.position());
		return new Assignation(identifier, expression, identifier.getPosition());
	}


}
