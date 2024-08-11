package org.example;
import org.example.Token;


public class SemanticErrorException extends Exception{

    public SemanticErrorException(Token token){
        super(getMessage(token)) ;
    }

    private static String getMessage(Token token) {
        return "Error on:\n\t" + token.associatedString() + "\n in character " + token.offset();
    }

    public SemanticErrorException(Token token, String message){
        super(getMessage(token) + "\n" + message) ;
    }
}
