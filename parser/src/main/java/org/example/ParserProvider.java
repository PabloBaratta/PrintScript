package org.example;

import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.nodeconstructors.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParserProvider {

	public static Parser provide10(List<Token> tokens) {
		List<NodeConstructor> nodeConstructors = getNodeConstructors10();
		List<BlockNodeConstructor> blockNodeConstructors = new LinkedList<>();
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		return new Parser(nodeConstructors, blockNodeConstructors, tokenBuffer);
	}

	public static Parser provide11(List<Token> tokens) {
		List<TokenType> operands = List.copyOf(PrintScriptTokenConfig.literalTokenTypeMapV11().values());

		CallExpressionNodeConstructor innerCall = new CallExpressionNodeConstructor(false, null);
		ExpressionNodeConstructor expCons = new ExpressionNodeConstructor(
				mapOperatorPrecedence(), operands, innerCall);

		AssignationNodeConstructor assignationNodeConstructor =
				new AssignationNodeConstructor(expCons);
		VariableDeclarationNodeConstructor variableDeclarationNodeConstructor =
				new VariableDeclarationNodeConstructor(expCons,
						List.of(NativeTokenTypes.LET.toTokenType()),
						List.of(NativeTokenTypes.NUMBER_TYPE.toTokenType(),
								NativeTokenTypes.STRING_TYPE.toTokenType(),
								NativeTokenTypes.BOOLEAN_TYPE.toTokenType()));

		CallExpressionNodeConstructor callExpressionNodeConstructor =
				new CallExpressionNodeConstructor(true, expCons);

		List<NodeConstructor> constructors = List.of(
				callExpressionNodeConstructor,
				assignationNodeConstructor,
				variableDeclarationNodeConstructor);


		List<BlockNodeConstructor> blockNodeConstructors = new LinkedList<>(
								List.of(new IfNodeConstructor(expCons)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		return new Parser(constructors, blockNodeConstructors, tokenBuffer);
	}


	private static List<NodeConstructor> getNodeConstructors10() {
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
