package org.example.resources;

import org.example.Parser;
import org.example.TokenBuffer;
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
import java.util.Map;

public class Util {

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
		List<TokenType> operands = List.copyOf(PrintScriptTokenConfig.literalTokenTypeMapV10().values());

		CallExpressionNodeConstructor innerCall = new CallExpressionNodeConstructor(false, null);
		NodeConstructor expCons = new ExpressionNodeConstructor(mapOperatorPrecedence(), operands, innerCall);

		AssignationNodeConstructor assignationNodeConstructor =
				new AssignationNodeConstructor(expCons);
		VariableDeclarationNodeConstructor variableDeclarationNodeConstructor =
				new VariableDeclarationNodeConstructor(expCons,
						List.of(NativeTokenTypes.LET.toTokenType()),
						List.of(NativeTokenTypes.NUMBER_TYPE.toTokenType(),
								NativeTokenTypes.STRING_TYPE.toTokenType()));

		CallExpressionNodeConstructor callExpressionNodeConstructor =
				new CallExpressionNodeConstructor(true, expCons);
		return List.of(
				callExpressionNodeConstructor,
				assignationNodeConstructor,
				variableDeclarationNodeConstructor
		);
	}

	private static Map<TokenType, Integer> mapOperatorPrecedence() {
		return Map.of(NativeTokenTypes.PLUS.toTokenType(), 1,
				NativeTokenTypes.MINUS.toTokenType(), 1,
				NativeTokenTypes.ASTERISK.toTokenType(), 2,
				NativeTokenTypes.SLASH.toTokenType(), 2);
	}

}
