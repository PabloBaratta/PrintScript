package org.example.lexer;


import org.token.Token;
import java.util.Optional;

public interface TokenConstructor {
	Optional<Token> constructToken(String code, int offset, int line, int column);
}
