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
import java.util.LinkedList;
import java.util.List;


public class ParserTest {
	@Test
	public void testSimpleAddExpression(){
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
		Assertions.assertEquals("1.0 + 2.0", node.toString());
	}

	@Test
	public void testNumberStringOperatorCombination(){
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
		tokens.add(new Token(NativeTokenTypes.STRING.toTokenType(), "\"hola\"", new Position(5, 6, 1)));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("1.0 + 2.0 * hola", node.toString());
	}

	@Test
	public void testOnlyNumber(){
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
	public void testOnlyString(){
		List<NodeConstructor> list = new ArrayList<>();
		List<TokenType> operators = new ArrayList<>();
		List<TokenType> expressions = new ArrayList<>();
		operators.add(NativeTokenTypes.PLUS.toTokenType());
		operators.add(NativeTokenTypes.MINUS.toTokenType());
		operators.add(NativeTokenTypes.ASTERISK.toTokenType());
		operators.add(NativeTokenTypes.SLASH.toTokenType());
		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		TokenType str = NativeTokenTypes.STRING.toTokenType();
		expressions.add(str);
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());
		list.add(new ExpressionNodeConstructor(operators, expressions));
		List<Token> tokens = new ArrayList<>();
		Position pos = new Position(1, 20, 1);
		tokens.add(new Token(str, "\"hola buenas tardes\"", pos));
		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		Parser parser = new Parser(list, new ArrayList<>(), tokenBuffer);
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();
		Assertions.assertEquals("hola buenas tardes", node.toString());
	}

	@Test
	public void testOnlyIdentifier(){
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

	@Test
	public void testVariableDeclarationWithParentheses() {

		List<TokenType> operators = listOfOperators();
		List<TokenType> expressions = new ArrayList<>();

		expressions.add(NativeTokenTypes.NUMBER.toTokenType());
		expressions.add(NativeTokenTypes.STRING.toTokenType());
		expressions.add(NativeTokenTypes.IDENTIFIER.toTokenType());

		List<Token> tokens = new ArrayList<>();
		tokens.add(new Token(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 17)));
		tokens.add(new Token(NativeTokenTypes.LEFT_PARENTHESIS.toTokenType(), "(", new Position(6, 1, 17)));
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "1", new Position(7, 1, 17)));
		tokens.add(new Token(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType(), ")", new Position(10, 1, 17)));

		tokens.add(new Token(NativeTokenTypes.PLUS.toTokenType(), "+", new Position(8, 1, 17)));
		tokens.add(new Token(NativeTokenTypes.NUMBER.toTokenType(), "2", new Position(9, 1, 17)));
		tokens.add(new Token(NativeTokenTypes.RIGHT_PARENTHESES.toTokenType(), ")", new Position(10, 1, 17)));

		TokenBuffer tokenBuffer = new TokenBuffer(tokens);
		ExpressionNodeConstructor e1 = new ExpressionNodeConstructor(operators, expressions);
		Parser parser = new Parser(new LinkedList<>(List.of(e1)), new ArrayList<>(), tokenBuffer);

		// Parsear la expresión
		Try<ASTNode, Exception> res = parser.parseExpression();
		ASTNode node = res.getSuccess().get();

		// Verifica que la expresión se haya parseado correctamente
		Assertions.assertEquals("((1.0) + 2.0)", node.toString());

	}

	private static List<TokenType> listOfOperators() {
		return List.of(NativeTokenTypes.PLUS.toTokenType(),
				NativeTokenTypes.MINUS.toTokenType(),
				NativeTokenTypes.ASTERISK.toTokenType(),
				NativeTokenTypes.SLASH.toTokenType());
	}


}
