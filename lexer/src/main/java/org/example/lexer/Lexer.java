package org.example.lexer;

import org.example.lexer.token.Token;
import org.example.lexer.utils.Try;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

public class Lexer {

	private final String code;
	private final Collection<TokenConstructor> tokenConstructors;
	private final TokenConstructor keywordConstructor;
	private final List<Character> whiteSpaces;
	int currentPosition = 0;
	int currentLine = 1;

	public Lexer(String code,
				Collection<TokenConstructor> tokenConstructors,
				TokenConstructor keywordConstructor,
				List<Character> whiteSpaces){
		this.code = code;
		this.tokenConstructors = tokenConstructors;
		this.keywordConstructor = keywordConstructor;
		this.whiteSpaces = whiteSpaces;
	}

	public boolean hasNext(){
		return currentPosition < code.length();
	}

	public Try<Token, Exception> getNext() {

		if (!hasNext()) {
			return new Try<>(new NoMoreTokensAvailableException());
		}

		char currentCharacter = code.charAt(currentPosition);

		skipCharactersFromList(currentCharacter, whiteSpaces);

		Optional<Token> optionalToken = keywordConstructor.constructToken(code.substring(currentPosition), currentPosition, currentLine)
				.or(() -> tokenConstructors.stream()
						.map(constructor -> constructor.constructToken(code.substring(currentPosition), currentPosition, currentLine))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.max(Comparator.comparingInt(Token::length)));

		if (optionalToken.isPresent()) {
			Token token = optionalToken.get();
			setCurrentPosition(currentPosition + token.position().getLength());
			return new Try<>(token);
		}

		UnsupportedCharacterException lexicalError = new UnsupportedCharacterException(currentCharacter, currentPosition, currentLine);

		return new Try<>(lexicalError);
	}

/*    private void skipCharactersFromList(char currentCharacter, List<Character> characters) {

		while (characters.contains(currentCharacter) && hasNext()) {
			setCurrentPosition(currentPosition + 1);
			if (hasNext()) {
				currentCharacter = code.charAt(currentPosition);
			}
		}
	}*/

/*    private void setCurrentPosition(int newPosition){
		currentPosition = newPosition;
	}*/

	private void skipCharactersFromList(char currentCharacter, List<Character> characters) {

		while (characters.contains(currentCharacter) && hasNext()) {
			setCurrentPosition(currentPosition + 1);
			if (hasNext()) {
				currentCharacter = code.charAt(currentPosition);
			}
		}
	}


	private void setCurrentPosition(int newPosition) {
		while (currentPosition < newPosition) {
			char currentChar = code.charAt(currentPosition);
			if (currentChar == '\n') {
				currentLine++;
			}
			currentPosition++;
		}
	}

}
