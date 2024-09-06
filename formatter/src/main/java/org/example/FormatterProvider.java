package org.example;


import java.io.IOException;
import java.util.List;

public class FormatterProvider {

	public static Formatter provideV10() throws IOException {
		return new Formatter(Ruler.rulesV10(), new PrintScriptIteratorTest<ASTNode>(List.of()));
	}

	public static Formatter provideV11() throws IOException {
		return new Formatter(Ruler.rulesV11(), new PrintScriptIteratorTest<ASTNode>(List.of()));
	}

}
