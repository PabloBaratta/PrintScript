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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.lexer.token.NativeTokenTypes.*;

public class Runner {

	private static Interpreter createInterpreter() {
		return new Interpreter();
	}

	private static Lexer createLexer(String code) {
		List<Character> whiteSpaces = List.of(' ', '\t', '\n');
		Map<Pattern, TokenType> tokenConfig = PrintScriptTokenConfig.keywordTokenTypeMap();
		TokenConstructor keywordConstructor = new TokenConstructorImpl(tokenConfig);
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
		List<TokenType> expressions = List.copyOf(PrintScriptTokenConfig.literalTokenTypeMap().values());
		ExpressionNodeConstructor expCons = new ExpressionNodeConstructor(mapOperatorPrecedence(), expressions);
		AssignationNodeConstructor assignationConstructor = new AssignationNodeConstructor(expCons);
		VariableDeclarationNodeConstructor variableDeclarationConstructor =
				new VariableDeclarationNodeConstructor(expCons,
						List.of(LET.toTokenType()),
						List.of(NUMBER_TYPE.toTokenType(), STRING_TYPE.toTokenType()));

		NodeConstructor callExpressionConstructor = new CallExpressionNodeConstructor(true, expCons);
		return List.of(
				callExpressionConstructor,
				assignationConstructor,
				variableDeclarationConstructor
		);
	}

	private static Map<TokenType, Integer> mapOperatorPrecedence() {
		return Map.of(NativeTokenTypes.PLUS.toTokenType(), 1,
				NativeTokenTypes.MINUS.toTokenType(), 1,
				NativeTokenTypes.ASTERISK.toTokenType(), 2,
				NativeTokenTypes.SLASH.toTokenType(), 2);
	}


	public static void run(String filePath) throws Exception {
		String code = readFileAsString(filePath);

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

	public static String readFileAsString(String filePath) throws IOException {
		String content = "";
		content = Files.lines(Paths.get(filePath))
				.collect(Collectors.joining("\n"));
		return content;
	}

}
