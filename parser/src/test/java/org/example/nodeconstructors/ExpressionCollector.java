package org.example.nodeconstructors;

import org.example.TextLiteral;
import org.example.TokenBuffer;
import org.token.Position;
import org.token.Token;
import functional.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.token.NativeTokenTypes.*;

public class ExpressionCollector extends ExpressionNodeConstructor{

	List<Token> collectedTokens = new LinkedList<>();


	public ExpressionCollector() {

		super(Map.of(),
				List.of(),
				new CallExpressionNodeConstructor(
						false,
						null,
						List.of(PRINTLN.toTokenType(),
								READENV.toTokenType(),
								READINPUT.toTokenType())));
	}

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
