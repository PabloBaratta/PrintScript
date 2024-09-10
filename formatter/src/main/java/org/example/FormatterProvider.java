package org.example;


import java.io.IOException;
import java.util.List;

public class FormatterProvider {

	public static Formatter provideV10(PrintScriptIterator<ASTNode> nodes) throws IOException {
		return new Formatter(Ruler.rulesV10(), nodes);
	}

	public static Formatter provideV11(PrintScriptIterator<ASTNode> nodes) throws IOException {
		return new Formatter(Ruler.rulesV11(), nodes);
	}

}
