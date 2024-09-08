package org.example.nodeconstructors;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
	R apply(T t) throws Exception;
}
