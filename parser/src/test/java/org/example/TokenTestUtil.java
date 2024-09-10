package org.example;

import org.token.NativeTokenTypes;
import org.token.Position;
import org.token.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TokenTestUtil {

	public static List<Token> getTokens(NativeTokenTypes[] nativeTokenTypes) {
		List<Token> tokens = new LinkedList<>();

		Arrays.stream(nativeTokenTypes).forEach(type -> tokens.add(getaTokenFromTokenType(type)));
		return tokens;
	}

	public static Token getaTokenFromTokenType(NativeTokenTypes tokenType) {
		String associatedString = "";
		if (tokenType.equals(NativeTokenTypes.STRING)) {
			associatedString = "\"value\"";
		} else if (tokenType.equals(NativeTokenTypes.BOOLEAN)) {
			associatedString = "true";
		} else if (tokenType.equals(NativeTokenTypes.NUMBER)) {
		associatedString = "1";
		}
		return new Token(tokenType.toTokenType(), associatedString, new Position(0, 0, 0, 0));
	}

	public static Token getaTokenFromTokenType(NativeTokenTypes tokenType, String associatedString) {
		return new Token(tokenType.toTokenType(), associatedString, new Position(0, 0, 0, 0));
	}
}
