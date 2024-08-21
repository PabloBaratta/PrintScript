package org.example.builders;

public interface CommandBuilder {
    void buildAndRun(String[] parts) throws Exception;
}
