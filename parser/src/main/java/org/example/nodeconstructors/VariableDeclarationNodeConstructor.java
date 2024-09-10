package org.example.nodeconstructors;

import org.example.*;

import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import functional.Try;

import java.util.*;

import static org.example.lexer.token.NativeTokenTypes.*;
import static org.example.nodeconstructors.NodeResponse.response;

public class VariableDeclarationNodeConstructor implements NodeConstructor {

	private final NodeConstructor expressionNodeConstructor;
	private final List<TokenType> variableDeclarationTokenTypes;
	private final List<TokenType> literalTypes;


	public VariableDeclarationNodeConstructor(NodeConstructor ec,
											List<TokenType> types,
											List<TokenType> literalType) {
		this.expressionNodeConstructor = ec;
		this.variableDeclarationTokenTypes = types;
		this.literalTypes = literalType;
	}

	//TODO refactor
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {

		if (!tokenBuffer.peekTokenType(variableDeclarationTokenTypes)){
			return new NodeResponse(new Try<>(Optional.empty()), tokenBuffer);
		}

		tokenBuffer.consumeToken(variableDeclarationTokenTypes);

		Try<Token> identifierTry = tokenBuffer.consumeToken(IDENTIFIER);

		if (identifierTry.isFail()) {
			return response(identifierTry.getFail().get(), tokenBuffer);
		}

		Token identifier = identifierTry.getSuccess().get();

		Try<Token> colonTry = tokenBuffer.consumeToken(COLON);

		if (colonTry.isFail()) {
			return response(colonTry.getFail().get(), tokenBuffer);
		}

		Try<Token> typeTry = tokenBuffer.consumeToken(literalTypes);

		if (typeTry.isFail()) {
			return response(typeTry.getFail().get(), tokenBuffer);
		}

		Token type = typeTry.getSuccess().get();


		if (tokenBuffer.peekTokenType(SEMICOLON)){

			tokenBuffer.consumeToken(SEMICOLON);

			return response(getVariableDeclaration(identifier, type, Optional.empty()),
					tokenBuffer);
		}
		else if (tokenBuffer.peekTokenType(EQUALS)){
			Token equalsToken = tokenBuffer.consumeToken(EQUALS).getSuccess().get();
			return handleEqualsToken(identifier, equalsToken, type, tokenBuffer);
		}
		else {
			return response(new SemanticErrorException(type, "was expecting assignation or closing"),
					tokenBuffer);
		}
	}

	private NodeResponse handleEqualsToken(Token id, Token eq, Token type, TokenBuffer tokenBuffer) {

		try {
			ParserUtil.ParseEqualsResult parseEqualsResult = ParserUtil.
					handleEqualsWithTermination(expressionNodeConstructor, eq, tokenBuffer);

			ASTNode astNode = parseEqualsResult.node();

			Optional<Expression> optionalExpression = Optional.of((Expression) astNode);
			return response(getVariableDeclaration(id, type, optionalExpression), tokenBuffer);
		}
		catch (Exception e) {
			return response(e, tokenBuffer);
		}
	}


	private static VariableDeclaration getVariableDeclaration(Token id, Token typeToken, Optional<Expression> exp) {
		Identifier identifier = new Identifier(id.associatedString(), id.position());
		Type type = new Type(typeToken.associatedString(), typeToken.position());
		return new VariableDeclaration(identifier, type, exp);
	}



}
