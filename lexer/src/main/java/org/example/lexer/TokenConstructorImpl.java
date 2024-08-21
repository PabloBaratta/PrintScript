package org.example.lexer;

import org.example.lexer.token.Position;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenConstructorImpl implements TokenConstructor {

	private final Map<Pattern, TokenType> map;

	public TokenConstructorImpl(Map<Pattern, TokenType> map){
		this.map = map;
	}

	@Override
	public Optional<Token> constructToken(String code, int offset, int line) {
		for(Map.Entry<Pattern, TokenType> entry : map.entrySet()){
			Pattern key = entry.getKey();
			Matcher matcher = key.matcher(code);
			if (matcher.lookingAt()){
				String chars = matcher.group();
				Position position = new Position(offset, chars.length(), line);
				Token token = new Token(entry.getValue(), chars, position);
				return Optional.of(token);
			}
		}
		return Optional.empty();
	}
}
