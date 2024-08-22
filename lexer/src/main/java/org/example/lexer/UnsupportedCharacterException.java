package org.example.lexer;

public class UnsupportedCharacterException extends Exception {
	UnsupportedCharacterException(char character, int offset, int line){
		super("Unrecognized character: " + character + "\n\t"+
				"offset: " + offset  + "\non line: " + line);
	}
}
