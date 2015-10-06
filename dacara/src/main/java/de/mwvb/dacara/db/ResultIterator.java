package de.mwvb.dacara.db;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public interface ResultIterator extends Iterator<List<String>>, Closeable {

	public boolean hasNext();
	
	public List<String> next();

	public void close() throws IOException;
}
