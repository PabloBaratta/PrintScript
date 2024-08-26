package org.example.lexer;

import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

public class Lexer {

	private final String code;
	private final Collection<TokenConstructor> tokConstr;
	private final TokenConstructor keyConstr;
	private final List<Character> whiteSpaces;
	int pos = 0;
	int line = 1;
	int column = 0;

	public Lexer(String code,
				Collection<TokenConstructor> tokConstr,
				TokenConstructor keyConstr,
				List<Character> whiteSpaces){
		this.code = code;
		this.tokConstr = tokConstr;
		this.keyConstr = keyConstr;
		this.whiteSpaces = whiteSpaces;
	}

	public boolean hasNext(){
		return pos < code.length();
	}

	public Try<Token, Exception> getNext() {

		if (!hasNext()) {
			return new Try<>(new NoMoreTokensAvailableException());
		}

		char currentCharacter = code.charAt(pos);

		skipCharactersFromList(currentCharacter, whiteSpaces);

		Optional<Token> op = keyConstr.constructToken(code.substring(pos), pos, line, column)
				.or(() -> tokConstr.stream()
						.map(c -> c.constructToken(code.substring(pos), pos, line, column))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.max(Comparator.comparingInt(Token::length)));

		if (op.isPresent()) {
			Token token = op.get();
			setPos(pos + token.position().getLength());
			return new Try<>(token);
		}

		UnsupportedCharacterException l = new UnsupportedCharacterException(currentCharacter, pos, line);

		return new Try<>(l);

	}

	private void skipCharactersFromList(char currentCharacter, List<Character> characters) {

		while (characters.contains(currentCharacter) && hasNext()) {
			setPos(pos + 1);
			if (hasNext()) {
				currentCharacter = code.charAt(pos);
			}
		}
	}


	private void setPos(int newPosition) {
		while (pos < newPosition) {
			char currentChar = code.charAt(pos);
			if (currentChar == '\n') {
				line++;
				column = 0;
			}
			else {
				column++;
			}
			pos++;

		}
	}

}
