package org.token;

public record Position (
		int offset,
		int length,
		int line,
		int column

){

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() { return column; }
}
