import org.example.ASTNode;
import org.example.InputProvider;
import org.example.interpreter.*;
import org.example.PrintScriptIterator;
import org.example.PrintScriptIteratorTest;
import org.example.interpreter.handlers.ASTNodeHandler;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterProviderTest {

	@Test
	void testProvideV10() {

		PrintScriptIterator<ASTNode> nodeIterator = new PrintScriptIteratorTest<>(Collections.emptyList());
		InputProvider inputProvider = new MockInputProvider("hola");
		OutputCapture outputCapture = new OutputCapture();
		Interpreter interpreter = InterpreterProvider.provideV10(nodeIterator, inputProvider, outputCapture);

		Map<String, ASTNodeHandler> handlers = interpreter.getHandlers();
		assertNotNull(handlers, "Handlers should not be null.");

	}

	@Test
	void testProvideV11() {

		PrintScriptIterator<ASTNode> nodeIterator = new PrintScriptIteratorTest<>(Collections.emptyList());
		InputProvider inputProvider = new MockInputProvider("chau");

		OutputCapture outputCapture = new OutputCapture();
		Interpreter interpreter = InterpreterProvider.provideV11(nodeIterator, inputProvider, outputCapture);
		Map<String, ASTNodeHandler> handlers = interpreter.getHandlers();
		assertNotNull(handlers, "Handlers should not be null.");

	}
}
