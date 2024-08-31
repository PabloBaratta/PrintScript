package org.example.lexer;

import java.util.Collection;
import java.util.List;

public class LexerProvider {

	public static Lexer provideV10(String code) {
		List<Character> whiteSpaces = List.of(' ', '\t', '\n');
		TokenConstructor keywordConstructor =
				new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMapV10());
		Collection<TokenConstructor> tokenConstructors = List.of(
				new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMapV10()),
				new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap()),
				new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMapV10())
		);
		return new Lexer(code, tokenConstructors, keywordConstructor, whiteSpaces);
	}

	public static Lexer provideV11(String code) {
		List<Character> whiteSpaces = List.of(' ', '\t', '\n');
		TokenConstructor keywordConstructor =
				new TokenConstructorImpl(PrintScriptTokenConfig.keywordTokenTypeMapV11());
		Collection<TokenConstructor> tokenConstructors = List.of(
				new TokenConstructorImpl(PrintScriptTokenConfig.separatorTokenTypeMapV11()),
				new TokenConstructorImpl(PrintScriptTokenConfig.operatorTokenTypeMap()),
				new TokenConstructorImpl(PrintScriptTokenConfig.literalTokenTypeMapV11())
		);
		return new Lexer(code, tokenConstructors, keywordConstructor, whiteSpaces);
	}

}
