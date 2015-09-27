package de.mwvb.dacara.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverDelegator implements Driver {
	private final Driver driver;

	public DriverDelegator(final Driver driver) {
		this.driver = driver;
	}

	@Override
	public boolean acceptsURL(String u) throws SQLException {
		return driver.acceptsURL(u);
	}

	@Override
	public Connection connect(String u, Properties p) throws SQLException {
		return driver.connect(u, p);
	}

	@Override
	public int getMajorVersion() {
		return driver.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return driver.getMinorVersion();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return driver.getParentLogger();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return driver.getPropertyInfo(url, info);
	}

	@Override
	public boolean jdbcCompliant() {
		return driver.jdbcCompliant();
	}
}
