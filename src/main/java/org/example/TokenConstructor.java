package org.example;

import java.util.Optional;

public interface TokenConstructor {
    Optional<Token> constructToken(String code, int offset);

}
