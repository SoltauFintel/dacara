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
import de.mwvb.dacara.db.describe.DescribeCE;
import de.mwvb.dacara.db.select.SelectCE;
import de.mwvb.dacara.db.tables.TablesCE;

/**
 * @author Marcus Warm
 */
public class DacaraSQLExecutor implements SQLExecutor {
	private final List<CommandExecutor> commandExecutors = new ArrayList<>();
	private final ClassFactory driverFactory = new ClassFactory("drivers") { // folder with driver JAR files, e.g. postgresql-9.1-901.jdbc4.jar
		protected void created(Object driver) {
			try {
				DriverManager.registerDriver(new DriverDelegator((Driver) driver)); // http://stackoverflow.com/a/14479658
			} catch (SQLException e) {
				throw new RuntimeException("Error registering database driver from drivers folder: " + cfg.getDriverClassName(), e);
			}
		}
	};
	@Inject
	public Configuration cfg;
	
	public DacaraSQLExecutor() {
		commandExecutors.add(new SelectCE());
		commandExecutors.add(new DescribeCE());
		commandExecutors.add(new TablesCE());
		commandExecutors.add(new CommandExecutor() {
			@Override
			public ExecuteResult execute(Connection conn, String sql) throws SQLException {
				if (!sql.trim().isEmpty()) {
					return null; // not responsible
				}
				throw new RuntimeException("Please enter SQL!\n\n"
						+ "You can also enter \"tables\" to list all table names"
						+ "\nor \"desc <TABLE NAME>\" to list all table columns.");
			}
		});
		commandExecutors.add(new UpdateCE()); // last!
	}
	
	@Override
	public ExecuteResult execute(final String sql) {
		loadDriver();
		try {
			Connection conn = DriverManager.getConnection(cfg.getConnectionString(), cfg.getUser(), cfg.getPassword());
			return execute(conn, sql);
		} catch (SQLException e) {
			throw new RuntimeException("Error accessing database\n" + e.getMessage(), e);
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
			driverFactory.newInstance(cfg.getDriverClassName());
		} catch (Exception e) {
			throw new RuntimeException("Error loading database driver from drivers folder: " + cfg.getDriverClassName(), e);
		}
	}
}
