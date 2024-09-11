package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.TokenBuffer;
import functional.Try;

import java.util.Optional;

public record NodeResponse(
		Try<Optional<ASTNode>> possibleNode,
		TokenBuffer possibleBuffer
) {

	static NodeResponse response(Exception exception, TokenBuffer buffer){
		return new NodeResponse(
				new Try<>(exception),
				buffer
		);
	}

	static NodeResponse response(ASTNode node, TokenBuffer buffer){
		return new NodeResponse(
				new Try<>(Optional.of(node)),
				buffer
		);
	}

	static NodeResponse emptyResponse(TokenBuffer buffer){
		return new NodeResponse(
				new Try<>(Optional.empty()),
				buffer
		);
	}
}
