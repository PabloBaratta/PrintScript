package org.example.resources;

import org.example.Parser;
import org.example.ParserProvider;
import org.example.PrintScriptIterator;
import org.example.interpreter.Interpreter;
import org.token.NativeTokenTypes;
import org.token.Token;
import org.token.TokenType;

public class Util {

	public static Parser createParser(PrintScriptIterator<Token> tokens) throws Exception {
		return ParserProvider.provide10(tokens);
	}

	public static Interpreter createInterpreter() {
		return new Interpreter();
	}

}
