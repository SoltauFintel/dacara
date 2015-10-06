package de.mwvb.dacara.db.describe;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.dacara.db.ResultIterator;

/**
 * Iterator for Table Meta Data (desc command)
 * 
 * @author Marcus Warm
 */
public class TableDescriptionIterator implements ResultIterator {
	private final ResultSetMetaData m;
	private int columnNumber = 0;
	
	public TableDescriptionIterator(final ResultSetMetaData m) {
		this.m = m;
	}
	
	@Override
	public boolean hasNext() {
		try {
			return columnNumber < m.getColumnCount();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> getColumnHeaders() {
		List<String> ret = new ArrayList<>();
		ret.add("Column name");
		ret.add("Type");
		ret.add("Precision");
		ret.add("Scale");
		ret.add("Nullable");
		ret.add("AutoIncr");
		return ret;
	}
	
	@Override
	public List<String> next() {
		try {
			columnNumber++;
			List<String> cells = new ArrayList<>();
			cells.add(m.getColumnName(columnNumber));
			final int t = m.getColumnType(columnNumber);
			cells.add(getType(t));
			cells.add("" + m.getPrecision(columnNumber));
			cells.add(hasScale(t) ? "" + m.getScale(columnNumber) : "");
			cells.add(m.isNullable(columnNumber) == ResultSetMetaData.columnNoNulls ? "NOT NULL" : "NULL");
			cells.add(m.isAutoIncrement(columnNumber) ? "yes" : "");
			return cells;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getType(int t) {
		switch (t) {
		case java.sql.Types.VARCHAR: return "VARCHAR";
		case java.sql.Types.CHAR: return "char";
		case java.sql.Types.INTEGER: return "Integer";
		case java.sql.Types.DOUBLE: return "Double";
		case java.sql.Types.DECIMAL: return "Decimal";
		case java.sql.Types.FLOAT: return "Float";
		case java.sql.Types.SMALLINT: return "Small Integer";
		case java.sql.Types.BIGINT: return "Big Integer";
		case java.sql.Types.REAL: return "Real";
		case java.sql.Types.TINYINT: return "Tiny Integer";
		case java.sql.Types.NUMERIC: return "Numeric";
		case java.sql.Types.BIT: return "Bit";
		case java.sql.Types.CLOB: return "CLOB";
		case java.sql.Types.BLOB: return "BLOB";
		case java.sql.Types.BOOLEAN: return "Boolean";
		case java.sql.Types.BINARY: return "Binary";
		case java.sql.Types.DATE: return "Date";
		case java.sql.Types.TIME: return "Time";
		case java.sql.Types.TIMESTAMP: return "Timestamp";
		case java.sql.Types.VARBINARY: return "Varbinary";
		case java.sql.Types.LONGVARBINARY: return "Longvarbinary";
		case java.sql.Types.LONGVARCHAR: return "Longvarchar";
		case java.sql.Types.NCHAR: return "nchar";
		case java.sql.Types.NVARCHAR: return "NVARCHAR";
		case java.sql.Types.ROWID: return "Row Id";
		default: return "" + t;
		}
	}
	
	private boolean hasScale(int t) {
		switch (t) {
		case java.sql.Types.FLOAT: 
		case java.sql.Types.DOUBLE:
		case java.sql.Types.DECIMAL:
		case java.sql.Types.REAL: 
		case java.sql.Types.NUMERIC:
			return true;
		default: return false;
		}
	}

	@Override
	public void close() throws IOException {
	}
}
