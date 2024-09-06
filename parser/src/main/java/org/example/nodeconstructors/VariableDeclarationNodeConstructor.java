package org.example.nodeconstructors;

import org.example.*;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;

import java.util.*;

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

		if (!tokenBuffer.isNextTokenOfAnyOfThisTypes(variableDeclarationTokenTypes)){
			return new NodeResponse(new Try<>(Optional.empty()), tokenBuffer);
		}

		Token varDeclToken = tokenBuffer.getToken().get();
		TokenBuffer tokenBufferWithoutVarDecl = tokenBuffer.consumeToken();

		String vdError = "was expecting variable declaration with an identifier";
		if (!tokenBufferWithoutVarDecl.hasAnyTokensLeft()) {
			SemanticErrorException exception = new SemanticErrorException(varDeclToken, vdError);
			return response(exception,
					tokenBufferWithoutVarDecl);
		}

		Token identifier = tokenBufferWithoutVarDecl.getToken().get();


		if (!tokenBufferWithoutVarDecl.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())) {
			SemanticErrorException exception = new SemanticErrorException(identifier, vdError);
			return response(exception,
					tokenBufferWithoutVarDecl);
		}

		TokenBuffer tokenBufferWithoutIdentifier = tokenBufferWithoutVarDecl.consumeToken();

		String typeError = "was expecting type assignation operator";
		if (!tokenBufferWithoutIdentifier.hasAnyTokensLeft()) {
			SemanticErrorException exception = new SemanticErrorException(identifier, typeError);
			return response(exception,
					tokenBufferWithoutIdentifier);
		}

		Token typeAssignationOp = tokenBufferWithoutIdentifier.getToken().get();

		if (!tokenBufferWithoutIdentifier.isNextTokenOfType(NativeTokenTypes.COLON.toTokenType())) {
			return response(new SemanticErrorException(typeAssignationOp, typeError),
					tokenBufferWithoutIdentifier);
		}

		TokenBuffer tokenBufferWithoutTypeAssig = tokenBufferWithoutIdentifier.consumeToken();

		if (!tokenBufferWithoutTypeAssig.hasAnyTokensLeft()) {
			return response(new SemanticErrorException(typeAssignationOp, "was expecting a valid type"),
					tokenBufferWithoutTypeAssig);
		}

		Token type = tokenBufferWithoutTypeAssig.getToken().get();

		if (!tokenBufferWithoutTypeAssig.isNextTokenOfAnyOfThisTypes(literalTypes)) {
			return response(new SemanticErrorException(type, "was expecting a valid type"),
					tokenBufferWithoutTypeAssig);
		}


		TokenBuffer tokenBufferWithoutType = tokenBufferWithoutTypeAssig.consumeToken();

		if (!tokenBufferWithoutType.hasAnyTokensLeft()){
			return response(new SemanticErrorException(type, "was expecting assignation or closing"),
					tokenBufferWithoutTypeAssig);
		}

		if (tokenBufferWithoutType.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

			return response(getVariableDeclaration(identifier, type, Optional.empty()),
					tokenBufferWithoutType.consumeToken());
		}
		else if (tokenBufferWithoutType.isNextTokenOfType(NativeTokenTypes.EQUALS.toTokenType())){
			Token equalsToken = tokenBufferWithoutType.getToken().get();
			return handleEqualsToken(identifier, equalsToken, type, tokenBufferWithoutType.consumeToken());

		}
		else {
			return response(new SemanticErrorException(type, "was expecting assignation or closing"),
					tokenBufferWithoutType);
		}
	}

	private NodeResponse handleEqualsToken(Token id, Token eq, Token type, TokenBuffer tb) {
		List<Token> tokens = new LinkedList<>();

		Token currentToken = eq;
		while (!tb.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

			if (!tb.hasAnyTokensLeft()) {
				return response(new SemanticErrorException(currentToken, "was expecting closing after"),
						tb);
			}

			currentToken = tb.getToken().get();
			tb = tb.consumeToken();
			tokens.add(currentToken);
		}

		boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();
		if (noTokensBetweenEqualsAndSemiColon){
			return response(new SemanticErrorException(eq, "was expecting assignation"),
					tb);
		}

		TokenBuffer expressionTokenBuffer = new TokenBuffer(tokens);

		NodeResponse buildResult = expressionNodeConstructor.build(expressionTokenBuffer);

		if (buildResult.possibleNode().isFail()) {
			return buildResult;
		}
		else if (buildResult.possibleBuffer().hasAnyTokensLeft()){
			Optional<Token> token = buildResult.possibleBuffer().getToken();
			String message = "unexpected expression";
			SemanticErrorException exception = new SemanticErrorException(token.get(), message);
			return response(exception, buildResult.possibleBuffer());
		}

		ASTNode astNode = buildResult.possibleNode().getSuccess().get().get();

		return response(getVariableDeclaration(id, type, Optional.of((Expression) astNode)), tb.consumeToken());
	}


	private static VariableDeclaration getVariableDeclaration(Token id, Token typeToken, Optional<Expression> exp) {
		Identifier identifier = new Identifier(id.associatedString(), id.position());
		Type type = new Type(typeToken.associatedString(), typeToken.position());
		return new VariableDeclaration(identifier, type, exp);
	}



}
