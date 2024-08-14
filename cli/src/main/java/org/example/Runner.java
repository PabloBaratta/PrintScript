package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.example.nodeconstructors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Runner {

    private static Interpreter createInterpreter() {
        return new Interpreter();
    }

    private static Lexer createLexer(String code) {
        List<Character> whiteSpaces = List.of(' ', '\t', '\n');
        TokenConstructor keywordConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMap());
        Collection<TokenConstructor> tokenConstructors = List.of(
                new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMap()),
                new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap()),
                new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMap())
        );
        return new Lexer(code, tokenConstructors, keywordConstructor, whiteSpaces);
    }

    private static Parser createParser(List<Token> tokens) {
        List<NodeConstructor> nodeConstructors = getNodeConstructors();
        List<BlockNodeConstructor> blockNodeConstructors = new LinkedList<>();
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        return new Parser(nodeConstructors, blockNodeConstructors, tokenBuffer);
    }

    private static List<NodeConstructor> getNodeConstructors() {
        ExpressionNodeConstructor expressionNodeConstructor = new ExpressionNodeConstructor(listOfOperators(), List.copyOf(PrintScriptTokenConfig.literalTokenTypeMap().values()));
        AssignationNodeConstructor assignationNodeConstructor = new AssignationNodeConstructor(expressionNodeConstructor);
        VariableDeclarationNodeConstructor variableDeclarationNodeConstructor =
                new VariableDeclarationNodeConstructor(expressionNodeConstructor,
                        List.of(NativeTokenTypes.LET.toTokenType()),
                        List.of(NativeTokenTypes.NUMBER_TYPE.toTokenType(), NativeTokenTypes.STRING_TYPE.toTokenType()));

        CallExpressionNodeConstructor callExpressionNodeConstructor = new CallExpressionNodeConstructor(true, expressionNodeConstructor);
        return List.of(
                callExpressionNodeConstructor,
                assignationNodeConstructor,
                variableDeclarationNodeConstructor
        );
    }

    private static List<TokenType> listOfOperators() {
        return List.of(NativeTokenTypes.PLUS.toTokenType(),
                NativeTokenTypes.MINUS.toTokenType(),
                NativeTokenTypes.ASTERISK.toTokenType(),
                NativeTokenTypes.SLASH.toTokenType());
    }


    public static void run(String code) throws Exception {

        Lexer lexer = createLexer(code);
        List<Token> tokens = new ArrayList<>();

        while (lexer.hasNext()){
            Try<Token, Exception> possibleToken = lexer.getNext();
            if (possibleToken.isFail()){
                throw possibleToken.getFail().get();
            }
            else {
                tokens.add(possibleToken.getSuccess().get());
            }
        }

        Parser parser = createParser(tokens);
        Try<ASTNode, Exception> possibleAst = parser.parseExpression();
        if (possibleAst.isFail()){
            throw possibleAst.getFail().get();
        }
        ASTNode ast = possibleAst.getSuccess().get();
        Interpreter interpreter = createInterpreter();
        interpreter.visit((Program) ast);

    }

}
