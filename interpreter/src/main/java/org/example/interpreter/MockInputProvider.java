package org.example.interpreter;

import org.example.InputProvider;

public class MockInputProvider implements InputProvider {
	private final String simulatedInput;

	public MockInputProvider(String simulatedInput) {
		this.simulatedInput = simulatedInput;
	}

	@Override
	public String readInput(String message) {
		return simulatedInput;
	}
}
