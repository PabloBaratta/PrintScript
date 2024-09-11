package org.example;


import java.io.IOException;
import java.util.List;

public class FormatterProvider {

	public static Formatter provideV10(PrintScriptIterator<ASTNode> nodes, String rules) throws IOException {
		return new Formatter(Ruler.readRulesFromJson(rules), nodes);
	}

	public static Formatter provideV11(PrintScriptIterator<ASTNode> nodes, String rules) throws IOException {
		return new Formatter(Ruler.readRulesFromJson(rules), nodes);
	}

}
