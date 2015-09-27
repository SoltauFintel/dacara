package de.mwvb.dacara.db;

import java.sql.Connection;
import java.sql.SQLException;

import de.mwvb.dacara.ExecuteResult;

/**
 * Executes a command that not begins with 'select'. Usually 'insert', 'update' or 'delete'.
 * 
 * <p>Must be last one in responsibility chain because it's always responsible.
 * 
 * @author Marcus Warm
 */
public class UpdateCE implements CommandExecutor {

	@Override
	public ExecuteResult execute(Connection conn, String sql) throws SQLException {
		final int ra = conn.createStatement().executeUpdate(sql);
		return new ExecuteResult() {
			@Override
			public Integer getRecordsAffected() {
				return ra;
			}
		};
	}
}
