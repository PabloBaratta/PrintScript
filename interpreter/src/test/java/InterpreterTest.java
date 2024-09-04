import org.example.*;
import org.example.interpreter.Executor;
import org.example.interpreter.Interpreter;
import org.example.interpreter.InterpreterException;
import org.example.interpreter.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import org.example.lexer.token.Position;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	Position pos = new Position(0, 0, 0, 0);

	// VARIABLE DECLARATION

	@Test
	void testVariableDeclaration() throws Exception {
		Interpreter interpreter = new Interpreter();

		// let a: string;
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		interpreter.visit(variableDeclaration);

		assertTrue(interpreter.getEnvironment().containsKey("a"));
		assertEquals(interpreter.getEnvironment().get("a").getType().getTypeName(), "string");
		assertTrue(interpreter.getEnvironment().get("a").getLiteral().isEmpty());
	}

	@Test
	void testVariableDeclarationWithAssignment() throws Exception {
		Interpreter interpreter = new Interpreter();

		// let a: string = "hola";
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		Expression expression = new TextLiteral("hola", new Position(0, 0,0, 0));
		VariableDeclaration variableDecl = new VariableDeclaration(identifier, type, Optional.of(expression));

		interpreter.visit(variableDecl);

		assertTrue(interpreter.getEnvironment().containsKey("a"));
		Optional<Literal> optionalExpression = interpreter.getEnvironment().get("a").getLiteral();

		assertTrue(optionalExpression.isPresent());
		assertEquals("hola", optionalExpression.get().getValue());
	}

	@Test
	void testVariableDeclarationWithAssignmentAndTypeMismatch() {
		Interpreter interpreter = new Interpreter();

		// let a: string = 42;
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		Expression expression = new NumericLiteral(42.0, new Position(0, 0,0, 0));
		VariableDeclaration variableDecl = new VariableDeclaration(identifier, type, Optional.of(expression));

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.visit(variableDecl);
		});

		assertEquals("Type mismatch", exception.getMessage()); // cambiar el mensaje mas adelante
	}

	@Test
	void testVariableRedeclarationThrowsError() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		interpreter.visit(variableDeclaration);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.visit(new VariableDeclaration(identifier, type, Optional.empty()));
		});

		assertEquals("Variable already declared", exception.getMessage());
	}

	// ASSIGNATION ------------------------------------------------------------------------

	@Test
	void testAssignationVariableNotDeclared() {
		Interpreter interpreter = new Interpreter();

		// a = "hola";
		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Expression expression = new TextLiteral("hola", new Position(0, 0,0, 0));
		Assignation assignation = new Assignation(identifier, expression, new Position(0, 0,0, 0));

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.visit(assignation);
		});

		assertEquals("Variable not declared", exception.getMessage());
	}

	@Test
	void testAssignationToExistingVariable() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", new Position(0, 0,0, 0));
		Type type = new Type("string", new Position(0, 0,0, 0));
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		interpreter.visit(variableDeclaration);

		Expression expression = new TextLiteral("hola", new Position(0, 0,0, 0));
		Assignation assignation = new Assignation(identifier, expression, new Position(0, 0,0, 0));

		interpreter.visit(assignation);

		Optional<Literal> optionalExpression = interpreter.getEnvironment().get("a").getLiteral();
		assertTrue(optionalExpression.isPresent());

		assertEquals("hola", optionalExpression.get().getValue());
	}

	@Test
	void testAssignationWithTypeMismatch() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		interpreter.visit(variableDeclaration);

		Expression expression = new NumericLiteral(42.0, pos);
		Assignation assignation = new Assignation(identifier, expression, pos);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.visit(assignation);
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	// BINARY EXPRESSION ----------------------------------------------------------------------------------

	@Test
	void testAssignationWithExpressionTwoPlusThree() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("result", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		interpreter.visit(variableDeclaration);

		Expression left = new NumericLiteral(2.0, pos);
		Expression right = new NumericLiteral(3.0, pos);
		BinaryExpression expression = new BinaryExpression(left, "+", right);
		Assignation assignation = new Assignation(identifier, expression, pos);

		interpreter.visit(assignation);

		Optional<Literal> optionalExpression = interpreter.getEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());
		assertEquals(5.0, optionalExpression.get().getValue());
	}

	@Test
	void testAssignationWithStringAndNumberAddition() throws Exception {
		Interpreter interpreter = new Interpreter();

		Position pos = new Position(0, 0, 0, 0);
		Identifier stringIdentifier = new Identifier("a", pos);
		Type stringType = new Type("string", pos);
		VariableDeclaration stringDec = new VariableDeclaration(stringIdentifier, stringType, Optional.empty());
		interpreter.visit(stringDec);

		TextLiteral text = new TextLiteral("Hello ", pos);
		Assignation stringAssignation = new Assignation(stringIdentifier, text, pos);
		interpreter.visit(stringAssignation);

		Identifier resId = new Identifier("result", pos);
		VariableDeclaration res = new VariableDeclaration(resId, stringType, Optional.empty());
		interpreter.visit(res);

		BinaryExpression ex = new BinaryExpression(new Identifier("a", pos), "+", new NumericLiteral(2.0, pos));
		Assignation assignation = new Assignation(resId, ex, pos);

		interpreter.visit(assignation);

		Optional<Literal> optionalExpression = interpreter.getEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());
		assertEquals("Hello 2.0", optionalExpression.get().getValue());
	}

	@Test
	void testVariableDeclarationWithExpressionDivision() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier resId = new Identifier("result", pos);
		Type numType = new Type("number", pos);
		NumericLiteral num = new NumericLiteral(3.0, pos);
		Expression exp = new BinaryExpression(new NumericLiteral(2.0, pos), "/", num);
		VariableDeclaration varDecl = new VariableDeclaration(resId, numType, Optional.of(exp));
		interpreter.visit(varDecl);

		Optional<Literal> optionalExpression = interpreter.getEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());

		assertEquals(2.0 / 3.0, optionalExpression.get().getValue());
	}

	@Test
	void testAssignationWithComplexExpression() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("result", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		interpreter.visit(variableDeclaration);

		NumericLiteral num = new NumericLiteral(2.0, pos);
		BinaryExpression firstPart = new BinaryExpression(num, "*", num);
		NumericLiteral num2 = new NumericLiteral(3.0, pos);
		BinaryExpression secondPart = new BinaryExpression(num2, "*", num2);
		BinaryExpression expression = new BinaryExpression(firstPart, "+", secondPart);
		Assignation assignation = new Assignation(identifier, expression, pos);

		interpreter.visit(assignation);
		Optional<Literal> optionalExpression = interpreter.getEnvironment().get("result").getLiteral();
		assertTrue(optionalExpression.isPresent());
		assertEquals(13.0, optionalExpression.get().getValue());
	}

	@Test
	void testVisitBinaryExpressionMultiplication() throws Exception {
		Validator validator = new Validator();
		Identifier identifier = new Identifier("x", null);
		Type type = new Type("number", null);
		validator.visit(new VariableDeclaration(identifier, type, Optional.of(new NumericLiteral(5.0, null))));

		Expression left = new Identifier("x", null);
		Expression right = new NumericLiteral(2.0, null);
		BinaryExpression binaryExpression = new BinaryExpression(left, "*", right);

		validator.visit(binaryExpression);

		Literal result = validator.getStack().pop();
		assertTrue(result instanceof NumericLiteral);

		assertTrue(left instanceof Identifier);
		assertTrue(right instanceof NumericLiteral);

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
		Interpreter interpreter = new Interpreter();
		List<Expression> text = Collections.singletonList(new TextLiteral("hola", pos));
		Method printMethod = new Method(new Identifier("println", pos), text);

		printMethod.accept(interpreter.getExecutionVisitor());

		assertEquals("hola", outputStream.toString().trim());
	}

	@Test
	public void testPrintBinaryExpression() throws Exception {
		Interpreter interpreter = new Interpreter();

		NumericLiteral leftOperand = new NumericLiteral(2.0, pos);
		NumericLiteral rightOperand = new NumericLiteral(2.0, pos);
		BinaryExpression expression = new BinaryExpression(leftOperand, "+", rightOperand);

		List<Expression> arguments = Collections.singletonList(expression);
		Method print = new Method(new Identifier("println", pos), arguments);

		print.accept(interpreter.getExecutionVisitor());

		assertEquals("4.0", outputStream.toString().trim());
	}

	@Test
	public void testPrintIdentifier() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("number",pos);
		NumericLiteral value = new NumericLiteral(10.0, pos);
		VariableDeclaration declaration = new VariableDeclaration(identifier, type, Optional.of(value));

		declaration.accept(interpreter.getExecutionVisitor());

		List<Expression> ident = Collections.singletonList(identifier);
		Method printMethod = new Method(new Identifier("println", pos), ident);

		printMethod.accept(interpreter.getExecutionVisitor());

		assertEquals("10.0", outputStream.toString().trim());
	}

	@Test
	public void testProgramExecution() throws Exception {
		setUp();

		Interpreter interpreter = new Interpreter();

		// let a: number = 10;
		Identifier identA  = new Identifier("a", pos);
		Type typeNumber = new Type("number", pos);
		NumericLiteral value10 = new NumericLiteral(10.0, pos);
		VariableDeclaration declA = new VariableDeclaration(identA, typeNumber, Optional.of(value10));

		// a = a + 5;
		NumericLiteral value5 = new NumericLiteral(5.0, pos);
		BinaryExpression sum = new BinaryExpression(identA, "+", value5);
		Assignation assignation = new Assignation(identA, sum, pos);

		// let b: string;
		Identifier identifierB = new Identifier("b", pos);
		Type typeString = new Type("string", pos);
		VariableDeclaration declarationB = new VariableDeclaration(identifierB, typeString, Optional.empty());

		// b = "Hola";
		TextLiteral helloText = new TextLiteral("Hola", pos);
		Assignation assignationB = new Assignation(identifierB, helloText, pos);

		// println(b);
		List<Expression> ident = Collections.singletonList(new Identifier("b", pos));
		Method printMethod = new Method(new Identifier("println", pos), ident);

		// armo program
		Program prog = new Program(Arrays.asList(declA, assignation, declarationB, assignationB, printMethod));

		prog.accept(interpreter.getExecutionVisitor());

		assertEquals("Hola", outputStream.toString().trim());

		tearDown();
	}

	@Test
	void testVariableDeclarationWithAssignmentAndTypeMismatchValidation() {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		Expression expression = new NumericLiteral(42.0, pos);
		VariableDeclaration variableDecl = new VariableDeclaration(identifier, type, Optional.of(expression));

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate(variableDecl);
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVariableRedeclarationValidation() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		interpreter.validate(variableDeclaration);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate(new VariableDeclaration(identifier, type, Optional.empty()));
		});

		assertEquals("Variable already declared", exception.getMessage());
	}

	@Test
	void testAssignationVariableNotDeclaredValidation() {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", pos);
		Expression expression = new TextLiteral("hola", pos);
		Assignation assignation = new Assignation(identifier, expression, pos);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate(assignation);
		});

		assertEquals("Variable not declared", exception.getMessage());
	}

	@Test
	void testAssignationWithTypeMismatchValidation() throws Exception {
		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("a", pos);
		Type type = new Type("string", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

		interpreter.validate(variableDeclaration);

		Expression expression = new NumericLiteral(42.0, pos);
		Assignation assignation = new Assignation(identifier, expression, pos);

		Exception exception = assertThrows(Exception.class, () -> {
			interpreter.validate(assignation);
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVisitAssignationValid() throws Exception {
		Validator validator = new Validator();

		Identifier identifier = new Identifier("x", null);
		Type type = new Type("number", null);
		validator.visit(new VariableDeclaration(identifier, type, Optional.of(new NumericLiteral(5.0, null))));

		Assignation assignation = new Assignation(identifier, new NumericLiteral(10.0, null), null);
		validator.visit(assignation);

		assertEquals(10.0, validator.getEnvironment().get("x").getLiteral().get().getValue());
	}

	@Test
	void testVisitAssignationUndeclaredVariable() {
		Validator validator = new Validator();
		Identifier identifier = new Identifier("x", null);
		Assignation assignation = new Assignation(identifier, new NumericLiteral(10.0, null), null);

		Exception exception = assertThrows(Exception.class, () -> {
			validator.visit(assignation);
		});

		assertEquals("Variable not declared", exception.getMessage());
	}

	@Test
	void testVisitAssignationTypeMismatch() throws Exception {
		Validator validator = new Validator();

		Identifier identifier = new Identifier("x", null);
		Type type = new Type("number", null);
		validator.visit(new VariableDeclaration(identifier, type, Optional.of(new NumericLiteral(5.0, null))));

		Assignation assignation = new Assignation(identifier, new TextLiteral("Hello", null), null);

		Exception exception = assertThrows(Exception.class, () -> {
			validator.visit(assignation);
		});

		assertEquals("Type mismatch", exception.getMessage());
	}

	@Test
	void testVisitBinaryExpressionTypeMismatch() {
		Validator validator = new Validator();

		BinaryExpression binaryExpression = new BinaryExpression(
				new TextLiteral("Hello", null),
				"-",
				new NumericLiteral(10.0, null)
		);

		Exception exception = assertThrows(Exception.class, () -> {
			validator.visit(binaryExpression);
		});

		assertEquals("Type mismatch for operator", exception.getMessage());
	}

	@Test
	void testVisitMethodUndeclaredVariable() throws Exception {
		Validator validator = new Validator();

		Method method = new Method(new Identifier("myMethod", null), List.of(new Identifier("x", null)));

		Exception exception = assertThrows(Exception.class, () -> {
			validator.visit(method);
		});

		assertEquals("undeclared variable", exception.getMessage());
	}

	@Test
	void testVisitIdentifierDeclaredAndAssignedVariable() throws Exception {
		Validator validator = new Validator();

		Identifier identifier = new Identifier("x", null);
		Type type = new Type("number", null);
		validator.visit(new VariableDeclaration(identifier, type, Optional.of(new NumericLiteral(5.0, null))));

		validator.visit(identifier);

		assertEquals(5.0, validator.getStack().pop().getValue());
	}

	// UNARY EXPRESSIONS ------------------------------------------

	@Test
	public void testUnaryExpressionAssignNegativeNumber() throws Exception {
		Interpreter interpreter = new Interpreter();

		Position pos = new Position(0, 0, 0, 0);
		Identifier identifier = new Identifier("i", pos);
		Type type = new Type("number", pos);
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		interpreter.visit(variableDeclaration);

		NumericLiteral positiveFive = new NumericLiteral(5.0, pos);

		UnaryExpression negativeFiveExpression = new UnaryExpression(positiveFive, "-", pos);

		Assignation assignation = new Assignation(identifier, negativeFiveExpression, pos);
		interpreter.visit(assignation);

		Optional<Literal> resultExpression = interpreter.getEnvironment().get("i").getLiteral();
		assertTrue(resultExpression.isPresent());
		assertEquals(-5.0, resultExpression.get().getValue());
	}

	@Test
	public void testBinaryExpressionWithNegativeNumber() throws Exception {

		Interpreter interpreter = new Interpreter();

		Identifier identifier = new Identifier("result", new Position(0,0, 0, 0));
		Type type = new Type("number", new Position(0,0, 0, 0));
		VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
		interpreter.visit(variableDeclaration);

		NumericLiteral positiveTwo = new NumericLiteral(2.0, new Position(0,0, 0, 0));

		NumericLiteral positiveFive = new NumericLiteral(5.0, new Position(0,0, 0, 0));
		UnaryExpression negativeFive = new UnaryExpression(positiveFive, "-", new Position(0,0, 0, 0));

		BinaryExpression binaryExpression = new BinaryExpression(positiveTwo, "+", negativeFive);

		Assignation assignation = new Assignation(identifier, binaryExpression, new Position(0,0, 0, 0));
		interpreter.visit(assignation);

		Optional<Literal> resultExpression = interpreter.getEnvironment().get("result").getLiteral();
		assertTrue(resultExpression.isPresent());
		assertEquals(-3.0, resultExpression.get().getValue());
	}

	// READ INPUT ---------------------------------------------------------------------------

	@Test
	public void testReadInput() {

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

			Executor executor = new Executor();
			executor.visit(variableDeclaration);
			executor.visit(assignment);

			assertEquals("number", executor.getEnvironment().get("a").getType().getTypeName());

			Literal aValue = executor.getEnvironment().get("a").getLiteral().get();
			assertInstanceOf(NumericLiteral.class, aValue, "El valor de 'a' debe ser un NumericLiteral.");
			assertEquals(42, ((NumericLiteral) aValue).getValue());


		} catch (InterpreterException e) {
			fail("Error en la interpretacion: " + e.getMessage() +
					" (Linea: " + e.getLine() + ", Columna: " + e.getColumn() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ocurrió una excepción inesperada.");
		}
	}

	@Test
	public void testReadInputWithSimulatedInputTypeMismatch() {

		String simulatedInput = "hello\n";
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

			Executor executor = new Executor();

			executor.visit(variableDeclaration);

			executor.visit(assignment);

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

		String simulatedInput = "Ana\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

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
					holaLiteral, "+",
					readInputMethod
			);

			Assignation assignment = new Assignation(
					new Identifier("a", position),
					concatenation,
					position
			);

			Executor executor = new Executor();

			executor.visit(variableDeclaration);

			executor.visit(assignment);

			assertEquals("string", executor.getEnvironment().get("a").getType().getTypeName());

			Literal aValue = executor.getEnvironment().get("a").getLiteral().get();
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

		Executor executor = new Executor();

		Identifier identifier = new Identifier("a", new Position(1, 4,1,1));
		BooleanLiteral booleanLiteral = new BooleanLiteral(true, new Position(1, 15, 1, 1));
		Position pos = new Position(0, 0, 0, 0);
		Optional<Expression> booleanLiteral1 = Optional.of(booleanLiteral);
		Type type = new Type("boolean", pos);
		VariableDeclaration vardecl = new VariableDeclaration(identifier, type, booleanLiteral1);

		executor.visit(vardecl);

		Variable variable = executor.getEnvironment().get("a");
		assertEquals("boolean", variable.getType().getTypeName());
		assertEquals(true, ((BooleanLiteral) variable.getLiteral().get()).getValue());
	}

	@Test
	public void testIfStatementTrueCondition() throws Exception {

		Executor executor = new Executor();
		BooleanLiteral condition = new BooleanLiteral(true, new Position(1, 4,0,0));
		Identifier identifier = new Identifier("a", new Position(1, 9,0,0));
		Position pos = new Position(0, 0, 0, 0);
		Type type = new Type("boolean", pos);
		Optional<Expression> cond = Optional.of(condition);
		VariableDeclaration decl = new VariableDeclaration(identifier, type, cond);

		IfStatement ifStatement = new IfStatement(condition, List.of(decl), List.of(), pos);

		executor.visit(ifStatement);

		Variable variable = executor.getEnvironment().get("a");
		assertEquals("boolean", variable.getType().getTypeName());
		assertEquals(true, ((BooleanLiteral) variable.getLiteral().get()).getValue());
	}

	@Test
	public void testIfStatementFalseCondition() throws Exception {

		Executor executor = new Executor();
		BooleanLiteral condition = new BooleanLiteral(false, new Position(1, 4, 0, 0));
		Identifier identifier = new Identifier("a", new Position(1, 9, 0, 0));
		Position pos1 = new Position(0, 0, 0, 0);
		Optional<Expression> bool = Optional.of(new BooleanLiteral(true, new Position(1, 10, 0, 0)));
		VariableDeclaration elsedecl = new VariableDeclaration(identifier, new Type("boolean", pos1), bool);

		IfStatement ifStatement = new IfStatement(condition, List.of(), List.of(elsedecl), pos1);

		executor.visit(ifStatement);

		Variable variable = executor.getEnvironment().get("a");
		assertEquals("boolean", variable.getType().getTypeName());
		assertEquals(true, ((BooleanLiteral) variable.getLiteral().get()).getValue());
	}

	@Test
	public void testStackAfterUnaryExpression() throws Exception {
		Executor executor = new Executor();

		NumericLiteral numericLiteral = new NumericLiteral(5.0, new Position(1, 2, 0, 0));
		Position pos1 = new Position(1, 1, 0, 0);
		UnaryExpression unaryExpression = new UnaryExpression( numericLiteral, "-", pos1);

		executor.visit(unaryExpression);

		Stack<Literal> stack = executor.getStack();
		assertEquals(1, stack.size());
		assertEquals(-5.0, ((NumericLiteral) stack.pop()).getValue());
	}

	// CONST DECLARATION ------------------------------------------------------------------

	@Test
	public void testConstDeclarationIsStoredCorrectly() throws Exception {
		Executor executor = new Executor();
		Identifier identifier = new Identifier("constVar", new Position(1, 1,0,0));
		Type type = new Type("number", new Position(0,0,0,0));
		Expression expression = new NumericLiteral(10.0, new Position(1, 10,0,0));
		ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);

		constDeclaration.accept(executor);

		Variable variable = executor.getEnvironment().get("constVar");

		assertNotNull(variable);
		assertTrue(variable.isConst());
		assertEquals("number", variable.getType().getTypeName());
		assertEquals(10.0, ((NumericLiteral) variable.getLiteral().get()).getValue());
	}

	@Test
	public void testConstReassignmentThrowsError() {
		Executor executor = new Executor();
		assertThrows(InterpreterException.class, () -> {

			Identifier identifier = new Identifier("constVar", new Position(1, 1,0,0));
			Type type = new Type("number", new Position(0,0,0,0));
			Expression expression = new NumericLiteral(10.0, new Position(1, 10,0,0));
			ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);

			constDeclaration.accept(executor);

			Expression newExpression = new NumericLiteral(20.0, new Position(2, 10,0,0));
			Assignation assignation = new Assignation(identifier, newExpression, new Position(0,0,0,0));

			assignation.accept(executor);
		});
	}

	@Test
	public void testDuplicateConstDeclarationThrowsError() {
		Executor executor = new Executor();
		assertThrows(InterpreterException.class, () -> {

			Identifier identifier = new Identifier("constVar", new Position(1, 1,0,0));
			Type type = new Type("number", new Position(0,0,0,0));
			Expression expression = new NumericLiteral(10.0, new Position(1, 10,0,0));
			ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);

			constDeclaration.accept(executor);

			Expression anotherExpression = new NumericLiteral(20.0, new Position(2, 10, 0, 0));
			ConstDeclaration duplicateDec = new ConstDeclaration(identifier, type, anotherExpression);

			duplicateDec.accept(executor);
		});
	}

	@Test
	public void testConstDeclarationTypeMismatchThrowsError() {
		Executor executor = new Executor();
		assertThrows(InterpreterException.class, () -> {
			Identifier identifier = new Identifier("constVar", new Position(1, 1,0,0));
			Type type = new Type("number", new Position(0,0,0,0));
			Expression expression = new TextLiteral("text", new Position(1, 10,0,0));
			ConstDeclaration constDeclaration = new ConstDeclaration(identifier, type, expression);

			constDeclaration.accept(executor);
		});
	}




}
