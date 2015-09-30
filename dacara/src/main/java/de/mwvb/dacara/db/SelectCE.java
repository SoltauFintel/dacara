package de.mwvb.dacara.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.dacara.ExecuteResult;

/**
 * Executes a command that begins with 'select'.
 * 
 * @author Marcus Warm
 */
public class SelectCE implements CommandExecutor {

    /**
     * Call getRows().close() on the result!
     */
	@Override
	public ExecuteResult execute(final Connection conn, final String sql) throws SQLException {
		if (!sql.trim().toLowerCase().startsWith("select")) {
			return null; // not responsible
		}
		final ResultSet rs = conn.createStatement().executeQuery(sql);
		if (rs == null) {
			throw new NullPointerException("rs is null!");
		}
		return new ExecuteResult() {
			@Override
			public RecordsIterator getRows() {
				return new RecordsIterator(conn, rs);
			}
			
			@Override
			public List<String> getColumnHeaders() {
				try {
					List<String> ret = new ArrayList<>();
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						ret.add(rs.getMetaData().getColumnName(i));
					}
					return ret;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
