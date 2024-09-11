package org.example.nodeconstructors;

import org.example.Program;
import org.example.TokenBuffer;
import org.token.Token;
import functional.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ScopeCollector extends ScopeNodeConstructor{
	public ScopeCollector() {
		super(List.of());
	}

	List<Token> collectedTokens = new LinkedList<>();
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {
		while (tokenBuffer.hasAnyTokensLeft()) {
			collectedTokens.add(tokenBuffer.getToken().getSuccess().get());
		}

		return new NodeResponse(
				new Try<>(Optional.of(new Program(List.of()))),
				tokenBuffer
		);
	}

	public List<Token> getCollectedTokens() {
		return collectedTokens;
	}
}
