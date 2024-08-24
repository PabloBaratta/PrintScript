package org.example.nodeconstructors;

import org.example.TextLiteral;
import org.example.TokenBuffer;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExpressionCollectorNodeConstructor implements NodeConstructor{

	List<Token> collectedTokens = new LinkedList<>();
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {
		while (tokenBuffer.hasAnyTokensLeft()) {
			collectedTokens.add(tokenBuffer.getToken().get());
			tokenBuffer = tokenBuffer.consumeToken();
		}

		return new NodeResponse(
				new Try<>(Optional.of(new TextLiteral("default value", new Position(0,0,0)))),
				tokenBuffer
		);
	}

	public List<Token> getCollectedTokens() {
		return collectedTokens;
	}
}
