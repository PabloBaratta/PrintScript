package org.example;

public interface PrintScriptIterator<T> {

	public boolean hasNext();
	public Result<T> getNext();

}
