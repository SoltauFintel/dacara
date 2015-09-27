package de.mwvb.dacara;

import java.util.List;

import de.mwvb.dacara.db.RecordsIterator;

/**
 * Returns records affected -or- data rows.
 * 
 * @author Marcus Warm
 */
public abstract class ExecuteResult {

	/**
	 * @return null if SELECT statement, else number from 0
	 */
	public Integer getRecordsAffected() {
		return null;
	}

	/**
	 * Call only if getRecordsAffected() returns null.
	 */
	public List<String> getColumnHeaders() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Call this method only if getRecordsAffected() returns null.
	 * Call close() on return value in the finally block!
	 * @return all column values of all rows
	 */
	public RecordsIterator getRows() {
		throw new UnsupportedOperationException();
	}
}
