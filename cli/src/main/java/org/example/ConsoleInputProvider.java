package org.example;

import org.example.InputProvider;

import java.util.Scanner;

public class ConsoleInputProvider implements InputProvider {
	private final Scanner scanner;

	public ConsoleInputProvider() {
		this.scanner = new Scanner(System.in);
	}

	@Override
	public String readInput(String name) {
		return scanner.nextLine();
	}
}
