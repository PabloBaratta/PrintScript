package org.example;

import org.example.lexer.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class LexerExceptionTest {

	@Test
	void testUnsupportedCharacterException() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let x = 10; @".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		while (lexer.hasNext()) {
			try {
				lexer.getNext();
			} catch (UnsupportedCharacterException e) {
				String str = "Unrecognized character: @\n";
				String s = "org.example.lexer.UnsupportedCharacterException: " + str;
				String exception = s +
						"\toffset: 12\n" +
						"on line: 1";
				assertEquals(e.toString(), exception);
				return;
			}
		}
	}

	@Test
	void testNoMoreTokensAvailableException() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("let x = 10;".getBytes());
		Lexer lexer = LexerProvider.provideV11(new StreamReader(inputStream));
		while (lexer.hasNext()) {
			lexer.getNext();
		}
		assertThrows(NoMoreTokensAvailableException.class, lexer::getNext);
	}
}
