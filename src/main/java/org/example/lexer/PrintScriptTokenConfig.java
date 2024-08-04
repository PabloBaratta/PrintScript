package org.example.lexer;

import org.example.NativeTokenTypes;
import org.example.TokenType;

import java.util.Map;
import java.util.regex.Pattern;

public class PrintScriptTokenConfig {


    static Map<Pattern, TokenType> keywordTokenTypeMap() {
        return Map.of(NativeTokenTypes.LET.getRegex(), NativeTokenTypes.LET.toTokenType(),
                NativeTokenTypes.STRING_TYPE.getRegex(), NativeTokenTypes.STRING_TYPE.toTokenType(),
                NativeTokenTypes.NUMBER_TYPE.getRegex(), NativeTokenTypes.NUMBER_TYPE.toTokenType());
    }

    static Map<Pattern, TokenType> operatorTokenTypeMap() {
        return Map.of(NativeTokenTypes.EQUALS.getRegex(), NativeTokenTypes.EQUALS.toTokenType(),
                NativeTokenTypes.PLUS.getRegex(), NativeTokenTypes.PLUS.toTokenType(),
                NativeTokenTypes.MINUS.getRegex(), NativeTokenTypes.MINUS.toTokenType(),
                NativeTokenTypes.ASTERISK.getRegex(), NativeTokenTypes.ASTERISK.toTokenType(),
                NativeTokenTypes.SLASH.getRegex(), NativeTokenTypes.SLASH.toTokenType());
    }


}
