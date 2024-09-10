package org.example;



import functional.Try;

import org.example.lexer.token.Token;
import org.example.nodeconstructors.BlockNodeConstructor;
import org.example.nodeconstructors.NodeResponse;
import org.example.nodeconstructors.NodeConstructor;
import org.example.nodeconstructors.ScopeNodeConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser implements PrintScriptIterator<ASTNode>{
	private final ScopeNodeConstructor scopeConstructor;
	private final TokenBuffer tokens;

	public Parser(List<NodeConstructor> nodeConstructors,
				List<BlockNodeConstructor> blockNodeConstructors,
				TokenBuffer tokens) {
		LinkedList<NodeConstructor> constructors = new LinkedList<>(nodeConstructors);
		constructors.addAll(blockNodeConstructors);
		this.scopeConstructor = new ScopeNodeConstructor(constructors);
		blockNodeConstructors.forEach(cons -> cons.acceptInnerConstructor(this.scopeConstructor));
		this.tokens = tokens;

	}

	public Try<ASTNode> parseExpression() {
		NodeResponse build = scopeConstructor.build(tokens);
		if (build.possibleNode().isFail()) {
			return new Try<>(build.possibleNode().getFail().get());
		}

		Optional<ASTNode> optionalASTNode = build.possibleNode().getSuccess().get();

		return optionalASTNode.map(Try::new)
				.orElseGet(() -> {
					Token errorToken = build.possibleBuffer().getToken().getSuccess().get();
					return new Try<>(
							new SemanticErrorException(errorToken));
				});
	}

	@Override
	public boolean hasNext() {
		return this.tokens.hasAnyTokensLeft();
	}

	@Override
	public ASTNode getNext() throws Exception {

		if (!hasNext()) {
			throw new NoMoreTokensException();
		}

		Try<ASTNode> astNodeExceptionTry = parseExpression();
		if (astNodeExceptionTry.isFail()){
			throw astNodeExceptionTry.getFail().get();
		}
		return astNodeExceptionTry.getSuccess().get();
	}
}
