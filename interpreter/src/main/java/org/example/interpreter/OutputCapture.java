package org.example.interpreter;

import java.util.ArrayList;
import java.util.List;

public class OutputCapture {
	private final List<String> printList = new ArrayList<>();

	public void capture(String output) {
		printList.add(output);
	}

	public List<String> getPrintList() {
		return new ArrayList<>(printList);
	}
}
