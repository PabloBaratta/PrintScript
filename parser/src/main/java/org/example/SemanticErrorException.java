package org.example;

import org.token.Token;

public class SemanticErrorException extends Exception{

	public SemanticErrorException(Token token){
		super(getMessage(token)) ;
	}

	private static String getMessage(Token token) {
		int line = token.position().getLine();
		return "Error on:\n\t" + token.associatedString() + "\n in character "
				+ token.position().getColumn()+ "\n on line: " + line;
	}

	public SemanticErrorException(Token token, String message){
		super(getMessage(token) + "\n" + message) ;
	}
}
