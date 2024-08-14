package org.example.nodeconstructors;

import org.example.TokenBuffer;

public class ExpressionNodeConstructor implements NodeConstructor {

    @Override
    public NodeConstructionResponse build(TokenBuffer tokenBuffer) {
        return NodeConstructionResponse.response(new Exception("not implemented yet"),
                tokenBuffer);
    }
}
