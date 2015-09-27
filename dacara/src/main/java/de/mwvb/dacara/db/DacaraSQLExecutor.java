package de.mwvb.dacara.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import de.mwvb.dacara.Configuration;
import de.mwvb.dacara.ExecuteResult;
import de.mwvb.dacara.SQLExecutor;
import de.mwvb.dacara.base.ClassFactory;

/**
 * @author Marcus Warm
 */
public class DacaraSQLExecutor implements SQLExecutor {
	private final List<CommandExecutor> commandExecutors = new ArrayList<>();
	private final ClassFactory driverFactory = new ClassFactory("drivers"); // folder with driver JAR files, e.g. postgresql-9.1-901.jdbc4.jar
	@Inject
	public Configuration cfg;
	
	public DacaraSQLExecutor() {
		commandExecutors.add(new SelectCE());
		// TODO desc command
		// TODO tables command
		commandExecutors.add(new UpdateCE()); // last!
	}
	
	@Override
	public ExecuteResult execute(final String sql) {
		loadDriver();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(cfg.getConnectionString(), cfg.getUser(), cfg.getPassword());
			return execute(conn, sql);
		} catch (SQLException e) {
			throw new RuntimeException("Error accessing database\n" + e.getMessage(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ignore) {
				}
			}
		}
	}
	
	private ExecuteResult execute(final Connection conn, final String sql) throws SQLException {
		for (CommandExecutor ci : commandExecutors) {
			final ExecuteResult data = ci.execute(conn, sql);
			if (data != null) {
				return data;
			}
		}
		return null; // can't happen
	}

	private void loadDriver() {
		try {
			Driver driver = (Driver) driverFactory.newInstance(cfg.getDriverClassName());
			DriverManager.registerDriver(new DriverDelegator(driver)); // http://stackoverflow.com/a/14479658
		} catch (Exception e) {
			throw new RuntimeException("Error loading database driver from drivers folder: " + cfg.getDriverClassName(), e);
		}
	}
}
