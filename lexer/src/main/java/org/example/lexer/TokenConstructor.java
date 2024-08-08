package org.example.lexer;

import org.example.Token;

import java.util.Optional;

public interface TokenConstructor {
    Optional<Token> constructToken(String code, int offset);
}
