package org.example.lexer;

import org.example.PrintScriptIterator;
import org.example.lexer.token.Token;

import java.util.*;

public class Lexer implements PrintScriptIterator<Token> {

	private final Iterator<String> reader;
	private final Collection<TokenConstructor> constructors;
	private final TokenConstructor keywords;
	private final List<Character> whiteSpaces;
	private String currentLine;
	private int offset = 0;
	private int line = 1;

	public Lexer(Iterator<String> reader,
				Collection<TokenConstructor> constructors,
				TokenConstructor keywords,
				List<Character> whiteSpaces) {
		this.reader = reader;
		this.constructors = constructors;
		this.keywords = keywords;
		this.whiteSpaces = whiteSpaces;
		this.currentLine = reader.hasNext() ? reader.next() : "";
	}

	@Override
	public boolean hasNext() {


		if ((currentLine.isEmpty() || currentLine.length() == offset) && !reader.hasNext()) {
			return false;
		}

		if (currentLine.length() == offset) {
			currentLine = reader.next();
			skipWhiteSpace();
			line++;
			offset = 0;
		}

		// If the current line is empty but there are more lines to read, advance to the next line
		while (reader.hasNext() && currentLine.isEmpty()) {
			currentLine = reader.next();
			skipWhiteSpace();
			line++;
		}

		// Return true if the current line is not empty
		return !currentLine.isEmpty();
	}

	@Override
	public Token getNext() throws Exception {

		if (!hasNext()){
			throw new NoMoreTokensAvailableException();
		}
		skipWhiteSpace();

		char currentCharacter = currentLine.charAt(offset);

		Optional<Token> optionalToken = getOptionalToken();

		if (optionalToken.isPresent()) {
			Token token = optionalToken.get();
			setOffset(offset + token.position().getLength());
			return token;
		}

		throw new UnsupportedCharacterException(currentCharacter, offset, line);
	}

	private Optional<Token> getOptionalToken() {
		String s = currentLine.substring(offset);
		return keywords.constructToken(s, offset, line, offset + 1)
				.or(() -> constructors.stream()
						.map(c -> c.constructToken(s, offset, line, offset + 1))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.max(Comparator.comparingInt(Token::length)));
	}
/*
	private void checkEndOfLine() throws NoMoreTokensAvailableException {
		if (currentLine.length() == offset && reader.hasNext()) {
			currentLine = reader.next();
			offset = 0;
			line++;
		}
		else if (currentLine.length() == offset && !reader.hasNext()) {
			throw new NoMoreTokensAvailableException();
		}
	}
*/
	private void skipWhiteSpace() {
		while (offset < currentLine.length() && whiteSpaces.contains(currentLine.charAt(offset))) {
			offset++;
		}
	}

	private void setOffset(int newPosition) {
		while (offset < newPosition && offset < currentLine.length()) {
			offset++;
		}
	}
}
