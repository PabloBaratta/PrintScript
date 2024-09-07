package org.example;



import org.example.lexer.utils.Try;

import org.example.nodeconstructors.BlockNodeConstructor;
import org.example.nodeconstructors.NodeResponse;
import org.example.nodeconstructors.NodeConstructor;
import org.example.nodeconstructors.ScopeNodeConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {
	private final ScopeNodeConstructor scopeConstructor;
	private TokenBuffer tokens;

	public Parser(List<NodeConstructor> nodeConstructors,
				List<BlockNodeConstructor> blockNodeConstructors,
				TokenBuffer tokens) {
		LinkedList<NodeConstructor> constructors = new LinkedList<>(nodeConstructors);
		constructors.addAll(blockNodeConstructors);
		this.scopeConstructor = new ScopeNodeConstructor(constructors);
		blockNodeConstructors.forEach(cons -> cons.acceptInnerConstructor(this.scopeConstructor));
		this.tokens = tokens;

	}

	public Try<ASTNode, Exception> parseExpression() throws Exception {
		NodeResponse build = scopeConstructor.build(tokens);
		if (build.possibleNode().isFail()) {
			return new Try<>(build.possibleNode().getFail().get());
		}

		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();

		return optionalASTNode.map(Try::new)
				.orElseGet(() -> new Try<>(
						new SemanticErrorException(build.possibleBuffer().getToken().get())));
	}

}
