package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import functional.Try;

import java.util.List;
import java.util.Optional;

import static org.example.lexer.token.NativeTokenTypes.*;
import static org.example.nodeconstructors.NodeResponse.response;

public class ConstNodeConstructor implements NodeConstructor {

	private final NodeConstructor expressionNodeConstructor;
	private final List<TokenType> literalTypes;


	public ConstNodeConstructor(NodeConstructor ec,
								List<TokenType> literalType) {
		this.expressionNodeConstructor = ec;
		this.literalTypes = literalType;
	}


	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {
		if (!tokenBuffer.peekTokenType(CONST)){
			return new NodeResponse(new Try<>(Optional.empty()), tokenBuffer);
		}
		try {
			extractNextToken(tokenBuffer, CONST);
			Token identifierToken = extractNextToken(tokenBuffer, IDENTIFIER);
			extractNextToken(tokenBuffer, COLON);
			Token typeToken = extractNextToken(tokenBuffer, literalTypes);
			Token equalsToken = extractNextToken(tokenBuffer, EQUALS);

			return handleEqualsToken(identifierToken, equalsToken, typeToken, tokenBuffer);
		}
		catch (Exception e) {
			return NodeResponse.response(e, tokenBuffer);
		}
	}


	private Token extractNextToken(TokenBuffer buffer, NativeTokenTypes expectedType)
			throws Exception {

		Try<Token> tokenTry = buffer.consumeToken(expectedType);
		if (tokenTry.isFail()) {
			throw tokenTry.getFail().get();
		}

		return tokenTry.getSuccess().get();
	}

	private Token extractNextToken(TokenBuffer buffer, List<TokenType> expectedTypes)
			throws Exception {

		Try<Token> tokenTry = buffer.consumeToken(expectedTypes);
		if (tokenTry.isFail()) {
			throw tokenTry.getFail().get();
		}

		return tokenTry.getSuccess().get();
	}

	private NodeResponse handleEqualsToken(Token id, Token eq, Token type, TokenBuffer tb) throws Exception {
		ParserUtil.ParseEqualsResult parseEqualsResult =
				ParserUtil.handleEqualsWithTermination(expressionNodeConstructor, eq, tb);
		ASTNode astNode = parseEqualsResult.node();
		return response(getConstDeclaration(id, type, (Expression) astNode), parseEqualsResult.buffer());
	}

	private ASTNode getConstDeclaration(Token id, Token typeToken, Expression astNode) {
		Identifier identifier = new Identifier(id.associatedString(), id.position());
		Type type = new Type(typeToken.associatedString(), typeToken.position());
		return new ConstDeclaration(identifier, type, astNode);
	}


}
