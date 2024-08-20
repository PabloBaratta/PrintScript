package org.linter.visitors;

import org.example.*;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.Report;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LinterVisitorTests {


    @Test
    public void successfulProgramVisitor() throws Exception {

        Program program = new Program(
                List.of(new VariableDeclaration(
                                new Identifier("a", new Position(4,1,1)),
                                new Type("string", new Position(6,6,1)),
                                Optional.of(new TextLiteral("america", new Position(14, 7,1)))),
                        new Method(
                                new Identifier("println", new Position(23,7,2)),
                                List.of(new Identifier("a", new Position(31, 1,2))))));

        Report report = new Report();
        PrintLineRules printLineRules = new PrintLineRules(report);
        IdentifierRules identifierRules = new IdentifierRules(Case.CAMEL_CASE, report);

        LinterVisitor linterVisitor = new LinterVisitor(List.of(printLineRules, identifierRules));

        linterVisitor.visit(program);

        assertTrue(report.getReportLines().isEmpty());
    }


}