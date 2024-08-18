package org.example.visitors;

import org.example.*;

public class PrintLineRules implements ASTVisitor {

    public static final String ERROR_MESSAGE = "printLn should only be called with a identifier or a literal";
    private final Report report;

    public PrintLineRules(Report report) {
        this.report = report;
    }


    @Override
    public void visit(Assignation assignation) throws Exception {

    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) throws Exception {

    }

    @Override
    public void visit(Identifier identifier) throws Exception {

    }

    @Override
    public void visit(TextLiteral textLiteral) {

    }

    @Override
    public void visit(NumericLiteral numericLiteral) {

    }

    @Override
    public void visit(Method method) throws Exception {
        if (!method.getVariable().getName().equals("println")) {
            return;
        }
        Expression first = method.getArguments().getFirst();
        first.accept(this);
    }

    @Override
    public void visit(UnaryExpression unaryExpression) throws Exception {
        report.addLine(unaryExpression.getPosition(), ERROR_MESSAGE);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) throws Exception {
        report.addLine(unaryExpression.getPosition(), ERROR_MESSAGE);
    }

    @Override
    public void visit(Program program) throws Exception {

    }

    @Override
    public void visit(Parenthesis parenthesis) throws Exception {

    }
}
