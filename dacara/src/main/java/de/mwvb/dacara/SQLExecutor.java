package de.mwvb.dacara;

/**
 * Executes SQL command and returns result
 * 
 * @author Marcus Warm
 */
public interface SQLExecutor {

	ExecuteResult execute(String sql);
}
