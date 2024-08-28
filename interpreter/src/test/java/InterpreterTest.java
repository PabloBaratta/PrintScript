import org.example.*;
import org.example.interpreter.Interpreter;
import org.example.interpreter.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
		assertTrue(interpreter.getEnvironment().get("a").getExpression().isEmpty());
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
		Optional<Expression> optionalExpression = interpreter.getEnvironment().get("a").getExpression();

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

	// ASSIGNATION

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

		Optional<Expression> optionalExpression = interpreter.getEnvironment().get("a").getExpression();
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

	// BINARY EXPRESSION

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

		Optional<Expression> optionalExpression = interpreter.getEnvironment().get("result").getExpression();
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

		Optional<Expression> optionalExpression = interpreter.getEnvironment().get("result").getExpression();
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

		Optional<Expression> optionalExpression = interpreter.getEnvironment().get("result").getExpression();
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
		Optional<Expression> optionalExpression = interpreter.getEnvironment().get("result").getExpression();
		assertTrue(optionalExpression.isPresent());
		assertEquals(13.0, optionalExpression.get().getValue());
	}

	// METHOD

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

		assertEquals(10.0, validator.getEnvironment().get("x").getExpression().get().getValue());
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
}
