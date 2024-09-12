package org.example;

public class CliOutputEmitter implements OutputEmitter {
	@Override
	public void capture(String output) {
		System.out.println(output);
	}
}
