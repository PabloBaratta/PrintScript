package org.example.parser.nodeconstructors;

import org.example.parser.Parser;

public interface BlockNodeConstructor extends NodeConstructor {
    void acceptParser(Parser parser);
}
