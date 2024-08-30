package org.example;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.lexer.utils.Try;
import org.example.nodeconstructors.CallExpressionNodeConstructor;
import org.example.nodeconstructors.ExpressionNodeConstructor;
import org.example.nodeconstructors.NodeConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ParserTest {
	@Test
	public void testSimpleAddExpression(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		expressions.add(NativeTokenTypes.STRING.toTokenType());
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		tokens.add(new Token(NativeTokenTypes.PLUS.toTokenType(), "+", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "2", new Position(3, 1, 1, 3)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("1.0 + 2.0", node.toString());
	}

	@Test
	public void testNumberStringOperatorCombination(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		expressions.add(NativeTokenTypes.STRING.toTokenType());
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		tokens.add(new Token(NativeTokenTypes.PLUS.toTokenType(), "+", new Position(2, 1, 1, 2)));
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "2", new Position(3, 1, 1, 3)));
		tokens.add(new Token(NativeTokenTypes.ASTERISK.toTokenType(), "*", new Position(4, 1, 1, 4)));
		tokens.add(new Token(NativeTokenTypes.STRING.toTokenType(), "\"hola\"", new Position(5, 6, 1, 5)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("1.0 + 2.0 * hola", node.toString());
	}

	@Test
	public void testOnlyNumber(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		expressions.add(NativeTokenTypes.STRING.toTokenType());
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(1, 1, 1, 1)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("1.0", node.toString());
	}

	@Test
	public void testOnlyString(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		TokenType str = NativeTokenTypes.STRING.toTokenType();
		expressions.add(str);
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		Position pos = new Position(1, 20, 1, 1);
		tokens.add(new Token(str, "\"hola buenas tardes\"", pos));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("hola buenas tardes", node.toString());
	}

	private static ExpressionNodeConstructor getExpressionNodeConstructor(List<TokenType> expressions) {
		CallExpressionNodeConstructor callExpression = new CallExpressionNodeConstructor(false, null);
		return new ExpressionNodeConstructor(mapOperatorPrecedence(), expressions, callExpression);
	}

	@Test
	public void testOnlyIdentifier(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		expressions.add(NativeTokenTypes.STRING.toTokenType());
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
		list.add(getExpressionNodeConstructor(expressions));
		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NativeTokenTypes.IDENTIFIER.toTokenType(), "si", new Position(1, 2, 1, 1)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("si", node.toString());
	}

	@Test
	public void testVariableDeclarationWithParentheses() {

		List<TokenType> expressions = new ArrayList<>();

		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		expressions.add(NativeTokenTypes.STRING.toTokenType());
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());

		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 17, 6)));
		tokens.add(new Token(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 17, 6)));
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(7, 1, 17, 7)));
		TokenType rightPar = NativeTokenTypes.RIGHT_PARENTHESES.toTokenType();
		tokens.add(new Token(rightPar, ")", new Position(10, 1, 17, 10)));

		tokens.add(new Token(NativeTokenTypes.PLUS.toTokenType(), "+", new Position(8, 1, 17, 8)));
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "2", new Position(9, 1, 17, 9)));
		tokens.add(new Token(rightPar, ")", new Position(10, 1, 17, 10)));

		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		ExpressionNodeConstructor e1 = getExpressionNodeConstructor(expressions);
		Parser parser = new Parser(new LinkedList<>(List.of(e1)), new ArrayList<>(), tokenBuffer);

		// Parsear la expresión
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();

		// Verifica que la expresión se haya parseado correctamente
		Assertions.assertEquals("((1.0) + 2.0)", node.toString());

	}


	private static Map<TokenType, Integer> mapOperatorPrecedence() {
		return Map.of(NativeTokenTypes.PLUS.toTokenType(), 1,
				NativeTokenTypes.MINUS.toTokenType(), 1,
				NativeTokenTypes.ASTERISK.toTokenType(), 2,
				NativeTokenTypes.SLASH.toTokenType(), 2);
	}


}
