package org.example;

public class Main {

	public static void main(String[] args) throws Exception {
		Cli cli = new Cli("cli/src/main/java/org/example/terminal.txt");
		cli.run();
	}
}
