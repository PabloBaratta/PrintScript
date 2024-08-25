package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.nodeconstructors.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Util {
	public static Lexer createLexer(String code) {
		List<Character> whiteSpaces = List.of(' ', '\t', '\n');
		TokenConstructor keywordConstructor =
				new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMap());
		Collection<TokenConstructor> tokenConstructors = List.of(
				new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMap()),
				new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap()),
				new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMap())
		);
		return new Lexer(code, tokenConstructors, keywordConstructor, whiteSpaces);
	}

	public static Parser createParser(List<Token> tokens) {
		List<NodeConstructor> nodeConstructors = getNodeConstructors();
		List<BlockNodeConstructor> blockNodeConstructors = new LinkedList<>();
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		return new Parser(nodeConstructors, blockNodeConstructors, tokenBuffer);
	}

	public static Interpreter createInterpreter() {
		return new Interpreter();
	}

	private static List<NodeConstructor> getNodeConstructors() {
		ExpressionNodeConstructor expressionNodeConstructor =
				new ExpressionNodeConstructor(listOfOperators(),
						List.copyOf(PrintScriptTokenConfig.literalTokenTypeMap().values()));
		AssignationNodeConstructor assignationNodeConstructor =
				new AssignationNodeConstructor(expressionNodeConstructor);
		VariableDeclarationNodeConstructor variableDeclarationNodeConstructor =
				new VariableDeclarationNodeConstructor(expressionNodeConstructor,
						List.of(NativeTokenTypes.LET.toTokenType()),
						List.of(NativeTokenTypes.NUMBER_TYPE.toTokenType(),
								NativeTokenTypes.STRING_TYPE.toTokenType()));

		CallExpressionNodeConstructor callExpressionNodeConstructor =
				new CallExpressionNodeConstructor(true, expressionNodeConstructor);
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
}
