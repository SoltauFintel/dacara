package de.mwvb.dacara.config;

import de.mwvb.base.xml.XMLElement;

/**
 * All informations for opening a database
 * 
 * @author Marcus Warm
 */
final class DatabaseConfiguration {
	private final String name;
	private final String driverClassName;
	private final String connectionString;
	private final String user;
	private final String password;

	DatabaseConfiguration(final XMLElement e) {
		name = e.getValue("name");
		driverClassName = e.getValue("driverClassName");
		connectionString = e.getValue("connectionString");
		user = e.getValue("user");
		password = e.getValue("password");
	}

	public String getName() {
		return name;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
