package org.linter;

import org.example.lexer.token.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Report {
    private final List<ReportLine> reportLines = new LinkedList<>();

    public void addLine(Position position, String errorMessage) {
        reportLines.add(new ReportLine(position, errorMessage));
    }

    public List<ReportLine> getReportLines() {
        return reportLines;
    }

    public String printReport() {
        return reportLines.stream()
                .map(line -> line.errorMessage() +
                        "\n on \t" + line.position().toString())
                .collect(Collectors.joining());
    }
}
