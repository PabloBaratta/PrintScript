package org.example.builders;

import org.example.Runner;

public class ExecutionBuilder implements CommandBuilder{
    @Override
    public String buildAndRun(String[] parts) throws Exception {
        if (parts.length != 2) {
            throw new RuntimeException("Invalid number of arguments, should be two");
        }
        String filePath = parts[1];
        if (filePath == null) {
            throw new RuntimeException("File path not found");
        }
        Runner.run(filePath);
        return "Execution completed";
    }
}
