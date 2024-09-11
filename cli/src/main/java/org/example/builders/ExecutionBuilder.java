/*
package org.example.builders;

import org.example.Runner;

import java.nio.file.Paths;

public class ExecutionBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 2) {
            throw new RuntimeException("Invalid number of arguments, should be two");
        }
        String filePath = Paths.get("").toAbsolutePath() + parts[1];
        Runner.run(filePath);
        return "Execution completed";
    }
}
*/
