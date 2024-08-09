package org.example.nodeconstructors;


import org.example.Parser;

public interface BlockNodeConstructor extends NodeConstructor {
    void acceptParser(Parser parser);
}
