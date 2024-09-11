package org.example.nodeconstructors;

import org.example.*;
import org.token.Position;
import org.token.Token;
import org.token.TokenType;
import static org.token.NativeTokenTypes.*;
import functional.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


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
			Token ifToken = tokenBuffer.getToken().getSuccess().get();

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

		if (!tokenBuffer.peekTokenType(ELSE)) {
			return Optional.empty();
		}

		Token elseToken = tokenBuffer.getToken().getSuccess().get();

		return Optional.of(parseBody(tokenBuffer, elseToken));
	}

	private BlockAndBuffer parseBody(TokenBuffer tokenBuffer, Token token) throws Exception {
		int bracesCount = 0;

		Try<Token> leftBraceTry = tokenBuffer.consumeToken(LEFT_BRACE);

		if (leftBraceTry.isFail()) {
			throw leftBraceTry.getFail().get();
		}

		Token leftBrace = leftBraceTry.getSuccess().get();

		bracesCount++;

		List<Token> acummulatedTokens = new LinkedList<>();

		while (bracesCount != 0) {
			if (!tokenBuffer.hasAnyTokensLeft()) {
				throw new SemanticErrorException(leftBrace, "unclosed brace");
			} else if (tokenBuffer.peekTokenType(RIGHT_BRACE)) {
				bracesCount--;
				if (bracesCount == 0) {tokenBuffer.consumeToken(RIGHT_BRACE); continue;}
			} else if (tokenBuffer.peekTokenType(LEFT_BRACE)) {
				bracesCount++;
			}
			Token accToken = tokenBuffer.getToken().getSuccess().get();
			acummulatedTokens.add(accToken);
		}

		Accumulator accumulator = new Accumulator(acummulatedTokens);
		NodeResponse build = innerConstructor.buildAll(new TokenBuffer(accumulator));

		if (build.possibleNode().isFail()) {
			throw build.possibleNode().getFail().get();
		}

		Program program = (Program) build.possibleNode().getSuccess().get().get();
		return new BlockAndBuffer(tokenBuffer, program.getChildren());

	}

	private ExpressionAndBuffer parseExpression(TokenBuffer tokenBuffer, Token ifToken) throws Exception {
		int parenthesisCount = 0;

		Try<Token> leftParTry = tokenBuffer.consumeToken(LEFT_PARENTHESIS);

		if (leftParTry.isFail()) {
			throw leftParTry.getFail().get();
		}

		Token leftPar = leftParTry.getSuccess().get();

		parenthesisCount++;

		List<Token> acummulatedTokens = new LinkedList<>();

		while (parenthesisCount != 0) {

			if (!tokenBuffer.hasAnyTokensLeft()) {
				throw new SemanticErrorException(leftPar, "unclosed parenthesis");
			} else if (tokenBuffer.peekTokenType(RIGHT_PARENTHESES)) {
				parenthesisCount--;
				if (parenthesisCount == 0) {
					tokenBuffer.consumeToken(RIGHT_PARENTHESES);
					continue;}
			} else if (tokenBuffer.peekTokenType(LEFT_PARENTHESIS)) {
				parenthesisCount++;
			}

			Token token = tokenBuffer.getToken().getSuccess().get();
			acummulatedTokens.add(token);
		}

		if (acummulatedTokens.isEmpty()) {
			throw new SemanticErrorException(
					leftPar,
					"If condition is not an expression");
		}


		Accumulator accumulator = new Accumulator(acummulatedTokens);
		NodeResponse build = expressionNodeConstructor.build(new TokenBuffer(accumulator));

		if (build.possibleNode().isFail()) {
			throw build.possibleNode().getFail().get();
		}

		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();
		boolean notRecognizedAsExpression = optionalASTNode.isEmpty();

		if (notRecognizedAsExpression) {
			throw new SemanticErrorException(
					build.possibleBuffer().getToken().getSuccess().get(),
					"If condition is not an expression");
		}

		return new ExpressionAndBuffer(tokenBuffer, (Expression) optionalASTNode.get());
	}

	private static boolean isNotThisExpression(TokenBuffer tokenBuffer) {
		return !tokenBuffer.peekTokenType(IF);
	}

	@Override
	public void acceptInnerConstructor(ScopeNodeConstructor inner) {
		this.innerConstructor = inner;
	}

	private record ExpressionAndBuffer(TokenBuffer buffer, Expression expression){}

	private record BlockAndBuffer(TokenBuffer buffer, List<ASTNode> nodes){}
}
