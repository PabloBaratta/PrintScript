package org.example.interpreter;

public class InterpreterException extends Exception {
	private final int line;
	private final int column;

	public InterpreterException(String message, int line, int column) {
		super(message);
		this.line = line;
		this.column = column;
	}

	@Override
	public String toString() {
		return "Error: " + getMessage() + " at line " + line + ", column " + column;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}
}
