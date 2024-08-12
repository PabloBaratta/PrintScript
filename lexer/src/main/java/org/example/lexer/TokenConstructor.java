package org.example.lexer;


import org.example.lexer.token.Token;

import java.util.Optional;

public interface TokenConstructor {
    Optional<Token> constructToken(String code, int offset);
}
