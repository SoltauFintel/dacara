package de.mwvb.dacara;

import java.util.List;

/**
 * Provides configuration parameters
 * 
 * @author Marcus Warm
 */
public interface Configuration {
	int MAX_HISTORY_SIZE = 50;

	/**
	 * @return names of all configured databases
	 */
	List<String> getDatabaseNames();

	/**
	 * @return index of current database
	 */
	int getCurrentDatabase();

	/**
	 * Changes current database.
	 * @param index from 0
	 */
	void setCurrentDatabase(int index);

	/**
	 * @return class name of database driver of current database
	 */
	String getDriverClassName();
	
	/**
	 * @return database connection string of current database
	 */
	String getConnectionString();
	
	/**
	 * @return user name of current database
	 */
	String getUser();
	
	/**
	 * @return password for user
	 */
	String getPassword();
	
	/**
	 * @return all successfully executed SQL commands of the past
	 */
	List<String> getHistoryList();

	/**
	 * Adds successfully executed SQL command to history.
	 */
	void addToHistory(String sql);
}
