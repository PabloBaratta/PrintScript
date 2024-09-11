package org.example;

import org.w3c.dom.Text;

public interface ASTVisitor {

	void visit(Assignation assignation) throws Exception;

	void visit(VariableDeclaration variableDeclaration) throws Exception;

	void visit(Identifier identifier) throws Exception;

	void visit(TextLiteral textLiteral) throws Exception;

	void visit(NumericLiteral numericLiteral) throws Exception;

	void visit(Method method) throws Exception;

	void visit(UnaryExpression unaryExpression) throws Exception;

	void visit(BinaryExpression binaryExpression) throws Exception;

	void visit(Program program) throws Exception;

	void visit(Parenthesis parenthesis) throws Exception;

	void visit(BooleanLiteral booleanLiteral) throws Exception;

	void visit(IfStatement ifStatement) throws Exception;
	void visit(ConstDeclaration constDeclaration) throws Exception;
}
