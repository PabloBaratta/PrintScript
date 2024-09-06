package org.example.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StreamReader implements Iterator<String> {

	private final BufferedReader reader;
	private String nextLine;

	public StreamReader(InputStream inputStream) {
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		advance();
	}

	private void advance() {
		try {
			nextLine = reader.readLine();
		} catch (IOException e) {
			nextLine = null;
			try {
				reader.close();
			} catch (IOException ex) {
				// Ignore
			}
		}
	}

	@Override
	public boolean hasNext() {
		return nextLine != null;
	}

	@Override
	public String next() {
		if (nextLine == null) {
			throw new NoSuchElementException();
		}
		String line = nextLine;
		advance();
		return line;
	}
}
