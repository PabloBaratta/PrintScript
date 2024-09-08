package org.example.nodeconstructors;

import org.example.Program;
import org.example.TextLiteral;
import org.example.TokenBuffer;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ScopeCollector extends ScopeNodeConstructor{
	public ScopeCollector() {
		super(List.of());
	}

	List<Token> collectedTokens = new LinkedList<>();
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) throws Exception {
		while (tokenBuffer.hasAnyTokensLeft()) {
			collectedTokens.add(tokenBuffer.getToken().get());
			tokenBuffer = tokenBuffer.consumeToken();
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
