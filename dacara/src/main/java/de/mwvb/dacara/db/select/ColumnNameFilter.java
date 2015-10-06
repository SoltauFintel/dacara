package de.mwvb.dacara.db.select;

import java.util.ArrayList;
import java.util.List;

public class ColumnNameFilter {
	private List<String> allowedColumnNames;
	
	/** all columns allowed */
	public ColumnNameFilter() {
		allowedColumnNames = null;
	}

	public ColumnNameFilter(final List<String> allowedColumnNames) {
		this.allowedColumnNames = allowedColumnNames;
	}

	public ColumnNameFilter(String ... allowedColumnNames) {
		this.allowedColumnNames = new ArrayList<>();
		for (String c : allowedColumnNames) {
			this.allowedColumnNames.add(c);
		}
	}

	public boolean show(final String columnName) {
		if (allowedColumnNames == null) {
			return true;
		}
		for (String c : allowedColumnNames) {
			if (c.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}
}
