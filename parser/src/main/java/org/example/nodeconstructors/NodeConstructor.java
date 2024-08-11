package org.example.nodeconstructors;

import org.example.ASTNode;
import org.example.Token;
import org.example.TokenType;
import org.example.lexer.utils.Try;


import java.util.List;
import java.util.Optional;
import java.util.Queue;

public interface NodeConstructor {
    /**
    Receives a buffer containing the other tokens and returns an object containing
    a new token buffer and a node if it matches the pattern it is supposed to check or an exception
    if has been a syntax error
     **/
    NodeConstructionResponse build(TokenBuffer tokenBuffer);

}
