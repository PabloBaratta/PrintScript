package org.example;

public interface PrintScriptIterator<T> {

	public boolean hasNext();
	public T getNext() throws Exception;

}
