package org.example.builders;

public class ValidationBuilder implements CommandBuilder{
    @Override
    public void buildAndRun(String[] parts) throws Exception {
        String pathFile = parts[1];
        if (pathFile == null) {
            throw new RuntimeException("File path not found");
        }
    }
}
