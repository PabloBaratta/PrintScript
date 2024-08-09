package org.example.parser;

import org.example.Token;
import org.example.Try;
import org.example.parser.AST.ASTNode;
import org.example.parser.AST.Program;
import org.example.parser.nodeconstructors.BlockNodeConstructor;
import org.example.parser.nodeconstructors.NodeConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class Parser {
    private final List<NodeConstructor> nodeConstructors;
    private final Queue<Token> tokens;

    public Parser(List<NodeConstructor> nodeConstructors,
                  List<BlockNodeConstructor> blockNodeConstructors,
                  Queue<Token> tokens) {
        blockNodeConstructors.forEach(cons -> cons.acceptParser(this));
        nodeConstructors.addAll(blockNodeConstructors);
        this.nodeConstructors = nodeConstructors;
        this.tokens = tokens;
    }

    public Try<ASTNode, Exception> parseExpression() {

        LinkedList<ASTNode> nodes = new LinkedList<>();

        while (!tokens.isEmpty()) {

            Token token = getNextToken();

          /*  if (token == null) {
                return new Try<>(new NoMoreTokensAvailableException());
            }
           */

            Try<ASTNode, Exception> astNodeExceptionTry = getAstNodeExceptionTry(token, nodes);

            if (astNodeExceptionTry.isFail()){
                return astNodeExceptionTry;
            }

            nodes.add(astNodeExceptionTry.getSuccess().get());
        }
        
        return new Try<>(new Program(nodes));
        
    }

    private Try<ASTNode, Exception> getAstNodeExceptionTry(Token token, LinkedList<ASTNode> nodes) {
        for (NodeConstructor nodeConstructor : nodeConstructors) {

            Try<Optional<ASTNode>, Exception> possibleNodeOrError = nodeConstructor.build(token, tokens);

            //if node construction sends exception return exception
            if (possibleNodeOrError.isFail()) {
                return new Try<>(possibleNodeOrError.getFail().get());
            }

            Optional<ASTNode> astNode = possibleNodeOrError.getSuccess().get();

            //If node construction is positive --> return token
            if (astNode.isPresent()) {
                return new Try<>(astNode.get());
            }

            //If node constructión doesnt give any token --> pass
        }

        return new Try<>(new SemanticErrorException(token));
    }

    private Token getNextToken() {
        return tokens.poll();
    }
}
