package org.example.nodeconstructors;

import org.example.TextLiteral;
import org.example.TokenBuffer;
import org.token.Position;
import org.token.Token;
import functional.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CollectorNodeConstructor implements NodeConstructor{

	List<Token> collectedTokens = new LinkedList<>();
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {
		while (tokenBuffer.hasAnyTokensLeft()) {
			collectedTokens.add(tokenBuffer.getToken().getSuccess().get());
		}

		return new NodeResponse(
				new Try<>(Optional.of(new TextLiteral("value", new Position(0,0,0,0)))),
				tokenBuffer
		);
	}

	public List<Token> getCollectedTokens() {
		return collectedTokens;
	}
}
