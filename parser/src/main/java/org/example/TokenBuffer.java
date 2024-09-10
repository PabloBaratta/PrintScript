package org.example;



import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import functional.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TokenBuffer {

	private final PrintScriptIterator<Token> tokens;
	private final List<Token> tokenAcc;
	private Token lastToken = null;

	private Exception errorHolderFromPeek = null;

	public TokenBuffer(PrintScriptIterator<Token> tokens) {
		this.tokens = tokens;
		this.tokenAcc = new LinkedList<>();
	}
	/**
	@throws Exception if it cannot process a Token
	*/

	private static Token getTokenFromIterator(PrintScriptIterator<Token> tokens) throws Exception {
		return tokens.hasNext() ? tokens.getNext() : null;
	}

	/**
	@throws Exception if it cannot process a Token or if token is trying to be consumes while there
	is not any more tokens
	 */

	private Token decideToken(PrintScriptIterator<Token> tokens, List<Token> tokensAcc) throws Exception {

		if (!hasAnyTokensLeft()) {
			throw new NoMoreTokensException();
		}

		if (errorHolderFromPeek != null) {
			throw errorHolderFromPeek;
		}

		if (!tokensAcc.isEmpty()) {
			Token first = tokensAcc.getFirst();
			lastToken = first;
			tokensAcc.removeFirst();
			return first;
		}

		Token tokenFromIterator = getTokenFromIterator(tokens);
		lastToken = tokenFromIterator;
		return tokenFromIterator;
	}

	public boolean hasAnyTokensLeft(){
		return !tokenAcc.isEmpty() || tokens.hasNext();
	}

	public static boolean isThisTokenType(Optional<Token> token, List<TokenType> types){
		return token.isPresent() && types.contains(getType(token.get()));
	}

	public static boolean isThisTokenType(Optional<Token> token, TokenType expectedType) {
		return token.isPresent() && getType(token.get()).equals(expectedType);
	}

	public static boolean isThisTokenType(Token token, List<TokenType> types){
		return token != null && types.contains(getType(token));
	}

	public static boolean isThisTokenType(Token token, TokenType expectedType) {
		return  token != null && getType(token).equals(expectedType);
	}

	/**
	 * mutates this
	 * @return a try with the next or an exception.
	 * See errors on
	 * {@link #decideToken(PrintScriptIterator, List)}}
	 */
	public Try<Token> getToken() {
		return Try.of(() -> decideToken(tokens, tokenAcc));
	}

	/**
	 * mutates this
	 * @param type to compare
	 * @return try with token or an error in case the desired type does not match
	 * or happens an error on getToken
	 * {@link #getToken()}
	 */

	public Try<Token> consumeToken(TokenType type) {
		if (!hasAnyTokensLeft()) {
			String error = "Expected in sentence :" + type.name();
			if (lastToken == null) {
				return new Try<>(new NoMoreTokensException());
			}
			return new Try<>(new SemanticErrorException(lastToken, error));
		}
		return getToken().onSuccess((token) -> {
			if (isThisTokenType(token, type)) {
				return token;
			} else {
				throw new SemanticErrorException(token, "Expected " + type.name());
			}
		});
	}

	/**
	 ** mutates this
	 * @param types : List of token types to compare
	 * @return try with token or an error in case the desired type does not match
	 * or happens an error on getToken
	 * {@link #getToken()}
	 */

	public Try<Token> consumeToken(List<TokenType> types) {
		if (!hasAnyTokensLeft()) {
			String error = "Expected in sentence :" + optionsForMethod(types);
			if (lastToken == null) {
				return new Try<>(new NoMoreTokensException());
			}
			return new Try<>(new SemanticErrorException(lastToken, error));
		}

		return getToken().onSuccess((token) -> {
			if (isThisTokenType(token, types)) {
				return token;
			} else {
				String options = optionsForMethod(types);
				throw new SemanticErrorException(token, "Expected " + options);
			}
		});
	}

	public Try<Token> consumeToken(NativeTokenTypes type) {
		return consumeToken(type.toTokenType());
	}

	private static String optionsForMethod(List<TokenType> types) {
		String options = types.stream().map(Record::toString)
				.collect(Collectors.joining("\n"));
		return options;
	}

	/**
	 * peeks a token ahead of not yet consumed token
	 * @return desired token or null if there is not one available
	 * or if that token cannot be produced by iterator due to
	 * fail in that stage
	 */
	private Optional<Token> peekNext() {
		return lookahead(0);
	}

	/**
	 * @param n - 0 index argument number of token to peek
	 * @return asked token or null if there is not a token available
	 * token iterator cannot process a token
	 * or if a token cannot be produced by iterator due to
	 * error in that stage
	 */
	private Optional<Token> lookahead(int n) {
		if (errorHolderFromPeek != null ) {
			return Optional.empty();
		}

		if (n < tokenAcc.size()) {
			return Optional.ofNullable(tokenAcc.get(n));
		}
		int tokensToAsk = n - tokenAcc.size() + 1;

		for (int i = 1; i <= tokensToAsk; i++) {


			Token tokenFromIterator = null;
			try {
				tokenFromIterator = getTokenFromIterator(tokens);
			} catch (Exception e) {
				errorHolderFromPeek = e;
			}

			if (tokenFromIterator == null) {
				break;
			}

			tokenAcc.add(tokenFromIterator);
		}
		return n < tokenAcc.size() ? Optional.ofNullable(tokenAcc.get(n)) : Optional.empty();
	}

	public boolean peekTokenType(TokenType type) {
		return isThisTokenType(peekNext(), type);
	}

	public boolean peekTokenType(NativeTokenTypes type) {
		return isThisTokenType(peekNext(), type.toTokenType());
	}

	public boolean peekTokenType(List<TokenType> types) {
		return isThisTokenType(peekNext(), types);
	}

	public boolean lookaheadType(int n, NativeTokenTypes type) {
		return isThisTokenType(lookahead(n), type.toTokenType());
	}

	public boolean lookaheadType(int n, TokenType type) {
		return isThisTokenType(lookahead(n), type);
	}

	public boolean lookaheadType(int n, List<TokenType> types) {
		return isThisTokenType(lookahead(n), types);
	}

	/**
	 * mutates this -> discards last token
	 * @param type consumes token until that token
	 * @return the list of tokens if it found the desired type, an error if it did not
	 * {@link #getToken()}
	 */

	public Try<List<Token>> consumeUntil(TokenType type) {
		List<Token> tokens = new LinkedList<>();

		while (hasAnyTokensLeft()) {

			Try<Token> token = getToken();

			if (token.isFail()) {
				return new Try<>(token.getFail().get());
			}

			Token actualToken = token.getSuccess().get();

			if (isThisTokenType(actualToken, type)) {
				return new Try<>(tokens);
			}
			else {
				tokens.add(actualToken);
			}
		}


		return Try.of(() -> {
			String error = "Expected in sentence :" + type.name();
			throw new SemanticErrorException(lastToken, error);});
	}

	public Try<List<Token>> consumeUntil(NativeTokenTypes type) {
		return consumeUntil(type.toTokenType());
	}

		private static TokenType getType(Token token) {
		return token.type();
	}
}
