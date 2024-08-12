package org.example.lexer;



import org.example.lexer.token.NativeTokenTypes;
import org.example.lexer.token.TokenType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PrintScriptTokenConfig {


    public static Map<Pattern, TokenType> keywordTokenTypeMap() {

        NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
                NativeTokenTypes.LET,
                NativeTokenTypes.STRING_TYPE,
                NativeTokenTypes.NUMBER_TYPE};

        return getMapFromArray(typeArray);
    }

    public static Map<Pattern, TokenType> operatorTokenTypeMap() {

        NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
                NativeTokenTypes.EQUALS,
                NativeTokenTypes.PLUS,
                NativeTokenTypes.MINUS,
                NativeTokenTypes.ASTERISK,
                NativeTokenTypes.SLASH};

        return getMapFromArray(typeArray);
    }


    public static Map<Pattern, TokenType> separatorTokenTypeMap() {

        NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
                NativeTokenTypes.COMMA,
                NativeTokenTypes.COLON,
                NativeTokenTypes.SEMICOLON,
                NativeTokenTypes.LEFT_PARENTHESIS,
                NativeTokenTypes.RIGHT_PARENTHESES,
        };

        return getMapFromArray(typeArray);
    }

    public static Map<Pattern, TokenType> literalTokenTypeMap() {
        NativeTokenTypes[] typeArray = new NativeTokenTypes[]{
                NativeTokenTypes.NUMBER,
                NativeTokenTypes.STRING,
                NativeTokenTypes.IDENTIFIER,
        };

        return getMapFromArray(typeArray);
    }

    private static LinkedHashMap<Pattern, TokenType> getMapFromArray(NativeTokenTypes[] typeArray) {
        LinkedHashMap<Pattern, TokenType> map = new LinkedHashMap<>();

        Arrays.stream(typeArray).forEach(x -> map.put(x.getRegex(), x.toTokenType()));
        return map;
    }

}
