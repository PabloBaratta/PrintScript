package org.example.nodeconstructors;

import org.example.TokenBuffer;

public interface NodeConstructor {
	/**
	Receives a buffer containing the other tokens and returns an object containing
	a new token buffer and a node if it matches the pattern it is supposed to check or an exception
	if has been a syntax error
	 **/
	NodeResponse build(TokenBuffer tokenBuffer);

}
