package org.example;

import org.example.lexer.token.Position;

record ReportLine(Position position, String errorMessage) {
}
