package de.mwvb.dacara.db;

import java.sql.Connection;
import java.sql.SQLException;

import de.mwvb.dacara.ExecuteResult;

/**
 * Interprets and executes entered command.
 * 
 * @author Marcus Warm
 */
public interface CommandExecutor {

	/**
	 * Returns null if implementation can not execute sql.
	 */
	ExecuteResult execute(final Connection conn, final String sql) throws SQLException;
}
