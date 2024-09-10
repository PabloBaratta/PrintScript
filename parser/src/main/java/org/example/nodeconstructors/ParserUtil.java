package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.SemanticErrorException;
import org.example.TokenBuffer;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import functional.Try;

import java.util.List;
import java.util.Optional;

import static org.example.nodeconstructors.NodeResponse.response;


public class ParserUtil {

	public static ParseEqualsResult
			handleEqualsWithTermination(NodeConstructor expressionConstructor,
										Token eq,
										TokenBuffer tokenBuffer)
			throws Exception {


		Try<List<Token>> listTry = tokenBuffer.consumeUntil(NativeTokenTypes.SEMICOLON.toTokenType());

		if (listTry.isFail()) {
			throw listTry.getFail().get();
		}

		List<Token> tokens = listTry.getSuccess().get();


		boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();

		if (noTokensBetweenEqualsAndSemiColon){
			throw new SemanticErrorException(eq, "was expecting assignation");
		}

		Accumulator accumulator = new Accumulator(tokens);
		TokenBuffer expressionTokenBuffer = new TokenBuffer(accumulator);

		NodeResponse buildResult = expressionConstructor.build(expressionTokenBuffer);

		if (buildResult.possibleNode().isFail()) {
			throw buildResult.possibleNode().getFail().get();
		}

		else if (buildResult.possibleBuffer().hasAnyTokensLeft()){
			Optional<Token> token = buildResult.possibleBuffer().getToken().getSuccess();
			String message = "unexpected expression";
			throw new SemanticErrorException(token.get(), message);
		}

		return new ParseEqualsResult(buildResult.possibleNode().getSuccess().get().get(), tokenBuffer);

	}
	public record ParseEqualsResult(ASTNode node, TokenBuffer buffer){}

	private Token extractNextToken(TokenBuffer buffer, List<TokenType> expectedTypes)
			throws Exception {

		Try<Token> tokenTry = buffer.consumeToken(expectedTypes);
		if (tokenTry.isFail()) {
			throw tokenTry.getFail().get();
		}

		return tokenTry.getSuccess().get();
	}

}
