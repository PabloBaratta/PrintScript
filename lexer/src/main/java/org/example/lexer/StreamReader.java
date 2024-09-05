/*
package org.example.lexer;

import java.io.InputStream;
import java.util.Iterator;

public class StreamReader implements Iterator<Character> {

	private final InputStream stream;

	public StreamReader(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public boolean hasNext() {
		return stream != null;
	}

	@Override
	public Character next() {
		if (hasNext()) {
			try {
				return (char) stream.read();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
}
*/
