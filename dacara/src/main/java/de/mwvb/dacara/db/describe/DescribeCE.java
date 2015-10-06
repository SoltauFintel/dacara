package de.mwvb.dacara.db.describe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import de.mwvb.dacara.ExecuteResult;
import de.mwvb.dacara.db.CommandExecutor;
import de.mwvb.dacara.db.ResultIterator;

/**
 * Show table meta data:
 * "desc {tablename}" or "describe {tablename}"
 * 
 * @author Marcus Warm
 */
public class DescribeCE implements CommandExecutor {

	@Override
	public ExecuteResult execute(final Connection conn, final String sql) throws SQLException {
		String tsql = sql.trim().toLowerCase();
		String tabelle;
		if (tsql.startsWith("describe ")) {
			tabelle = sql.substring("describe ".length()).trim();
		} else if (tsql.startsWith("desc ")) {
			tabelle = sql.substring("desc ".length()).trim();
		} else {
			return null; // not responsible
		}
		final ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + tabelle + " WHERE 1=0");
		final ResultSetMetaData m;
		try {
			m = rs.getMetaData();
		} finally {
			rs.close();
		}
		return new ExecuteResult() {
			@Override
			public List<String> getColumnHeaders() {
				return TableDescriptionIterator.getColumnHeaders();
			}
			
			@Override
			public ResultIterator getRows() {
				return new TableDescriptionIterator(m, conn);
			}
		};
	}
}
