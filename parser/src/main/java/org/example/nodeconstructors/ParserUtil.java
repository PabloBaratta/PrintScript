package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.SemanticErrorException;
import org.example.TokenBuffer;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class ParserUtil {

	public static ParseEqualsResult
			handleEqualsWithTermination(NodeConstructor expressionConstructor,
										Token eq,
										TokenBuffer tb) throws Exception {
		List<Token> tokens = new LinkedList<>();

		Token currentToken = eq;
		while (!tb.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

			if (!tb.hasAnyTokensLeft()) {
				throw (new SemanticErrorException(currentToken, "was expecting closing after"));
			}

			currentToken = tb.getToken().get();
			tb = tb.consumeToken();
			tokens.add(currentToken);
		}

		boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();
		if (noTokensBetweenEqualsAndSemiColon){
			throw new SemanticErrorException(eq, "was expecting assignation");
		}

		TokenBuffer expressionTokenBuffer = new TokenBuffer(tokens);

		NodeResponse buildResult = expressionConstructor.build(expressionTokenBuffer);

		if (buildResult.possibleNode().isFail()) {
			throw buildResult.possibleNode().getFail().get();
		}
		else if (buildResult.possibleBuffer().hasAnyTokensLeft()){
			Optional<Token> token = buildResult.possibleBuffer().getToken();
			String message = "unexpected expression";
			SemanticErrorException exception = new SemanticErrorException(token.get(), message);
			throw exception;
		}

		return new ParseEqualsResult(buildResult.possibleNode().getSuccess().get().get(), tb.consumeToken());

	}
	public record ParseEqualsResult(ASTNode node, TokenBuffer buffer){}

}
