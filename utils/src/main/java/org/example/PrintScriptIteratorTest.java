package org.example;

import java.util.Iterator;
import java.util.List;

public class PrintScriptIteratorTest<T> implements PrintScriptIterator<T> {

	private final Iterator<T> iterator;

	public PrintScriptIteratorTest(List<T> items) {
		this.iterator = items.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T getNext() {
		return iterator.next();
	}
}
