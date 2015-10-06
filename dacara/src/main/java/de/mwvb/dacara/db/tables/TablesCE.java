package de.mwvb.dacara.db.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import de.mwvb.dacara.ExecuteResult;
import de.mwvb.dacara.db.CommandExecutor;
import de.mwvb.dacara.db.ResultIterator;
import de.mwvb.dacara.db.select.ColumnNameFilter;
import de.mwvb.dacara.db.select.RecordsIterator;

public class TablesCE implements CommandExecutor {

	@Override
	public ExecuteResult execute(Connection conn, String sql) throws SQLException {
		final String tsql = sql.trim().toLowerCase();
		if (!tsql.equals("tables") && !tsql.equals("list") && !tsql.equals("tabellen")) {
			return null;
		}
		final ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });
		final ColumnNameFilter filter = new ColumnNameFilter("table_name");
		return new ExecuteResult() {
			@Override
			public List<String> getColumnHeaders() {
				try {
					return RecordsIterator.getColumnHeaders(rs.getMetaData(), filter);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public ResultIterator getRows() {
				return new RecordsIterator(conn, rs, filter);
			}
		};
	}

}
