package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ScopeNodeConstructor implements  NodeConstructor{

	private final List<NodeConstructor> constructors;

	public ScopeNodeConstructor(List<NodeConstructor> constructors) {
		this.constructors = constructors;
	}
	@Override
	public NodeResponse build(TokenBuffer tokenBuffer) {
		LinkedList<ASTNode> nodes = new LinkedList<>();

		while (tokenBuffer.hasAnyTokensLeft()) {
			Response response = getAstNodeExceptionTry(tokenBuffer);

			Try<ASTNode, Exception> astNodeExceptionTry = response.result();

			if (astNodeExceptionTry.isFail()){
				return NodeResponse.response(astNodeExceptionTry.getFail().get(), tokenBuffer);
			}

			nodes.add(astNodeExceptionTry.getSuccess().get());
			tokenBuffer = response.newBuffer();
		}

		return NodeResponse.response(new Program(nodes), tokenBuffer);
	}


	private Response getAstNodeExceptionTry(TokenBuffer tokens) {
		for (NodeConstructor nodeConstructor : constructors) {

			NodeResponse build = nodeConstructor.build(tokens);
			Try<Optional<ASTNode>, Exception> possibleNodeOrError = build.possibleNode();

			//if node construction sends exception return exception
			if (possibleNodeOrError.isFail()) {
				return new Response(new Try<>(possibleNodeOrError.getFail().get()),
						build.possibleBuffer());
			}

			Optional<ASTNode> astNode = possibleNodeOrError.getSuccess().get();

			//If node construction is positive --> return token
			if (astNode.isPresent()) {
				return new Response(new Try<>(astNode.get()),
						build.possibleBuffer());
			}

			//If node constructión doesnt give any token --> pass
		}
		return new Response(new Try<>(new SemanticErrorException(tokens.getToken().get())),
				tokens);
	}

	private record Response(
			Try<ASTNode, Exception> result,
			TokenBuffer newBuffer
	){}
}
