import org.example.*;
import org.example.interpreter.Interpreter;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

    // VARIABLE DECLARATION

    @Test
    void testVariableDeclaration() throws Exception {
        Interpreter interpreter = new Interpreter();

        // let a: string;
        Identifier identifier = new Identifier("a");
        Type type = new Type("string");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

        interpreter.visit(variableDeclaration);

        assertTrue(interpreter.getEnvironment().containsKey("a"));
        assertEquals(interpreter.getEnvironment().get("a").getType().getTypeName(), "string");
        assertNull(interpreter.getEnvironment().get("a").getExpression());
    }

    @Test
    void testVariableDeclarationWithAssignment() throws Exception {
        Interpreter interpreter = new Interpreter();

        // let a: string = "hola";
        Identifier identifier = new Identifier("a");
        Type type = new Type("string");
        Expression expression = new TextLiteral("hola");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.of(expression));

        interpreter.visit(variableDeclaration);

        assertTrue(interpreter.getEnvironment().containsKey("a"));
        assertEquals("hola", interpreter.getEnvironment().get("a").getExpression().getValue());
    }

    @Test
    void testVariableDeclarationWithAssignmentAndTypeMismatch() {
        Interpreter interpreter = new Interpreter();

        // let a: string = 42;
        Identifier identifier = new Identifier("a");
        Type type = new Type("string");
        Expression expression = new NumericLiteral(42.0);
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.of(expression));

        Exception exception = assertThrows(Exception.class, () -> {
            interpreter.visit(variableDeclaration);
        });

        assertEquals("los tipos no coinciden", exception.getMessage()); // cambiar el mensaje mas adelante
    }

    @Test
    void testVariableRedeclarationThrowsError() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier identifier = new Identifier("a");
        Type type = new Type("string");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

        interpreter.visit(variableDeclaration);

        Exception exception = assertThrows(Exception.class, () -> {
            interpreter.visit(new VariableDeclaration(identifier, type, Optional.empty()));
        });

        assertEquals("la variable ya esta declarada", exception.getMessage());
    }

    // ASSIGNATION

    @Test
    void testAssignationVariableNotDeclared() {
        Interpreter interpreter = new Interpreter();

        // a = "hola";
        Identifier identifier = new Identifier("a");
        Expression expression = new TextLiteral("hola");
        Assignation assignation = new Assignation(identifier, expression);

        Exception exception = assertThrows(Exception.class, () -> {
            interpreter.visit(assignation);
        });

        assertEquals("la variable no esta declarada", exception.getMessage());
    }

    @Test
    void testAssignationToExistingVariable() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier identifier = new Identifier("a");
        Type type = new Type("string");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

        interpreter.visit(variableDeclaration);

        Expression expression = new TextLiteral("hola");
        Assignation assignation = new Assignation(identifier, expression);

        interpreter.visit(assignation);

        assertEquals("hola", interpreter.getEnvironment().get("a").getExpression().getValue());
    }

    @Test
    void testAssignationWithTypeMismatch() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier identifier = new Identifier("a");
        Type type = new Type("string");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());

        interpreter.visit(variableDeclaration);

        Expression expression = new NumericLiteral(42.0);
        Assignation assignation = new Assignation(identifier, expression);

        Exception exception = assertThrows(Exception.class, () -> {
            interpreter.visit(assignation);
        });

        assertEquals("los tipos no coinciden", exception.getMessage());
    }

    // BINARY EXPRESSION

    @Test
    void testAssignationWithExpressionTwoPlusThree() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier identifier = new Identifier("result");
        Type type = new Type("number");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
        interpreter.visit(variableDeclaration);

        Expression left = new NumericLiteral(2.0);
        Expression right = new NumericLiteral(3.0);
        BinaryExpression expression = new BinaryExpression(left, "+", right);
        Assignation assignation = new Assignation(identifier, expression);

        interpreter.visit(assignation);

        assertEquals(5.0, interpreter.getEnvironment().get("result").getExpression().getValue());
    }

    @Test
    void testAssignationWithStringAndNumberAddition() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier stringIdentifier = new Identifier("a");
        Type stringType = new Type("string");
        VariableDeclaration stringDeclaration = new VariableDeclaration(stringIdentifier, stringType, Optional.empty());
        interpreter.visit(stringDeclaration);

        Assignation stringAssignation = new Assignation(stringIdentifier, new TextLiteral("Hello "));
        interpreter.visit(stringAssignation);

        Identifier resultIdentifier = new Identifier("result");
        VariableDeclaration resultDeclaration = new VariableDeclaration(resultIdentifier, stringType, Optional.empty());
        interpreter.visit(resultDeclaration);

        BinaryExpression expression = new BinaryExpression(new Identifier("a"), "+", new NumericLiteral(2.0));
        Assignation assignation = new Assignation(resultIdentifier, expression);

        interpreter.visit(assignation);

        assertEquals("Hello 2.0", interpreter.getEnvironment().get("result").getExpression().getValue());
    }

    @Test
    void testVariableDeclarationWithExpressionDivision() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier resultIdentifier = new Identifier("result");
        Type numType = new Type("number");
        Expression expression = new BinaryExpression(new NumericLiteral(2.0), "/", new NumericLiteral(3.0));
        VariableDeclaration variableDeclaration = new VariableDeclaration(resultIdentifier, numType, Optional.of(expression));
        interpreter.visit(variableDeclaration);

        assertEquals(2.0 / 3.0, interpreter.getEnvironment().get("result").getExpression().getValue());
    }

    @Test
    void testAssignationWithComplexExpression() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier identifier = new Identifier("result");
        Type type = new Type("number");
        VariableDeclaration variableDeclaration = new VariableDeclaration(identifier, type, Optional.empty());
        interpreter.visit(variableDeclaration);

        BinaryExpression firstPart = new BinaryExpression(new NumericLiteral(2.0), "*", new NumericLiteral(2.0));
        BinaryExpression secondPart = new BinaryExpression(new NumericLiteral(3.0), "*", new NumericLiteral(3.0));
        BinaryExpression expression = new BinaryExpression(firstPart, "+", secondPart);
        Assignation assignation = new Assignation(identifier, expression);

        interpreter.visit(assignation);

        assertEquals(13.0, interpreter.getEnvironment().get("result").getExpression().getValue());
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
        Method printMethod = new Method(new Identifier("println"), Collections.singletonList(new TextLiteral("hola")));

        printMethod.accept(interpreter);

        assertEquals("hola", outputStream.toString().trim());
    }

    @Test
    public void testPrintBinaryExpression() throws Exception {
        Interpreter interpreter = new Interpreter();

        NumericLiteral leftOperand = new NumericLiteral(2.0);
        NumericLiteral rightOperand = new NumericLiteral(2.0);
        BinaryExpression expression = new BinaryExpression(leftOperand, "+", rightOperand);

        Method printMethod = new Method(new Identifier("println"), Collections.singletonList(expression));

        printMethod.accept(interpreter);

        assertEquals("4.0", outputStream.toString().trim());
    }

    @Test
    public void testPrintIdentifier() throws Exception {
        Interpreter interpreter = new Interpreter();

        Identifier identifier = new Identifier("a");
        Type type = new Type("number");
        NumericLiteral value = new NumericLiteral(10.0);
        VariableDeclaration declaration = new VariableDeclaration(identifier, type, Optional.of(value));

        declaration.accept(interpreter);

        Method printMethod = new Method(new Identifier("println"), Collections.singletonList(identifier));

        printMethod.accept(interpreter);

        assertEquals("10.0", outputStream.toString().trim());
    }

    @Test
    public void testProgramExecution() throws Exception {
        setUp();

        Interpreter interpreter = new Interpreter();

        // let a: number = 10;
        Identifier identifierA = new Identifier("a");
        Type typeNumber = new Type("number");
        NumericLiteral value10 = new NumericLiteral(10.0);
        VariableDeclaration declarationA = new VariableDeclaration(identifierA, typeNumber, Optional.of(value10));

        // a = a + 5;
        NumericLiteral value5 = new NumericLiteral(5.0);
        BinaryExpression sumExpression = new BinaryExpression(new Identifier("a"), "+", value5);
        Assignation assignation = new Assignation(identifierA, sumExpression);

        // let b: string;
        Identifier identifierB = new Identifier("b");
        Type typeString = new Type("string");
        VariableDeclaration declarationB = new VariableDeclaration(identifierB, typeString, Optional.empty());

        // b = "Hola";
        TextLiteral helloText = new TextLiteral("Hola");
        Assignation assignationB = new Assignation(identifierB, helloText);

        // println(b);
        Method printMethod = new Method(new Identifier("println"), Collections.singletonList(new Identifier("b")));

        // armo program
        Program program = new Program(Arrays.asList(declarationA, assignation, declarationB, assignationB, printMethod));

        program.accept(interpreter);

        assertEquals("Hola", outputStream.toString().trim());

        tearDown();
    }
}
