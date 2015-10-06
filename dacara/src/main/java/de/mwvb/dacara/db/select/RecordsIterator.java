package de.mwvb.dacara.db.select;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.dacara.db.ResultIterator;

/**
 * Records iterator
 * <p>Please use hasNext() and then next() for iterating.
 * Please call close() at the end in a finally block.
 * 
 * @author Marcus Warm
 */
public class RecordsIterator implements ResultIterator {
    private final Connection conn;
	private final ResultSet rs;
	
	public RecordsIterator(final Connection conn, final ResultSet rs) {
	    this.conn = conn;
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
			if (conn != null) {
			    conn.close();
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
