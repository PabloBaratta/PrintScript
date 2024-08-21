package org.example.builders;

import org.example.Runner;

public class ExecutionBuilder implements CommandBuilder{
    @Override
    public void buildAndRun(String[] parts) throws Exception {
        String filePath = parts[1];
        if (filePath == null) {
            throw new RuntimeException("File path not found");
        }
        Runner.run(filePath);
    }
}
