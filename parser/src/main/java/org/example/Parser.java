package org.example;



import org.example.lexer.utils.Try;

import org.example.nodeconstructors.BlockNodeConstructor;
import org.example.nodeconstructors.NodeConstructionResponse;
import org.example.nodeconstructors.NodeConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {
    private final List<NodeConstructor> nodeConstructors;
    private TokenBuffer tokens;

    public Parser(List<NodeConstructor> nodeConstructors,
                  List<BlockNodeConstructor> blockNodeConstructors,
                  TokenBuffer tokens) {
        this.nodeConstructors = new LinkedList(nodeConstructors);
        blockNodeConstructors.forEach(cons -> cons.acceptParser(this));
        this.nodeConstructors.addAll(blockNodeConstructors);
        this.tokens = tokens;
    }

    public Try<ASTNode, Exception> parseExpression() {

        LinkedList<ASTNode> nodes = new LinkedList<>();

        while (tokens.hasAnyTokensLeft()) {
            Response response = getAstNodeExceptionTry();

            Try<ASTNode, Exception> astNodeExceptionTry = response.result;

            if (astNodeExceptionTry.isFail()){
                return astNodeExceptionTry;
            }

            nodes.add(astNodeExceptionTry.getSuccess().get());
            this.tokens = response.newBuffer();
        }
        
        return new Try<>(new Program(nodes));
        
    }

    private Response getAstNodeExceptionTry() {
        for (NodeConstructor nodeConstructor : nodeConstructors) {

            NodeConstructionResponse build = nodeConstructor.build(tokens);
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
                this.tokens);
    }

    private record Response(
            Try<ASTNode, Exception> result,
            TokenBuffer newBuffer
            ){}
}
