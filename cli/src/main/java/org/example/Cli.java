package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;
import org.example.nodeconstructors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Cli {

    private final String code;

    public Cli(String code) {
        this.code = code;
    }

    private Interpreter createInterpreter() {
        return new Interpreter();
    }

    private Lexer createLexer() {
        List<Character> whiteSpaces = List.of(' ', '\t', '\n', '\r');
        TokenConstructor keywordConstructor = new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMap());
        Collection<TokenConstructor> tokenConstructors = List.of(
                new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMap()),
                new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap())
        );
        return new Lexer(this.code, tokenConstructors, keywordConstructor, whiteSpaces);
    }

    private Parser createParser(List<Token> tokens, Lexer lexer) {
        List<NodeConstructor> nodeConstructors = getNodeConstructors();
        List<BlockNodeConstructor> blockNodeConstructors = List.of();
        TokenBuffer tokenBuffer = new TokenBuffer(tokens); // Assuming TokenBuffer takes a Lexer
        return new Parser(nodeConstructors, blockNodeConstructors, tokenBuffer);
    }

    private static List<NodeConstructor> getNodeConstructors() {
        ExpressionNodeConstructor expressionNodeConstructor = new ExpressionNodeConstructor();
        AssignationNodeConstructor assignationNodeConstructor = new AssignationNodeConstructor(expressionNodeConstructor);
        VariableDeclarationNodeConstructor variableDeclarationNodeConstructor = new VariableDeclarationNodeConstructor();
        return List.of(
                expressionNodeConstructor,
                assignationNodeConstructor,
                variableDeclarationNodeConstructor
        );
    }

    public void run() throws Exception {

        Lexer lexer = createLexer();
        List<Token> tokens = new ArrayList<>();

        while (lexer.hasNext()){
            Try<Token, Exception> possibleToken = lexer.getNext();
            if (possibleToken.isFail()){
                throw possibleToken.getFail().get();
            }
            else {
                tokens.add(lexer.getNext().getSuccess().get());
            }
        }

        Parser parser = createParser(tokens, lexer);
        Try<ASTNode, Exception> possibleAst = parser.parseExpression();
        if (possibleAst.isFail()){
            throw possibleAst.getFail().get();
        }
        ASTNode ast = possibleAst.getSuccess().get();
        Interpreter interpreter = createInterpreter();
        interpreter.visit(ast);

    }

}
