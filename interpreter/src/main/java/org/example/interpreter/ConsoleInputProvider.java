package org.example.interpreter;

import java.util.Scanner;

public class ConsoleInputProvider implements InputProvider{
	private final Scanner scanner;

	public ConsoleInputProvider() {
		this.scanner = new Scanner(System.in);
	}

	@Override
	public String readInput(String name) {
		System.out.print(name + ": ");
		return scanner.nextLine();
	}
}
