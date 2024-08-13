package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.Assignation;
import org.example.TokenBuffer;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.example.TokenTestUtil.getTokens;
import static org.example.TokenTestUtil.getaTokenFromTokenType;
import static org.example.lexer.token.NativeTokenTypes.*;
import static org.junit.jupiter.api.Assertions.*;

public class AssignationNodeConstructorTest {

    @Test
    public void doesNotRecognizeOtherTokens() {

        NodeConstructor builder = new AssignationNodeConstructor(new ExpressionCollectorNodeConstructor());

        Arrays.stream(NativeTokenTypes.values()).filter(
                type -> !type.equals(NativeTokenTypes.IDENTIFIER)
        ).forEach(type ->
                {TokenBuffer tokenBuffer = new TokenBuffer(List.of(getaTokenFromTokenType(type)));
                NodeConstructionResponse build = builder.build(tokenBuffer);
                assertTrue(build.possibleNode().isSuccess());
                assertTrue(build.possibleNode().getSuccess().get().isEmpty());}
        );

        Arrays.stream(NativeTokenTypes.values()).filter(
                type -> !type.equals(NativeTokenTypes.EQUALS)).
                forEach(type ->
                {TokenBuffer tokenBuffer = new TokenBuffer(List.of(getaTokenFromTokenType(NativeTokenTypes.IDENTIFIER),
                        getaTokenFromTokenType(type)));
                    NodeConstructionResponse build = builder.build(tokenBuffer);
                    assertTrue(build.possibleNode().isSuccess());
                    assertTrue(build.possibleNode().getSuccess().get().isEmpty());});
    }

    @Test
    public void successfulScenarios(){
        NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
                IDENTIFIER, EQUALS, STRING, SEMICOLON
        };

        List<Token> tokens = getTokens(nativeTokenTypes);

        int intermediateTokens = 1;

        assertSuccess(tokens, intermediateTokens);

        nativeTokenTypes = new NativeTokenTypes[]{
                IDENTIFIER, EQUALS, STRING, PLUS, NUMBER, SEMICOLON
        };

        tokens = getTokens(nativeTokenTypes);

        intermediateTokens = 3;

        assertSuccess(tokens, intermediateTokens);
    }
    
    @Test
    public void syntaxErrors() {
        NativeTokenTypes[] nativeTokenTypes = new NativeTokenTypes[]{
                IDENTIFIER, EQUALS, STRING, SEMICOLON
        };

        NodeConstructor builder = new AssignationNodeConstructor(new ExpressionCollectorNodeConstructor());


        List<Token> tokens = getTokens(nativeTokenTypes);

        int originalTokenListSize = tokens.size();
        for (int i = 2; i < originalTokenListSize; i++) {
            tokens.removeLast();
            NodeConstructionResponse build = builder.build(new TokenBuffer(tokens));
            assertTrue(build.possibleNode().isFail());
        }

    }

    private void assertSuccess(List<Token> tokens, int intermediateTokens) {
        ExpressionCollectorNodeConstructor collector = new ExpressionCollectorNodeConstructor();
        NodeConstructor assignationNodeConstructor = new AssignationNodeConstructor(collector);

        NodeConstructionResponse build = assignationNodeConstructor.build(new TokenBuffer(tokens));

        assertTrue(build.possibleNode().isSuccess());
        assertFalse(build.possibleBuffer().hasAnyTokensLeft());
        assertTrue(build.possibleNode().getSuccess().isPresent());
        ASTNode astNode = build.possibleNode().getSuccess().get().get();
        assertInstanceOf(Assignation.class, astNode);
        assertEquals(intermediateTokens, collector.collectedTokens.size());
    }
}
