package org.example.nodeconstructors;

import org.example.*;

import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;

import java.util.*;

import static org.example.nodeconstructors.NodeConstructionResponse.response;

public class VariableDeclarationNodeConstructor implements NodeConstructor {

    private final ExpressionNodeConstructor expressionNodeConstructor;
    private final List<TokenType> variableDeclarationTokenTypes;
    private final List<TokenType> literalTypes;


    VariableDeclarationNodeConstructor(ExpressionNodeConstructor expressionNodeConstructor,
                                       List<TokenType> variableDeclarationTokenTypes,
                                       List<TokenType> literalType
    ) {
        this.expressionNodeConstructor = expressionNodeConstructor;
        this.variableDeclarationTokenTypes = variableDeclarationTokenTypes;
        this.literalTypes = literalType;
    }

    //TODO refactor
    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {

        if (!tokenBuffer.isNextTokenOfAnyOfThisTypes(variableDeclarationTokenTypes)){
            return new NodeConstructionResponse(new Try<>(Optional.empty()), tokenBuffer);
        }

        Optional<Token> varDeclToken = tokenBuffer.getToken();
        TokenBuffer tokenBufferWithoutVarDecl = tokenBuffer.consumeToken();

        if (!tokenBufferWithoutVarDecl.hasAnyTokensLeft() &&
                !tokenBufferWithoutVarDecl.isNextTokenOfType(NativeTokenTypes.IDENTIFIER.toTokenType())) {
            return response(new SemanticErrorException(varDeclToken.get(), "was expecting variable declaration with an identifier"),
                    tokenBufferWithoutVarDecl);
        }

        Token identifier = tokenBufferWithoutVarDecl.getToken().get();
        TokenBuffer tokenBufferWithoutIdentifier = tokenBufferWithoutVarDecl.consumeToken();

        if (!tokenBufferWithoutIdentifier.hasAnyTokensLeft() &&
                !tokenBufferWithoutIdentifier.isNextTokenOfType(NativeTokenTypes.COLON.toTokenType())) {
            return response(new SemanticErrorException(identifier, "was expecting type assignation operator"),
                    tokenBufferWithoutIdentifier);
        }

        Token typeAssignationOp = tokenBufferWithoutIdentifier.getToken().get();
        TokenBuffer tokenBufferWithoutTypeAssig = tokenBuffer.consumeToken();

        if (!tokenBufferWithoutTypeAssig.hasAnyTokensLeft() &&
                !tokenBufferWithoutTypeAssig.isNextTokenOfAnyOfThisTypes(literalTypes)) {
            return response(new SemanticErrorException(typeAssignationOp, "was expecting a valid type"),
                    tokenBufferWithoutTypeAssig);
        }

        Token proxUnknownToken = tokenBuffer.poll();
        if (proxUnknownToken == null) {
            return new Try<>(new Exception("was expecting closing after " + type.associatedString() + " in character " + type.offset()));
        }

        if (tokenBufferWithoutType.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

            return response(getVariableDeclaration(identifier, type, Optional.empty()),
                    tokenBufferWithoutType.consumeToken());
        }
        else if (tokenBufferWithoutType.isNextTokenOfType(NativeTokenTypes.EQUALS.toTokenType())){
            Token equalsToken = tokenBufferWithoutType.getToken().get();
            return handleEqualsToken(identifier, equalsToken, type, tokenBufferWithoutType.consumeToken());

        }
        else {
            return response(new SemanticErrorException(type, "was expecting assignation or closing"),
                    tokenBufferWithoutType);
        }
    }

    private NodeConstructionResponse handleEqualsToken(Token identifierToken, Token equalsToken, Token type, TokenBuffer tokenBuffer) {
        List<Token> tokens = new LinkedList<>();

        Token currentToken = equalsToken;
        while (!tokenBuffer.isNextTokenOfType(NativeTokenTypes.SEMICOLON.toTokenType())){

            tokenBuffer = tokenBuffer.consumeToken();

            if (!tokenBuffer.hasAnyTokensLeft()) {
                return response(new SemanticErrorException(currentToken, "was expecting closing after"),
                        tokenBuffer);
            }

            currentToken = tokenBuffer.getToken().get();
            tokens.add(currentToken);
        }

        boolean noTokensBetweenEqualsAndSemiColon = tokens.isEmpty();

        if (noTokensBetweenEqualsAndSemiColon){
            return response(new SemanticErrorException(equalsToken, "was expecting assignation"),
                    tokenBuffer);
        }

        TokenBuffer expressionTokenBuffer = new TokenBuffer(tokens);

        NodeConstructionResponse buildResult = expressionNodeConstructor.build(expressionTokenBuffer);

        if (buildResult.possibleNode().isFail()) {
            return buildResult;
        }

        ASTNode astNode = buildResult.possibleNode().getSuccess().get().get();

        return response(getVariableDeclaration(identifierToken, type, Optional.of((Expression) astNode)), tokenBuffer);
    }


    private static VariableDeclaration getVariableDeclaration(Token identifierToken, Token typeToken, Optional<Expression> optionalExpression) {
        Identifier identifier = new Identifier(identifierToken.associatedString());
        Type type = new Type(typeToken.associatedString());
        return new VariableDeclaration(identifier, type, optionalExpression);
    }
}