package org.example;

public record Result<T>(T value, PrintScriptIterator<T> iterator) {
}
