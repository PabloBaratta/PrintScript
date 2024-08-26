package org.linter;

import org.example.lexer.token.Position;

public record ReportLine(Position position, String errorMessage) {}
