package org.example.lexer;

public class NoMoreTokensAvailableException extends Exception{

    public static final String MESSAGE = "All tokens have been consumed";

    NoMoreTokensAvailableException(){
        super(MESSAGE);
    }
}
