package org.example;



import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TokenBuffer {

	private final PrintScriptIterator<Token> tokens;
	private final List<Token> tokenAcc;
	private Token token;

	public TokenBuffer(PrintScriptIterator<Token> tokens) throws Exception {
		this.tokens = tokens;
		this.token = tokens.hasNext() ? tokens.getNext() : null;
		this.tokenAcc = new LinkedList<>();
	}

	private static Token getTokenFromIterator(PrintScriptIterator<Token> tokens) throws Exception {
		return tokens.hasNext() ? tokens.getNext() : null;
	}

	private static Token decideToken(PrintScriptIterator<Token> tokens, List<Token> tokensAcc) throws Exception {
		if (!tokensAcc.isEmpty()) {
			Token first = tokensAcc.getFirst();
			tokensAcc.removeFirst();
			return first;
		}
		return getTokenFromIterator(tokens);
	}

	public boolean hasAnyTokensLeft(){
		return token != null;
	}

	public boolean isNextTokenOfType(TokenType expectedType){
		return isThisTokenType(this.token, expectedType);
	}

	public boolean isNextTokenOfAnyOfThisTypes(List<TokenType> possibleTypes){
		return isThisTokenType(this.token, possibleTypes);
	}


	public static boolean isThisTokenType(Token token, List<TokenType> types){
		return token != null && types.contains(getType(token));
	}

	public static boolean isThisTokenType(Token token, TokenType expectedType) {
		return token != null && getType(token).equals(expectedType);
	}

	public Optional<Token> getToken() {
		return Optional.ofNullable(token);
	}

	public TokenBuffer consumeToken() throws Exception {
		this.token = decideToken(tokens, tokenAcc);
		return this;
	}

	public Token peekNext() throws Exception {
		return lookahead(0);
	}
	//zero index
	public Token lookahead(int n) throws Exception {
		if (n < tokenAcc.size()) {
			return tokenAcc.get(n);
		}
		int tokensToAsk = n - tokenAcc.size() + 1;
		for (int i = 1; i <= tokensToAsk; i++) {
			Token tokenFromIterator = getTokenFromIterator(tokens);
			if (tokenFromIterator != null) {
				tokenAcc.add(tokenFromIterator);
			}
		}
		return n < tokenAcc.size() ? tokenAcc.get(n) : null;
	}

	private static TokenType getType(Token token) {
		return token.type();
	}
}
