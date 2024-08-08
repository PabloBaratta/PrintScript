package org.example.lexer;

public class UnsupportedCharacterException extends Exception {
    UnsupportedCharacterException(char character, int offset){
        super("Unrecognized character: " + character + "\n\t"+
                "offset: " + offset);
    }
}
