package org.example.nodeconstructors;

import org.example.*;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.nodeconstructors.NodeConstructionResponse.*;

public class CallExpressionNodeConstructor implements NodeConstructor{

    //whether it has to check ";" --> Refactor

    private final boolean terminal;
    private final NodeConstructor expressionNodeConstructor;

    public CallExpressionNodeConstructor(boolean terminal, NodeConstructor expressionNodeConstructor){
        this.terminal = terminal;
        this.expressionNodeConstructor = expressionNodeConstructor;
    }



    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {

        if (!(tokenBuffer.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())
            || tokenBuffer.isNextTokenOfType(NativeTokenTypes.PRINTLN.toTokenType()))){
            return emptyResponse(tokenBuffer);
        }

        Token identifier = tokenBuffer.getToken().get();
        TokenBuffer tokenBufferWithoutIdentifier = tokenBuffer.consumeToken();

        if (!tokenBufferWithoutIdentifier.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())) {
            return emptyResponse(tokenBuffer);
        }

        TokenBuffer tokenBufferWithoutLeft = tokenBufferWithoutIdentifier.consumeToken();
        return handleCallExpression(identifier, tokenBufferWithoutLeft);
    }

    //TODO refactor
    private NodeConstructionResponse handleCallExpression(Token identifier, TokenBuffer tokenBuffer) {
        // ( --> +1 , ) --> -1
        int parenthesisCount = 1;
        Token lastToken = identifier; // it should be the left parenthesis

        List<Expression> listOfArguments = new LinkedList<>();
        List<Token> tokenAcc = new LinkedList<>();
        while (parenthesisCount > 0) {

            if (!tokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(lastToken, "expecting function closure"),
                        tokenBuffer);
            }

            if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType())) {
                parenthesisCount++;
                lastToken = tokenBuffer.getToken().get();
                tokenAcc.add(lastToken);
                tokenBuffer = tokenBuffer.consumeToken(); //continue

            }
            else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
                parenthesisCount--;
                lastToken = tokenBuffer.getToken().get();
                if (parenthesisCount != 0) {
                    tokenAcc.add(lastToken);
                }
                tokenBuffer = tokenBuffer.consumeToken(); //continue
            }
            else if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.COMMA.toTokenType())) {

                if (tokenAcc.isEmpty()) {
                    return response(new SemanticErrorException(lastToken, "expected expression after comma"),
                            tokenBuffer);
                }
                lastToken = tokenBuffer.getToken().get(); //check
                tokenBuffer = tokenBuffer.consumeToken(); //continue

                if (tokenBuffer.isNextTokenOfType(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType())) {
                    return response(new SemanticErrorException(lastToken, "expected expression after comma"),
                            tokenBuffer);
                }
                NodeConstructionResponse build = expressionNodeConstructor.build(new TokenBuffer(tokenAcc));
            
                if (build.possibleNode().isFail()){
                    return build;
                } 
                else if (build.possibleNode().getSuccess().isEmpty()) {
                    Token errorToken = build.possibleBuffer().getToken().get();
                    return response(new SemanticErrorException(errorToken, "not an expression"), tokenBuffer);
                }

                listOfArguments.add( (Expression) build.possibleNode().getSuccess().get().get());
                tokenAcc.clear();
            }
            else {
                lastToken = tokenBuffer.getToken().get();
                tokenBuffer = tokenBuffer.consumeToken();
                tokenAcc.add(lastToken);
            }
        }

        // f(a,b) --> build the b
        if (!tokenAcc.isEmpty()) {

            NodeConstructionResponse build = expressionNodeConstructor.build(new TokenBuffer(tokenAcc));

            if (build.possibleNode().isFail()){
                return build;
            }
            else if (build.possibleNode().getSuccess().isEmpty()) {
                Token errorToken = build.possibleBuffer().getToken().get();
                return response(new SemanticErrorException(errorToken, "not an expression"), tokenBuffer);
            }

            listOfArguments.add( (Expression) build.possibleNode().getSuccess().get().get());
            tokenAcc.clear();
        }

        if (terminal) {
            if (!tokenBuffer.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())) {
                return response(new SemanticErrorException(lastToken, "expected end of statement"),
                        tokenBuffer);
            };
            tokenBuffer = tokenBuffer.consumeToken();
        }

        return response(createMethodNode(identifier, listOfArguments), tokenBuffer);
    }

    private static Method createMethodNode(Token identifier, List<Expression> listOfArguments) {
        return new Method(new Identifier(identifier.associatedString()), listOfArguments);
    }
}
