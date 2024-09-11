package org.linter;

import org.token.Position;

public record ReportLine(Position position, String errorMessage) {}
