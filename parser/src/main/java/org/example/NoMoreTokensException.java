package org.example;

public class NoMoreTokensException extends Exception{

	public NoMoreTokensException() {
		super("Expected more tokens to construct a valid structure");
	}
}
