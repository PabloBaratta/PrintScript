import org.example.*;
import org.example.interpreter.*;
import org.example.interpreter.handlers.ASTNodeHandler;
import org.example.interpreter.handlers.HandlerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.token.Position;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	Position pos = new Position(0,0,0,0);

	Map<String, ASTNodeHandler> handlers = HandlerFactory.createHandlers("1.1");
	Map<String, ASTNodeHandler> handlers10 = HandlerFactory.createHandlers("1.0");
	Map<String, ASTNodeHandler> handlers0 = new HashMap<>();
	InputProvider inputProvider = new MockInputProvider("");
	OutputCapture outputCapture = new OutputCapture();

	@Test
	void testVariableDeclaration() throws Exception {
		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		assertTrue(interpreter.getExecutorEnvironment().containsKey("a"));
		assertEquals(interpreter.getExecutorEnvironment().get("a").getType().getTypeName(), "string");
		assertTrue(interpreter.getExecutorEnvironment().get("a").getLiteral().isEmpty());
	}

	@Test
	void testVariableDeclarationMismatchVersionExe() throws Exception {
		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Variable declaration", e.getMessage());
		}
	}

	@Test
	void testVariableDeclarationMismatchVersionVal() throws Exception {
		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Variable declaration", e.getMessage());
		}
	}

	@Test
	void testVariableDeclarationValidation() throws Exception {

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		assertDoesNotThrow(() -> interpreter.validate());

	}

	@Test
	void testVariableDeclarationWithAssignment() throws Exception {
		// let a: string = "hola";
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		Expression expression = new TextLiteral("hola", new Position(0, 0,0, 0));
		VariableDeclaration variableDecl = new VariableDeclaration(identifier, type, Optional.of(expression));

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDecl);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		assertTrue(interpreter.getExecutorEnvironment().containsKey("a"));
		Optional<Literal> optionalExpression = interpreter.getExecutorEnvironment().get("a").getLiteral();

		assertTrue(optionalExpression.isPresent());
		assertEquals("hola", optionalExpression.get().getValue());
	}

	@Test
	void testVariableDeclarationWithAssignmentAndTypeMismatch() throws Exception {
		// let a: string = 42;
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(42), new Position(0, 0,0, 0));
		VariableDeclaration variableDecl = new VariableDeclaration(identifier, type, Optional.of(expression));

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDecl);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.execute();
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVariableRedeclarationThrowsError() throws Exception {
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(variableDeclaration);

		astNodes.add(new VariableDeclaration(identifier, type, Optional.empty()));

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Variable already declared", exception.getMessage());
	}

	// ASSIGNATION ------------------------------------------------------------------------

	@Test
	void testAssignationVariableNotDeclared() {

		// a = "hola";
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Expression expression = new TextLiteral("hola", new Position(0, 0,0, 0));
		Assignation assignation = new Assignation(identifier, expression, new Position(0, 0,0, 0));
		List<ASTNode> astNodes = new ArrayList<>();

		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Variable not declared", exception.getMessage());
	}

	@Test
	void testAssignationToExistingVariable() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		astNodes.add(variableDeclaration);

		Expression expression = new TextLiteral("hola", pos);
		Assignation assignation = new Assignation(identifier, expression, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		Optional<Literal> optionalExpression = interpreter.getExecutorEnvironment().get("a").getLiteral();
		assertTrue(optionalExpression.isPresent());

		assertEquals("hola", optionalExpression.get().getValue());
	}

	@Test
	void testAssignationMismatchVersionVal() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		Expression expression = new TextLiteral("hola", pos);
		Assignation assignation = new Assignation(identifier, expression, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Assignation", e.getMessage());
		}
	}

	@Test
	void testAssignationMismatchVersionExe() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		Expression expression = new TextLiteral("hola", pos);
		Assignation assignation = new Assignation(identifier, expression, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Assignation", e.getMessage());
		}
	}

	@Test
	void testAssignationWithTypeMismatch() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		astNodes.add(variableDeclaration);

		Expression expression = new NumericLiteral(BigDecimal.valueOf(42), pos);
		Assignation assignation = new Assignation(identifier, expression, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	// BINARY EXPRESSION ----------------------------------------------------------------------------------

	@Test
	void testAssignationWithExpressionTwoPlusThree() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("result", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		astNodes.add(variableDeclaration);

		Expression left = new NumericLiteral(BigDecimal.valueOf(2), pos);
		Expression right = new NumericLiteral(BigDecimal.valueOf(3), pos);
		BinaryExpression expression = new BinaryExpression(left, "+", right);
		Assignation assignation = new Assignation(identifier, expression, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		Optional<Literal> optionalExpression = interpreter.getExecutorEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());
		assertEquals(BigDecimal.valueOf(5), optionalExpression.get().getValue());
	}

	@Test
	void testBinaryExpressionMismatchVersion() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("result", pos);

		Expression left = new NumericLiteral(BigDecimal.valueOf(2), pos);
		Expression right = new NumericLiteral(BigDecimal.valueOf(3), pos);
		BinaryExpression expression = new BinaryExpression(left, "+", right);
		astNodes.add(expression);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Binary expression", e.getMessage());
		}
	}

	@Test
	void testBinaryExpressionMismatchVersionVal() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("result", pos);

		Expression left = new NumericLiteral(BigDecimal.valueOf(2), pos);
		Expression right = new NumericLiteral(BigDecimal.valueOf(3), pos);
		BinaryExpression expression = new BinaryExpression(left, "+", right);
		astNodes.add(expression);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Binary expression", e.getMessage());
		}
	}

	@Test
	void testAssignationWithStringAndNumberAddition() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Position pos = new Position(0, 0, 0, 0);
		Identifier stringIdentifier = new Identifier("a", pos);
		Type stringType = new Type("string", pos);
		VariableDeclaration stringDec = new VariableDeclaration(stringIdentifier, stringType, Optional.empty());
		astNodes.add(stringDec);

		TextLiteral text = new TextLiteral("Hello ", pos);
		Assignation stringAssignation = new Assignation(stringIdentifier, text, pos);
		astNodes.add(stringAssignation);

		Identifier resId = new Identifier("result", pos);
		VariableDeclaration res = new VariableDeclaration(resId, stringType, Optional.empty());
		astNodes.add(res);

		BinaryExpression ex = new BinaryExpression(new Identifier("a", pos), "+",
				new NumericLiteral(BigDecimal.valueOf(2), pos));
		Assignation assignation = new Assignation(resId, ex, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();


		Optional<Literal> optionalExpression = interpreter.getExecutorEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());
		assertEquals("Hello 2", optionalExpression.get().getValue());
	}

	@Test
	void testVariableDeclarationWithExpressionDivision() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier resId = new Identifier("result", pos);
		Type numType = new Type("number", pos);
		NumericLiteral num = new NumericLiteral(BigDecimal.valueOf(3), pos);
		Expression exp = new BinaryExpression(new NumericLiteral(BigDecimal.valueOf(2), pos), "/", num);
		VariableDeclaration varDecl = new VariableDeclaration(resId, numType, Optional.of(exp));
		astNodes.add(varDecl);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		Optional<Literal> optionalExpression = interpreter.getExecutorEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());

		assertEquals(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(3), 10, RoundingMode.HALF_UP),
				optionalExpression.get().getValue());
	}

	@Test
	void testAssignationWithComplexExpression() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("result", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		astNodes.add(variableDeclaration);

		NumericLiteral num = new NumericLiteral(BigDecimal.valueOf(2), pos);
		BinaryExpression firstPart = new BinaryExpression(num, "*", num);
		NumericLiteral num2 = new NumericLiteral(BigDecimal.valueOf(3.0), pos);
		BinaryExpression secondPart = new BinaryExpression(num2, "*", num2);
		BinaryExpression expression = new BinaryExpression(firstPart, "+", secondPart);
		Assignation assignation = new Assignation(identifier, expression, pos);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);


		interpreter.execute();
		interpreter.validate();

		Optional<Literal> optionalExpression = interpreter.getExecutorEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());
		assertEquals(BigDecimal.valueOf(13.0).toString(), optionalExpression.get().toString());
	}

	// PRINT LN ------------------------------------------------------------------------------------

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	public void setUp() {
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	public void tearDown() {
		System.setOut(originalOut);
	}

	@Test
	public void testPrintTextLiteral() throws Exception {
		List<Expression> arguments = Collections.singletonList(new TextLiteral("hola", pos));
		List<ASTNode> astNodes = Collections.singletonList(
				new Method(new Identifier("println", pos), arguments)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		List<String> printList = outputCapture.getPrintList();
		assertEquals(Collections.singletonList("hola"), printList);
	}

//	@Test
//	public void testPrintBinaryExpression() throws Exception {
//        List<ASTNode> astNodes = Arrays.asList(
//                new Method(new Identifier("println", pos), Collections.singletonList(
//                        new BinaryExpression(new NumericLiteral(BigDecimal.valueOf(2.0, pos), "+",
//                        new NumericLiteral(BigDecimal.valueOf(2.0, pos))
//                ))
//        );
//        PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
//        Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);
//
//        interpreter.execute();
//        interpreter.validate();
//
//        assertEquals("4.0", outputStream.toString().trim());
//	}

	@Test
	public void testPrintIdentifier() throws Exception {
		Optional<Expression> ex = Optional.of(new NumericLiteral(BigDecimal.valueOf(10), pos));
		Type type = new Type("number", pos);
		List<Expression> arguments = Collections.singletonList(new Identifier("a", pos));
		List<ASTNode> astNodes = Arrays.asList(
				new VariableDeclaration(new Identifier("a", pos), type, ex),
				new Method(new Identifier("println", pos), arguments)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);
		interpreter.execute();
		interpreter.validate();


		List<String> printList = outputCapture.getPrintList();
		assertEquals(Collections.singletonList("10"), printList);
	}

	@Test
	public void testPrintlnWithMultipleArguments() {
		List<Expression> arguments = Arrays.asList(
				new TextLiteral("hola", pos),
				new TextLiteral("mundo", pos)
		);

		Method printlnMethod = new Method(new Identifier("println", pos), arguments);

		List<ASTNode> astNodes = Collections.singletonList(printlnMethod);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException due to multiple arguments for println");
		} catch (InterpreterException e) {
			assertEquals("println expects exactly one argument", e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testReadEnvWithMultipleArguments() {
		List<Expression> arguments = Arrays.asList(
				new TextLiteral("hola", pos),
				new TextLiteral("mundo", pos)
		);

		Method readEnvMethod = new Method(new Identifier("readEnv", pos), arguments);

		List<ASTNode> astNodes = Collections.singletonList(readEnvMethod);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException due to multiple arguments for readEnv");
		} catch (InterpreterException e) {
			assertEquals("readEnv expects exactly one TextLiteral argument", e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

//	@Test
//	public void testPrintlnWithInvalidArgumentType() {
//
//		List<Expression> arguments = Collections.singletonList(
//				new NumericLiteral(BigDecimal.valueOf(42.0, pos)
//		);
//
//		Method printlnMethod = new Method(new Identifier("println", pos), arguments);
//
//		List<ASTNode> astNodes = Collections.singletonList(printlnMethod);
//
//		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
//		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);
//
//		try {
//			interpreter.validate();
//			fail("Expected InterpreterException due to invalid argument type for println");
//		} catch (InterpreterException e) {
//			assertEquals("println expects a Literal", e.getMessage());
//		} catch (Exception e) {
//			fail("Unexpected exception: " + e.getMessage());
//		}
//	}

	@Test
	public void testProgramExecution() throws Exception {
		Optional<Expression> ex = Optional.of(new NumericLiteral(BigDecimal.valueOf(10), pos));
		BinaryExpression a = new BinaryExpression(new Identifier("a", pos), "+",
				new NumericLiteral(BigDecimal.valueOf(5), pos));
		Type type = new Type("string", pos);
		List<Expression> arguments = Collections.singletonList(new Identifier("b", pos));
		List<ASTNode> astNodes = Arrays.asList(
				new VariableDeclaration(new Identifier("a", pos), new Type("number", pos), ex),
				new Assignation(new Identifier("a", pos), a, pos),
				new VariableDeclaration(new Identifier("b", pos), type, Optional.empty()),
				new Assignation(new Identifier("b", pos), new TextLiteral("Hola", pos), pos),
				new Method(new Identifier("println", pos), arguments)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		List<String> printList = outputCapture.getPrintList();
		assertEquals(Collections.singletonList("Hola"), printList);
	}

	@Test
	void testVariableDeclarationWithAssignmentAndTypeMismatchValidation() {
		Optional<Expression> ex = Optional.of(new NumericLiteral(BigDecimal.valueOf(42.0), pos));
		Type type = new Type("string", pos);
		List<ASTNode> astNodes = Collections.singletonList(
				new VariableDeclaration(new Identifier("a", pos), type, ex)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVariableRedeclarationValidation() throws Exception {
		Identifier a = new Identifier("a", pos);
		List<ASTNode> astNodes = Arrays.asList(
				new VariableDeclaration(a, new Type("string", pos), Optional.empty()),
				new VariableDeclaration(a, new Type("string", pos), Optional.empty())
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);


		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Variable already declared", exception.getMessage());
	}

	@Test
	void testAssignationVariableNotDeclaredValidation() {
		List<ASTNode> astNodes = Collections.singletonList(
				new Assignation(new Identifier("a", pos), new TextLiteral("hola", pos), pos)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Variable not declared", exception.getMessage());
	}

	@Test
	void testAssignationWithTypeMismatchValidation() throws Exception {
		Identifier a = new Identifier("a", pos);
		List<ASTNode> astNodes = Arrays.asList(
				new VariableDeclaration(a, new Type("string", pos), Optional.empty()),
				new Assignation(a, new NumericLiteral(BigDecimal.valueOf(42.0), pos), pos)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVisitAssignationValid() throws Exception {
		Optional<Expression> ex = Optional.of(new NumericLiteral(BigDecimal.valueOf(5.0), pos));
		Type type = new Type("number", pos);
		VariableDeclaration var = new VariableDeclaration(new Identifier("x", pos), type, ex);
		NumericLiteral expression = new NumericLiteral(BigDecimal.valueOf(10.0), pos);
		List<ASTNode> astNodes = Arrays.asList(var, new Assignation(new Identifier("x", pos), expression, pos));
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		assertEquals(BigDecimal.valueOf(10.0),
				interpreter.getExecutorEnvironment().get("x").getLiteral().get().getValue());
	}

	@Test
	void testVisitAssignationUndeclaredVariable() {
		List<ASTNode> astNodes = Collections.singletonList(
				new Assignation(new Identifier("x", pos),
						new NumericLiteral(BigDecimal.valueOf(10.0), pos), pos)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Variable not declared", exception.getMessage());
	}

	@Test
	void testVisitAssignationTypeMismatch() throws Exception {
		Optional<Expression> ex = Optional.of(new NumericLiteral(BigDecimal.valueOf(5.0), pos));
		List<ASTNode> astNodes = Arrays.asList(
				new VariableDeclaration(new Identifier("x", pos), new Type("number", pos), ex),
				new Assignation(new Identifier("x", pos), new TextLiteral("Hello", pos), pos)
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVisitBinaryExpressionTypeMismatch() {
		List<ASTNode> astNodes = Collections.singletonList(
				new BinaryExpression(new TextLiteral("Hello", pos), "-",
						new NumericLiteral(BigDecimal.valueOf(10.0), pos))
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Type mismatch for operator", exception.getMessage());
	}

	@Test
	void testVisitMethodUndeclaredVariable() throws Exception {
		List<ASTNode> astNodes = Collections.singletonList(
				new Method(new Identifier("println", pos), List.of(new Identifier("x", pos)))
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate();
		});

		assertEquals("Undeclared variable", exception.getMessage());
	}

	@Test
	void testVisitIdentifierDeclaredAndAssignedVariable() throws Exception {
		Optional<Expression> ex = Optional.of(new NumericLiteral(BigDecimal.valueOf(5), pos));
		List<ASTNode> astNodes = Arrays.asList(
				new VariableDeclaration(new Identifier("x", pos), new Type("number", pos), ex),
				new Method(new Identifier("println", pos), List.of(new Identifier("x", pos)))
		);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		assertEquals(BigDecimal.valueOf(5),
				interpreter.getExecutorEnvironment().get("x").getLiteral().get().getValue());
	}

	// UNARY EXPRESSIONS ------------------------------------------

	@Test
	public void testUnaryExpressionAssignNegativeNumber() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("i", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		UnaryExpression negativeFiveExpression = new UnaryExpression(
				new NumericLiteral(BigDecimal.valueOf(5.0), pos), "-", pos);
		Assignation assignation = new Assignation(identifier, negativeFiveExpression, pos);
		astNodes.add(variableDeclaration);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		Optional<Literal> resultExpression = interpreter.getExecutorEnvironment().get("i").getLiteral();
		assertTrue(resultExpression.isPresent());
		assertEquals(BigDecimal.valueOf(-5.0), resultExpression.get().getValue());
	}

	@Test
	public void testUnaryExpressionMismatchVersion() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("i", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		UnaryExpression negativeFiveExpression = new UnaryExpression(
				new NumericLiteral(BigDecimal.valueOf(5), pos), "-", pos);
		astNodes.add(negativeFiveExpression);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Unary expression", e.getMessage());
		}
	}

	@Test
	public void testUnaryExpressionMismatchVersionVal() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("i", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		UnaryExpression negativeFiveExpression = new UnaryExpression(
				new NumericLiteral(BigDecimal.valueOf(5.0), pos), "-", pos);
		astNodes.add(negativeFiveExpression);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers0, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Unary expression", e.getMessage());
		}
	}

	@Test
	public void testUnaryExpressionAssignNegativeNumberValidation() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();
		Identifier identifier = new Identifier("i", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		UnaryExpression negativeFiveExpression = new UnaryExpression(
				new NumericLiteral(BigDecimal.valueOf(5.0), pos), "-", pos);
		Assignation assignation = new Assignation(identifier, negativeFiveExpression, pos);
		astNodes.add(variableDeclaration);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.validate();
	}

	@Test
	public void testBinaryExpressionWithNegativeNumber() throws Exception {

		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("result", new Position(0,0, 0, 0));

		Type type = new Type("number", new Position(0,0, 0, 0));

		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		astNodes.add(variableDeclaration);

		NumericLiteral positiveTwo = new NumericLiteral(BigDecimal.valueOf(2.0), new Position(0,0, 0, 0));
		NumericLiteral positiveFive = new NumericLiteral(BigDecimal.valueOf(5.0), new Position(0,0, 0, 0));

		UnaryExpression negativeFive = new UnaryExpression(positiveFive, "-", new Position(0,0, 0, 0));

		BinaryExpression binaryExpression = new BinaryExpression(positiveTwo, "+", negativeFive);

		Assignation assignation = new Assignation(identifier, binaryExpression, new Position(0,0, 0, 0));
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();

		Optional<Literal> resultExpression = interpreter.getExecutorEnvironment().get("result").getLiteral();
		assertTrue(resultExpression.isPresent());
		assertEquals(BigDecimal.valueOf(-3.0), resultExpression.get().getValue());
	}

	// READ INPUT ---------------------------------------------------------------------------

	@Test
	public void testReadInput() {

		String simulatedInput = "42";
		InputProvider inputProvider = new MockInputProvider(simulatedInput);

		try {
			Position position = new Position(0, 0, 0, 0);

			VariableDeclaration variableDeclaration = new VariableDeclaration(
					new Identifier("a", position),
					new Type("number", position),
					Optional.empty()
			);

			Method readInputMethod = new Method(
					new Identifier("readInput", position),
					List.of(new TextLiteral("Escribe un numero:", position))
			);

			Assignation assignment = new Assignation(
					new Identifier("a", position),
					readInputMethod,
					position
			);

			List<ASTNode> astNodes = List.of(variableDeclaration, assignment);
			PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

			Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

			interpreter.execute();
			interpreter.validate();

			assertEquals("number", interpreter.getExecutorEnvironment().get("a").getType().getTypeName());

			Literal aValue = interpreter.getExecutorEnvironment().get("a").getLiteral().get();
			assertInstanceOf(NumericLiteral.class, aValue, "El valor de 'a' debe ser un NumericLiteral.");
			assertEquals(BigDecimal.valueOf(42), ((NumericLiteral) aValue).getValue());

		} catch (InterpreterException e) {
			fail("Error en la interpretación: " + e.getMessage() +
					" (Línea: " + e.getLine() + ", Columna: " + e.getColumn() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}

	@Test
	public void testReadInputVal() {

		String simulatedInput = "42";
		InputProvider inputProvider = new MockInputProvider(simulatedInput);

		try {
			Position position = new Position(0, 0, 0, 0);

			VariableDeclaration variableDeclaration = new VariableDeclaration(
					new Identifier("a", position),
					new Type("number", position),
					Optional.empty()
			);

			Method readInputMethod = new Method(
					new Identifier("readInput", position),
					List.of(new TextLiteral("Escribe un numero:", position))
			);

			Assignation assignment = new Assignation(
					new Identifier("a", position),
					readInputMethod,
					position
			);

			List<ASTNode> astNodes = List.of(variableDeclaration, assignment);
			PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

			Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

			interpreter.validate();

		} catch (InterpreterException e) {
			fail("Error en la interpretación: " + e.getMessage() +
					" (Línea: " + e.getLine() + ", Columna: " + e.getColumn() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}

	@Test
	public void testReadInputWithSimulatedInputTypeMismatch() {

		String simulatedInput = "hello";
		InputProvider inputProvider = new MockInputProvider(simulatedInput);

		try {

			Position position = new Position(1, 1, 1, 1);

			VariableDeclaration variableDeclaration = new VariableDeclaration(
					new Identifier("a", position),
					new Type("number", position),
					Optional.empty()
			);

			Method readInputMethod = new Method(
					new Identifier("readInput", position),
					List.of(new TextLiteral("Escribe un numero:", position))
			);

			Assignation assignment = new Assignation(
					new Identifier("a", position),
					readInputMethod,
					position
			);

			List<ASTNode> astNodes = List.of(variableDeclaration, assignment);
			PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

			Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

			interpreter.execute();
			interpreter.validate();

			fail("Se esperaba un error");

		} catch (InterpreterException e) {
			assertTrue(e.getMessage().contains("Type mismatch"), "Se esperaba un error");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}

	@Test
	public void testBinaryExpressionWithReadInput() {

		String simulatedInput = "Ana";
		InputProvider inputProvider = new MockInputProvider(simulatedInput);

		try {
			Position position = new Position(1, 1, 1, 1);

			VariableDeclaration variableDeclaration = new VariableDeclaration(
					new Identifier("a", position),
					new Type("string", position),
					Optional.empty()
			);

			TextLiteral holaLiteral = new TextLiteral("hola ", position);

			Method readInputMethod = new Method(
					new Identifier("readInput", position),
					List.of(new TextLiteral("Escribe tu nombre:", position))
			);

			BinaryExpression concatenation = new BinaryExpression(
					holaLiteral, "+", readInputMethod
			);

			Assignation assignment = new Assignation(
					new Identifier("a", position),
					concatenation,
					position
			);

			List<ASTNode> astNodes = List.of(variableDeclaration, assignment);
			PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

			Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

			interpreter.execute();
			interpreter.validate();

			assertEquals("string", interpreter.getExecutorEnvironment().get("a").getType().getTypeName());

			Literal aValue = interpreter.getExecutorEnvironment().get("a").getLiteral().get();
			assertInstanceOf(TextLiteral.class, aValue, "El valor de 'a' debe ser un TextLiteral.");
			assertEquals("hola Ana", ((TextLiteral) aValue).getValue());

		} catch (InterpreterException e) {
			fail("Error en la interpretación: " + e.getMessage() +
					" (Línea: " + e.getLine() + ", Columna: " + e.getColumn() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}

	@Test
	public void testBooleanVariableDeclaration() throws Exception {

		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("a", new Position(1, 4,1,1));
		BooleanLiteral booleanLiteral = new BooleanLiteral(true, new Position(1, 15, 1, 1));
		Position pos = new Position(0, 0, 0, 0);
		Optional<Expression> booleanLiteralExpr = Optional.of(booleanLiteral);
		Type type = new Type("boolean", pos);
		VariableDeclaration vardecl = new VariableDeclaration(identifier, type, booleanLiteralExpr);
		astNodes.add(vardecl);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();
		interpreter.validate();
		Variable variable = interpreter.getExecutorEnvironment().get("a");
		assertEquals("boolean", variable.getType().getTypeName());
		assertEquals(true, ((BooleanLiteral) variable.getLiteral().get()).getValue());
	}

	@Test
	public void testBooleanVariableDeclarationValidation() throws Exception {

		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("a", new Position(1, 4,1,1));
		BooleanLiteral booleanLiteral = new BooleanLiteral(true, new Position(1, 15, 1, 1));
		Position pos = new Position(0, 0, 0, 0);
		Optional<Expression> booleanLiteralExpr = Optional.of(booleanLiteral);
		Type type = new Type("boolean", pos);
		VariableDeclaration vardecl = new VariableDeclaration(identifier, type, booleanLiteralExpr);
		astNodes.add(vardecl);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.validate();
	}

	@Test
	public void testIfStatementTrueCondition() throws Exception {

		List<ASTNode> astNodes = new ArrayList<>();

		BooleanLiteral condition = new BooleanLiteral(true, new Position(1, 4,0,0));
		Identifier identifier = new Identifier("a", new Position(1, 9,0,0));
		Position pos = new Position(0, 0, 0, 0);
		Type type = new Type("boolean", pos);
		Optional<Expression> cond = Optional.of(condition);
		VariableDeclaration decl = new VariableDeclaration(identifier, type, cond);

		IfStatement ifStatement = new IfStatement(condition, List.of(decl), List.of(), pos);
		astNodes.add(ifStatement);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		assertFalse(interpreter.getExecutorEnvironment().containsKey("a"));

		interpreter.validate();
	}

	@Test
	public void testIfStatementTrueConditionValidation() throws Exception {

		List<ASTNode> astNodes = new ArrayList<>();

		BooleanLiteral condition = new BooleanLiteral(true, new Position(1, 4,0,0));
		Identifier identifier = new Identifier("a", new Position(1, 9,0,0));
		Position pos = new Position(0, 0, 0, 0);
		Type type = new Type("boolean", pos);
		Optional<Expression> cond = Optional.of(condition);
		VariableDeclaration decl = new VariableDeclaration(identifier, type, cond);

		IfStatement ifStatement = new IfStatement(condition, List.of(decl), List.of(), pos);
		astNodes.add(ifStatement);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.validate();
	}


	@Test
	public void testIfStatementFalseCondition() throws Exception {

		List<ASTNode> astNodes = new ArrayList<>();

		BooleanLiteral condition = new BooleanLiteral(false, new Position(1, 4, 0, 0));
		Identifier identifier = new Identifier("a", new Position(1, 9, 0, 0));
		Position pos = new Position(0, 0, 0, 0);
		Optional<Expression> bool = Optional.of(new BooleanLiteral(true, new Position(1, 10, 0, 0)));
		VariableDeclaration elseDecl = new VariableDeclaration(identifier, new Type("boolean", pos), bool);

		IfStatement ifStatement = new IfStatement(condition, List.of(), List.of(elseDecl), pos);
		astNodes.add(ifStatement);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		assertFalse(interpreter.getExecutorEnvironment().containsKey("a"));

		interpreter.validate();
	}

	@Test
	public void testStackAfterUnaryExpression() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		NumericLiteral numericLiteral = new NumericLiteral(BigDecimal.valueOf(5.0), new Position(1, 2, 0, 0));
		Position pos = new Position(1, 1, 0, 0);
		UnaryExpression unaryExpression = new UnaryExpression(numericLiteral, "-", pos);
		astNodes.add(unaryExpression);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		Stack<Literal> stack = interpreter.getStack();
		assertEquals(1, stack.size());
		assertEquals(BigDecimal.valueOf(-5.0), ((NumericLiteral) stack.pop()).getValue());

		interpreter.validate();
	}

	@Test
	public void testIfStatementReassignExternalVariable() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifierB = new Identifier("b", new Position(1, 1, 0, 0));
		Position pos = new Position(0, 0, 0, 0);
		Type type = new Type("boolean", pos);
		Optional<Expression> initialAssignment = Optional.of(new BooleanLiteral(false, pos));
		VariableDeclaration declB = new VariableDeclaration(identifierB, type, initialAssignment);
		astNodes.add(declB);

		BooleanLiteral condition = new BooleanLiteral(true, new Position(1, 4, 0, 0));

		BooleanLiteral newAssignment = new BooleanLiteral(true, pos);
		Assignation reassignB = new Assignation(identifierB, newAssignment, pos);

		IfStatement ifStatement = new IfStatement(condition, List.of(reassignB), List.of(), pos);
		astNodes.add(ifStatement);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		Variable variable = interpreter.getExecutorEnvironment().get("b");
		assertEquals("boolean", variable.getType().getTypeName());
		assertEquals(true, ((BooleanLiteral) variable.getLiteral().get()).getValue());

		interpreter.validate();
	}

	// CONST DECLARATION ------------------------------------------------------------------

	@Test
	public void testConstDeclarationIsStoredCorrectly() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10.0), new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		Variable variable = interpreter.getExecutorEnvironment().get("constVar");

		assertNotNull(variable);
		assertTrue(variable.isConst());
		assertEquals("number", variable.getType().getTypeName());
		assertEquals(BigDecimal.valueOf(10.0), ((NumericLiteral) variable.getLiteral().get()).getValue());

		interpreter.validate();
	}

	@Test
	public void testConstDeclarationValidation() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10.0), new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.validate();
	}

	@Test
	public void testConstReassignmentThrowsError() {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10.0), new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		Expression newExpression = new NumericLiteral(BigDecimal.valueOf(20.0), new Position(2, 10, 0, 0));
		Assignation assignation = new Assignation(identifier, newExpression, new Position(0, 0, 0, 0));
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		assertThrows(InterpreterException.class, interpreter::execute);
	}

	@Test
	public void testDuplicateConstDeclarationThrowsError() {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10.0), new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		Expression anotherExpression = new NumericLiteral(BigDecimal.valueOf(20.0), new Position(2, 10, 0, 0));
		ConstDeclaration duplicateDec = new ConstDeclaration(identifier, type, anotherExpression);
		astNodes.add(duplicateDec);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		assertThrows(InterpreterException.class, interpreter::execute);
	}

	@Test
	public void testConstDeclarationTypeMismatchThrowsError() {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new TextLiteral("text", new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		assertThrows(InterpreterException.class, interpreter::execute);
	}

	// READ ENV -----------------------------------------------------------------

	@Test
	void testReadEnvString() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		String name = "MY_VAR";

		VariableDeclaration declaration = new VariableDeclaration(
				new Identifier("myVar", new Position(1, 0, 0, 0)),
				new Type("string", new Position(0, 0, 0, 0)),
				Optional.empty()
		);
		astNodes.add(declaration);

		Assignation assignation = new Assignation(
				new Identifier("myVar", new Position(1, 0, 0, 0)),
				new Method(
						new Identifier("readEnv", new Position(1, 1, 0, 0)),
						List.of(new TextLiteral(name, new Position(1, 10, 0, 0)))
				),
				new Position(0, 0, 0, 0)
		);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.execute();

		Literal result = interpreter.getExecutorEnvironment().get("myVar").getLiteral().get();
		assertTrue(result instanceof TextLiteral, "El valor debe ser un TextLiteral.");
		assertEquals("Hello", ((TextLiteral) result).getValue(), "Los valores no coinciden");
	}

	@Test
	void testReadEnvStringVal() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		String name = "MY_VAR";

		VariableDeclaration declaration = new VariableDeclaration(
				new Identifier("myVar", new Position(1, 0, 0, 0)),
				new Type("string", new Position(0, 0, 0, 0)),
				Optional.empty()
		);
		astNodes.add(declaration);

		Assignation assignation = new Assignation(
				new Identifier("myVar", new Position(1, 0, 0, 0)),
				new Method(
						new Identifier("readEnv", new Position(1, 1, 0, 0)),
						List.of(new TextLiteral(name, new Position(1, 10, 0, 0)))
				),
				new Position(0, 0, 0, 0)
		);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers, inputProvider, outputCapture);

		interpreter.validate();
	}

	// VERSION MISMATCH
	@Test
	public void testIfStatementWithVersionMismatch() {

		BooleanLiteral condition = new BooleanLiteral(true, pos);
		Identifier identifier = new Identifier("a", pos);
		Position pos = new Position(0, 0, 0, 0);
		Type type = new Type("boolean", pos);
		Optional<Expression> cond = Optional.of(condition);
		VariableDeclaration decl = new VariableDeclaration(identifier, type, cond);

		IfStatement ifStatement = new IfStatement(condition, List.of(decl), List.of(), pos);

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(ifStatement);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException due to missing IfStatement handler");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: If Statement", e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testIfStatementWithVersionMismatchExe() {

		BooleanLiteral condition = new BooleanLiteral(true, pos);
		Identifier identifier = new Identifier("a", pos);
		Position pos = new Position(0, 0, 0, 0);
		Type type = new Type("boolean", pos);
		Optional<Expression> cond = Optional.of(condition);
		VariableDeclaration decl = new VariableDeclaration(identifier, type, cond);

		IfStatement ifStatement = new IfStatement(condition, List.of(decl), List.of(), pos);

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(ifStatement);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException due to missing IfStatement handler");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: If Statement", e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Test
	public void testBooleanVariableDeclarationWithVersionMismatch() {

		Identifier identifier = new Identifier("a", pos);
		BooleanLiteral booleanLiteral = new BooleanLiteral(true, pos);
		Position pos = new Position(0, 0, 0, 0);
		Optional<Expression> booleanLiteralExpr = Optional.of(booleanLiteral);
		Type type = new Type("boolean", pos);
		VariableDeclaration vardecl = new VariableDeclaration(identifier, type, booleanLiteralExpr);

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(vardecl);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException due to missing BooleanLiteral handler");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: BooleanLiteral", e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testBooleanVariableDeclarationWithVersionMismatchExe() {

		Identifier identifier = new Identifier("a", pos);
		BooleanLiteral booleanLiteral = new BooleanLiteral(true, pos);
		Position pos = new Position(0, 0, 0, 0);
		Optional<Expression> booleanLiteralExpr = Optional.of(booleanLiteral);
		Type type = new Type("boolean", pos);
		VariableDeclaration vardecl = new VariableDeclaration(identifier, type, booleanLiteralExpr);

		List<ASTNode> astNodes = new ArrayList<>();
		astNodes.add(vardecl);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException due to missing BooleanLiteral handler");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: BooleanLiteral", e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Test
	public void testReadInputWithVersionMismatch() {

		String simulatedInput = "42\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
		try {
			Position position = new Position(1, 1, 1, 1);

			VariableDeclaration variableDeclaration = new VariableDeclaration(
					new Identifier("a", position),
					new Type("number", position),
					Optional.empty()
			);
			Method readInputMethod = new Method(
					new Identifier("readInput", position),
					List.of(new TextLiteral("Escribe un numero:", position))
			);
			Assignation assignment = new Assignation(
					new Identifier("a", position),
					readInputMethod,
					position
			);
			List<ASTNode> astNodes = List.of(variableDeclaration, assignment);
			PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

			Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);
			try {
				interpreter.validate();
				fail("Expected InterpreterException due to missing readInput handler");
			} catch (InterpreterException e) {
				assertEquals("No handler found for node type: readInput", e.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}
	@Test
	public void testReadInputWithVersionMismatchExecute() {

		String simulatedInput = "42\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
		try {
			Position position = new Position(1, 1, 1, 1);

			VariableDeclaration variableDeclaration = new VariableDeclaration(
					new Identifier("a", position),
					new Type("number", position),
					Optional.empty()
			);
			Method readInputMethod = new Method(
					new Identifier("readInput", position),
					List.of(new TextLiteral("Escribe un numero:", position))
			);
			Assignation assignment = new Assignation(
					new Identifier("a", position),
					readInputMethod,
					position
			);
			List<ASTNode> astNodes = List.of(variableDeclaration, assignment);
			PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

			Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);
			try {
				interpreter.execute();
				fail("Expected InterpreterException due to missing readInput handler");
			} catch (InterpreterException e) {
				assertEquals("No handler found for node type: readInput", e.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}

	@Test
	public void testConstDeclarationMismatchVersion() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10.0), new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.validate();
			fail("Expected InterpreterException due to missing readInput handler");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Const declaration", e.getMessage());
		}
	}

	@Test
	public void testConstDeclarationMismatchVersionExecute() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		Identifier identifier = new Identifier("constVar", new Position(1, 1, 0, 0));
		Type type = new Type("number", new Position(0, 0, 0, 0));
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10.0), new Position(1, 10, 0, 0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);
		astNodes.add(constDeclaration);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: Const declaration", e.getMessage());
		}
	}

	@Test
	void testReadEnvStringVersionMismatchVal() throws Exception {
		List<ASTNode> astNodes = new ArrayList<>();

		String name = "MY_VAR";

		VariableDeclaration declaration = new VariableDeclaration(
				new Identifier("myVar", new Position(1, 0, 0, 0)),
				new Type("string", new Position(0, 0, 0, 0)),
				Optional.empty()
		);
		astNodes.add(declaration);
		Assignation assignation = new Assignation(
				new Identifier("myVar", new Position(1, 0, 0, 0)),
				new Method(
						new Identifier("readEnv", new Position(1, 1, 0, 0)),
						List.of(new TextLiteral(name, new Position(1, 10, 0, 0)))
				),
				new Position(0, 0, 0, 0)
		);
		astNodes.add(assignation);

		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);
		Interpreter interpreter = new Interpreter(iterator, handlers10, inputProvider, outputCapture);

		try {
			interpreter.execute();
			fail("Expected InterpreterException");
		} catch (InterpreterException e) {
			assertEquals("No handler found for node type: readEnv", e.getMessage());
		}
	}

	@Test
	void testParenthesisExecution() throws Exception {
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10), pos);
		Parenthesis parenthesis = new Parenthesis(expression);

		Identifier identifier = new Identifier("x", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		Assignation assignation = new Assignation(identifier, parenthesis, pos);

		List<ASTNode> astNodes = List.of(variableDeclaration, assignation);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(
				iterator,
				handlers,
				null,
				new OutputCapture()
		);

		interpreter.execute();

		Optional<Literal> literalOpt = interpreter.getExecutorEnvironment().get("x").getLiteral();

		assertTrue(literalOpt.isPresent(), "Variable 'x' should have a literal value.");
		assertEquals("10", literalOpt.get().getValue().toString().trim(), "Value of 'x' should be '10'.");
	}

	@Test
	void testParenthesisExecutionVal() throws Exception {
		Expression expression = new NumericLiteral(BigDecimal.valueOf(10), pos);
		Parenthesis parenthesis = new Parenthesis(expression);

		Identifier identifier = new Identifier("x", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		Assignation assignation = new Assignation(identifier, parenthesis, pos);

		List<ASTNode> astNodes = List.of(variableDeclaration, assignation);
		PrintScriptIterator<ASTNode> iterator = new PrintScriptIteratorTest<>(astNodes);

		Interpreter interpreter = new Interpreter(
				iterator,
				handlers,
				null,
				new OutputCapture()
		);

		interpreter.validate();
	}
}
