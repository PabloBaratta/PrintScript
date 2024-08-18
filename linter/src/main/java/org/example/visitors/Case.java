package org.example.visitors;


import java.util.regex.Pattern;

public enum Case {
    CAMEL_CASE ("[a-zA-Z_][a-z0-9_]*([A-Z][a-zA-Z0-9_]*)*"), SNAKE_CASE("[a-z0-9_]+(_[a-z0-9_]+)*");

    private final String regex;

    Case(String regex){
        this.regex = regex;
    }


    public Pattern getRegex() {
        return Pattern.compile("^" + regex);
    }

}
