package org.example.resources;

import org.example.Parser;
import org.example.ParserProvider;
import org.example.TokenBuffer;
import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.PrintScriptTokenConfig;
import org.example.lexer.TokenConstructor;
import org.example.lexer.TokenConstructorImpl;
import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.Token;
import org.example.lexer.token.TokenType;
import org.example.nodeconstructors.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Util {

	public static Parser createParser(List<Token> tokens) {
		return ParserProvider.provide10(tokens);
	}

	public static Interpreter createInterpreter() {
		return new Interpreter();
	}

}
