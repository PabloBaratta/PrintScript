package org.example.nodeconstructors;


import org.example.*;

import org.token.Token;
import functional.Try;
import static org.token.NativeTokenTypes.*;


import java.util.List;

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


		if (!isAssignation(tokenBuffer)) {
			return NodeResponse.emptyResponse(tokenBuffer);
		}

		Token identifierToken = tokenBuffer.getToken().getSuccess().get();
		Token equals = tokenBuffer.getToken().getSuccess().get();

		return handleEqualsToken(identifierToken, equals, tokenBuffer);
	}

	private static boolean isAssignation(TokenBuffer tokenBuffer) {
		return tokenBuffer.peekTokenType(IDENTIFIER)
				&& tokenBuffer.lookaheadType(1, EQUALS);
	}



	private NodeResponse handleEqualsToken(Token identifierToken, Token equalsToken, TokenBuffer tokenBuffer) {

		Try<List<Token>> listTry = tokenBuffer.consumeUntil(SEMICOLON);

		if (listTry.isFail()) {
			return response(listTry.getFail().get(), tokenBuffer);
		}

		List<Token> tokens = listTry.getSuccess().get();

		boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();

		if (noTokensBetweenEqualsAndSemiColon) {
			return response(new SemanticErrorException(equalsToken, "was expecting assignation"),
					tokenBuffer);
		}

		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer expressionTokenBuffer = new TokenBuffer(accumulator);

		NodeResponse buildResult = expressionNodeConstructor.build(expressionTokenBuffer);

		if (buildResult.possibleNode().isFail()) {
			return buildResult;
		}
		else if (buildResult.possibleBuffer().hasAnyTokensLeft()) {
			TokenBuffer expressionBuffer = buildResult.possibleBuffer();
			return response(new SemanticErrorException(expressionBuffer.getToken().getSuccess().get(),
					"Expecting an expression after assignation"),
					tokenBuffer);
		}

		ASTNode astNode = buildResult.possibleNode().getSuccess().get().get();

		return response(getAssignation(identifierToken, (Expression) astNode), tokenBuffer);
	}

	private static Assignation getAssignation(Token identifierToken, Expression expression) {
		Identifier identifier = new Identifier(identifierToken.associatedString(), identifierToken.position());
		return new Assignation(identifier, expression, identifier.getPosition());
	}


}
