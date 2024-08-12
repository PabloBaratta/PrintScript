package org.example;

import org.w3c.dom.Text;

public interface ASTVisitor {

    void visit(Assignation assignation) throws Exception;

    void visit(VariableDeclaration variableDeclaration) throws Exception;

    void visit(Identifier identifier);

    void visit(TextLiteral textLiteral);

    void visit(NumericLiteral numericLiteral);

    void visit(Method method) throws Exception;

    void visit(UnaryExpression unaryExpression) throws Exception;

    void visit(BinaryExpression binaryExpression) throws Exception;

    void visit(Program program) throws Exception;
}
