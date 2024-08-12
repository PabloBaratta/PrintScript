package org.example;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.example.nodeconstructors.ExpressionNodeConstructor;
import org.example.nodeconstructors.NodeConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class ParserTest {
    @Test
    public void test1(){
        List<NodeConstructor> list = new ArrayList<>();
        List<TokenType> operators = new ArrayList<>();
        List<TokenType> expressions = new ArrayList<>();
        operators.add(NativeTokenTypes.PLUS.toTokenType());
        operators.add(NativeTokenTypes.MINUS.toTokenType());
        operators.add(NativeTokenTypes.ASTERISK.toTokenType());
        operators.add(NativeTokenTypes.SLASH.toTokenType());
        expressions.add(NativeTokenTypes.NUMBER.toTokenType());
        expressions.add(NativeTokenTypes.STRING.toTokenType());
        expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
        list.add(new ExpressionNodeConstructor(operators, expressions));
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(1, 1, 1)));
        tokens.add(new Token(NativeTokenTypes.PLUS.toTokenType(), "+", new Position(2, 1, 1)));
        tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "2", new Position(3, 1, 1)));
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
        Try<ASTNode, Exception> res = parser.parseExpression();
        ASTNode node = res.getSuccess().get();
        Assertions.assertEquals("1.0+2.0", node.toString());
    }

    @Test
    public void test2(){
        List<NodeConstructor> list = new ArrayList<>();
        List<TokenType> operators = new ArrayList<>();
        List<TokenType> expressions = new ArrayList<>();
        operators.add(NativeTokenTypes.PLUS.toTokenType());
        operators.add(NativeTokenTypes.MINUS.toTokenType());
        operators.add(NativeTokenTypes.ASTERISK.toTokenType());
        operators.add(NativeTokenTypes.SLASH.toTokenType());
        expressions.add(NativeTokenTypes.NUMBER.toTokenType());
        expressions.add(NativeTokenTypes.STRING.toTokenType());
        expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
        list.add(new ExpressionNodeConstructor(operators, expressions));
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(1, 1, 1)));
        tokens.add(new Token(NativeTokenTypes.PLUS.toTokenType(), "+", new Position(2, 1, 1)));
        tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "2", new Position(3, 1, 1)));
        tokens.add(new Token(NativeTokenTypes.ASTERISK.toTokenType(), "*", new Position(4, 1, 1)));
        tokens.add(new Token(NativeTokenTypes.STRING.toTokenType(), "hola", new Position(5, 4, 1)));
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
        Try<ASTNode, Exception> res = parser.parseExpression();
        ASTNode node = res.getSuccess().get();
        Assertions.assertEquals("1.0+2.0*hola", node.toString());
    }

    @Test
    public void test3(){
        List<NodeConstructor> list = new ArrayList<>();
        List<TokenType> operators = new ArrayList<>();
        List<TokenType> expressions = new ArrayList<>();
        operators.add(NativeTokenTypes.PLUS.toTokenType());
        operators.add(NativeTokenTypes.MINUS.toTokenType());
        operators.add(NativeTokenTypes.ASTERISK.toTokenType());
        operators.add(NativeTokenTypes.SLASH.toTokenType());
        expressions.add(NativeTokenTypes.NUMBER.toTokenType());
        expressions.add(NativeTokenTypes.STRING.toTokenType());
        expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
        list.add(new ExpressionNodeConstructor(operators, expressions));
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(1, 1, 1)));
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
        Try<ASTNode, Exception> res = parser.parseExpression();
        ASTNode node = res.getSuccess().get();
        Assertions.assertEquals("1.0", node.toString());
    }

    @Test
    public void test4(){
        List<NodeConstructor> list = new ArrayList<>();
        List<TokenType> operators = new ArrayList<>();
        List<TokenType> expressions = new ArrayList<>();
        operators.add(NativeTokenTypes.PLUS.toTokenType());
        operators.add(NativeTokenTypes.MINUS.toTokenType());
        operators.add(NativeTokenTypes.ASTERISK.toTokenType());
        operators.add(NativeTokenTypes.SLASH.toTokenType());
        expressions.add(NativeTokenTypes.NUMBER.toTokenType());
        expressions.add(NativeTokenTypes.STRING.toTokenType());
        expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
        list.add(new ExpressionNodeConstructor(operators, expressions));
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(NativeTokenTypes.STRING.toTokenType(), "\"hola buenas tardes\"", new Position(1, 18, 1)));
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
        Try<ASTNode, Exception> res = parser.parseExpression();
        ASTNode node = res.getSuccess().get();
        Assertions.assertEquals("\"hola buenas tardes\"", node.toString());
    }

    @Test
    public void test5(){
        List<NodeConstructor> list = new ArrayList<>();
        List<TokenType> operators = new ArrayList<>();
        List<TokenType> expressions = new ArrayList<>();
        operators.add(NativeTokenTypes.PLUS.toTokenType());
        operators.add(NativeTokenTypes.MINUS.toTokenType());
        operators.add(NativeTokenTypes.ASTERISK.toTokenType());
        operators.add(NativeTokenTypes.SLASH.toTokenType());
        expressions.add(NativeTokenTypes.NUMBER.toTokenType());
        expressions.add(NativeTokenTypes.STRING.toTokenType());
        expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
        list.add(new ExpressionNodeConstructor(operators, expressions));
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "si", new Position(1, 2, 1)));
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
        Try<ASTNode, Exception> res = parser.parseExpression();
        ASTNode node = res.getSuccess().get();
        Assertions.assertEquals("si", node.toString());
    }
}
