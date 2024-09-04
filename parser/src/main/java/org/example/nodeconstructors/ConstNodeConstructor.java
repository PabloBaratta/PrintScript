package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
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
		if (!tokenBuffer.isNextTokenOfType(CONST.toTokenType())){
			return new NodeResponse(new Try<>(Optional.empty()), tokenBuffer);
		}

		try {
			String errorMessage = "Const should be followed by a valid identifier";
			Token identifierToken = extractNextToken(tokenBuffer, IDENTIFIER.toTokenType(), errorMessage);
			TokenBuffer tokenBufferWithIdentifier = tokenBuffer.consumeToken();

			errorMessage = "Identifier should be followed by type assignation ':'";
			extractNextToken(tokenBufferWithIdentifier, COLON.toTokenType(), errorMessage);
			TokenBuffer tokenBufferWithColon = tokenBufferWithIdentifier.consumeToken();

			errorMessage = "Was expecting a valid type";
			Token typeToken = extractNextToken(tokenBufferWithColon, literalTypes, errorMessage);
			TokenBuffer tokenBufferWithType = tokenBufferWithColon.consumeToken();

			errorMessage = "Was expecting equals '=' for const declaration";
			Token equalsToken = extractNextToken(tokenBufferWithType, EQUALS.toTokenType(), errorMessage);
			TokenBuffer tokenBufferWithEquals = tokenBufferWithType.consumeToken();

			TokenBuffer tokenBufferWithoutEquals = tokenBufferWithEquals.consumeToken();
			return handleEqualsToken(identifierToken, typeToken, equalsToken, tokenBufferWithoutEquals);
		}
		catch (Exception e) {
			return NodeResponse.response(e, tokenBuffer);
		}


	}

	//assumes there is a token in the buffer
	private Token extractNextToken(TokenBuffer buffer, TokenType expectedType, String errorMessage)
			throws SemanticErrorException {

		Token lastToken = buffer.getToken().get();
		buffer = buffer.consumeToken();

		if (!buffer.isNextTokenOfType(expectedType)) {
			throw new SemanticErrorException(lastToken, errorMessage);
		}
		return buffer.getToken().get();
	}

	private Token extractNextToken(TokenBuffer buffer, List<TokenType> expectedTypes, String errorMessage)
			throws SemanticErrorException {

		Token lastToken = buffer.getToken().get();
		buffer = buffer.consumeToken();

		if (!buffer.isNextTokenOfAnyOfThisTypes(expectedTypes)) {
			throw new SemanticErrorException(lastToken, errorMessage);
		}
		return buffer.getToken().get();
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
