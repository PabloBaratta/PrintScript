package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.lexer.token.NativeTokenTypes.*;

public class IfNodeConstructor implements BlockNodeConstructor{
	private final ExpressionNodeConstructor expressionNodeConstructor;
	private ScopeNodeConstructor innerConstructor;

	public IfNodeConstructor(ExpressionNodeConstructor expressionNodeConstructor) {
		this.expressionNodeConstructor = expressionNodeConstructor;
	}

	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {

		if (isNotThisExpression(tokenBuffer)) {
			return NodeResponse.emptyResponse(tokenBuffer);
		}

		try {
			Token ifToken = tokenBuffer.getToken().get();
			tokenBuffer = tokenBuffer.consumeToken();

			ExpressionAndBuffer condition = parseExpression(tokenBuffer, ifToken);

			tokenBuffer = condition.buffer();
			BlockAndBuffer restOfStatement = parseBody(tokenBuffer, ifToken);

			tokenBuffer = restOfStatement.buffer();
			Optional<BlockAndBuffer> possibleElse = parseElse(tokenBuffer);

			TokenBuffer newBuffer = possibleElse.isEmpty() ? tokenBuffer : possibleElse.get().buffer();
			List<ASTNode> elseBlock = possibleElse.isEmpty() ? List.of() : possibleElse.get().nodes();
			return NodeResponse.response(new IfStatement(condition.expression(),
					restOfStatement.nodes(),
					elseBlock,
					ifToken.position()),
					newBuffer);
		} catch (Exception e) {
			return NodeResponse.response(e, tokenBuffer);
		}


	}

	private Optional<BlockAndBuffer> parseElse(TokenBuffer tokenBuffer) throws Exception {

		if (!tokenBuffer.isNextTokenOfType(ELSE.toTokenType())) {
			return Optional.empty();
		}

		Token elseToken = tokenBuffer.getToken().get();
		tokenBuffer = tokenBuffer.consumeToken();

		return Optional.of(parseBody(tokenBuffer, elseToken));
	}

	private BlockAndBuffer parseBody(TokenBuffer tokenBuffer, Token token) throws Exception {
		int bracesCount = 0;

		if (!tokenBuffer.isNextTokenOfType(LEFT_BRACE.toTokenType())) {
			throw new SemanticErrorException(token, "If condition should be continued by a '{'");
		}

		bracesCount++;
		Token leftBrace = tokenBuffer.getToken().get();
		tokenBuffer = tokenBuffer.consumeToken();
		List<Token> acummulatedTokens = new LinkedList<>();

		while (bracesCount != 0) {
			if (!tokenBuffer.hasAnyTokensLeft()) {
				throw new SemanticErrorException(leftBrace, "unclosed brace");
			} else if (tokenBuffer.isNextTokenOfType(RIGHT_BRACE.toTokenType())) {
				bracesCount--;
				if (bracesCount == 0) {tokenBuffer = tokenBuffer.consumeToken(); continue;}
			} else if (tokenBuffer.isNextTokenOfType(LEFT_BRACE.toTokenType())) {
				bracesCount++;
			}

			Token accToken = tokenBuffer.getToken().get();
			acummulatedTokens.add(accToken);
			tokenBuffer = tokenBuffer.consumeToken();
		}

		NodeResponse build = innerConstructor.build(new TokenBuffer(acummulatedTokens));

		if (build.possibleNode().isFail()) {
			throw build.possibleNode().getFail().get();
		}

		Program program = (Program) build.possibleNode().getSuccess().get().get();
		return new BlockAndBuffer(tokenBuffer, program.getChildren());

	}

	private ExpressionAndBuffer parseExpression(TokenBuffer tokenBuffer, Token ifToken) throws Exception {
		int parenthesisCount = 0;

		if (!tokenBuffer.isNextTokenOfType(LEFT_PARENTHESIS.toTokenType())) {
			throw new SemanticErrorException(ifToken, "If should be continued by a '('");
		}
		parenthesisCount++;
		Token leftPar = tokenBuffer.getToken().get();
		tokenBuffer = tokenBuffer.consumeToken();
		List<Token> acummulatedTokens = new LinkedList<>();

		while (parenthesisCount != 0) {

			if (!tokenBuffer.hasAnyTokensLeft()) {
				throw new SemanticErrorException(leftPar, "unclosed parenthesis");
			} else if (tokenBuffer.isNextTokenOfType(RIGHT_PARENTHESES.toTokenType())) {
				parenthesisCount--;
				if (parenthesisCount == 0) {tokenBuffer = tokenBuffer.consumeToken(); continue;}
			} else if (tokenBuffer.isNextTokenOfType(LEFT_PARENTHESIS.toTokenType())) {
				parenthesisCount++;
			}

			Token token = tokenBuffer.getToken().get();
			acummulatedTokens.add(token);
			tokenBuffer = tokenBuffer.consumeToken();
		}

		NodeResponse build = expressionNodeConstructor.build(new TokenBuffer(acummulatedTokens));

		if (build.possibleNode().isFail()) {
			throw build.possibleNode().getFail().get();
		}

		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();
		boolean notRecognizedAsExpression = optionalASTNode.isEmpty();

		if (notRecognizedAsExpression) {
			throw new SemanticErrorException(
					build.possibleBuffer().getToken().get(),
					"If condition is not an expression");
		}

		return new ExpressionAndBuffer(tokenBuffer, (Expression) optionalASTNode.get());
	}

	private static boolean isNotThisExpression(TokenBuffer tokenBuffer) {
		return !tokenBuffer.isNextTokenOfType(IF.toTokenType());
	}

	@Override
	public void acceptInnerConstructor(ScopeNodeConstructor inner) {
		this.innerConstructor = inner;
	}

	private record ExpressionAndBuffer(TokenBuffer buffer, Expression expression){}

	private record BlockAndBuffer(TokenBuffer buffer, List<ASTNode> nodes){}
}
