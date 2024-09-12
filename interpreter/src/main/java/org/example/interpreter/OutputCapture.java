package org.example.interpreter;

import org.example.OutputEmitter;

import java.util.ArrayList;
import java.util.List;

public class OutputCapture implements OutputEmitter {
	private final List<String> printList = new ArrayList<>();

	@Override
	public void capture(String output) {
		printList.add(output);
	}

	public List<String> getPrintList() {
		return new ArrayList<>(printList);
	}
}
