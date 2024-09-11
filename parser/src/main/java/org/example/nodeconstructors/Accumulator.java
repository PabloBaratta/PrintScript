package org.example.nodeconstructors;

import org.example.PrintScriptIterator;
import org.token.Token;

import java.util.Iterator;
import java.util.List;

public class Accumulator implements PrintScriptIterator<Token> {

	private final Iterator<Token> iterator;

	public Accumulator(List<Token> tokenList) {
	this.iterator = tokenList.iterator();
	}


	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Token getNext() throws Exception {
		return iterator.next();
	}
}
