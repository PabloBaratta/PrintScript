package org.example;

import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.nodeconstructors.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.example.lexer.token.NativeTokenTypes.*;

public class ParserProvider {

	public static Parser provide10(List<Token> tokens) {
		List<NodeConstructor> nodeConstructors = getNodeConstructors10();
		List<BlockNodeConstructor> blockNodeConstructors = new LinkedList<>();
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		return new Parser(nodeConstructors, blockNodeConstructors, tokenBuffer);
	}

	public static Parser provide11(List<Token> tokens) {
		List<TokenType> operands = List.copyOf(PrintScriptTokenConfig.literalTokenTypeMapV11().values());
		List<TokenType> nativeFunctions = List.of(PRINTLN.toTokenType(),
				READENV.toTokenType(),
				READINPUT.toTokenType());
		CallExpressionNodeConstructor innerCall = new CallExpressionNodeConstructor(false,
				null,
				nativeFunctions);
		ExpressionNodeConstructor expCons = new ExpressionNodeConstructor(
				mapOperatorPrecedence(), operands, innerCall);

		AssignationNodeConstructor assignationNodeConstructor =
				new AssignationNodeConstructor(expCons);
		VariableDeclarationNodeConstructor variableDeclarationNodeConstructor =
				new VariableDeclarationNodeConstructor(expCons,
						List.of(LET.toTokenType()),
						List.of(NUMBER_TYPE.toTokenType(),
								STRING_TYPE.toTokenType(),
								BOOLEAN_TYPE.toTokenType()));

		CallExpressionNodeConstructor callExpressionNodeConstructor =
				new CallExpressionNodeConstructor(true, expCons, nativeFunctions);

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
		List<TokenType> nativeFunctions = List.of(PRINTLN.toTokenType());
		CallExpressionNodeConstructor innerCall = new CallExpressionNodeConstructor(
				false, null, nativeFunctions);
		NodeConstructor expCons = new ExpressionNodeConstructor(mapOperatorPrecedence(), operands, innerCall);

		AssignationNodeConstructor assignationNodeConstructor =
				new AssignationNodeConstructor(expCons);
		VariableDeclarationNodeConstructor variableDeclarationNodeConstructor =
				new VariableDeclarationNodeConstructor(expCons,
						List.of(LET.toTokenType()),
						List.of(NUMBER_TYPE.toTokenType(),
								STRING_TYPE.toTokenType()));

		CallExpressionNodeConstructor callExpressionNodeConstructor =
				new CallExpressionNodeConstructor(true, expCons, nativeFunctions);
		return List.of(
				callExpressionNodeConstructor,
				assignationNodeConstructor,
				variableDeclarationNodeConstructor
		);
	}

	private static Map<TokenType, Integer> mapOperatorPrecedence() {
		return Map.of(PLUS.toTokenType(), 1,
				MINUS.toTokenType(), 1,
				ASTERISK.toTokenType(), 2,
				SLASH.toTokenType(), 2);
	}

}
