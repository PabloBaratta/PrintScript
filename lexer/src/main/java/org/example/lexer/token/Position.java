package org.example.lexer.token;

public record Position (
         int offset,
         int length,
         int line

){

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getLine() {
        return line;
    }
}
