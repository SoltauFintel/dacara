package de.mwvb.dacara.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Records iterator
 * <p>Please use hasNext() and then next() for iterating.
 * Please call close() at the end in a finally block.
 * 
 * @author Marcus Warm
 */
public class RecordsIterator implements Iterator<List<String>>, Closeable {
	private final ResultSet rs;
	
	public RecordsIterator(ResultSet rs) {
		this.rs = rs;
	}
	
	@Override
	public boolean hasNext() {
		try {
			return rs.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> next() {
		try {
			List<String> ret = new ArrayList<>();
			for (int colNo = 1; colNo <= rs.getMetaData().getColumnCount(); colNo++) {
				final Object value = rs.getObject(colNo);
				ret.add(value == null ? null : value.toString());
			}
			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
