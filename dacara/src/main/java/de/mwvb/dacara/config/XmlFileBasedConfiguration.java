package de.mwvb.dacara.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Singleton;

import de.mwvb.base.xml.XMLDocument;
import de.mwvb.base.xml.XMLElement;
import de.mwvb.dacara.Configuration;

/**
 * This Configuration implementation reads all parameters from XML files.
 * 
 * @author Marcus Warm
 */
@Singleton
public class XmlFileBasedConfiguration implements Configuration {
	private final List<DatabaseConfiguration> databases = new ArrayList<>();
	private int current = -1;
	private final List<String> history = new ArrayList<>();
	
	public XmlFileBasedConfiguration() {
		readDatabases();
		readHistory();
	}

	private void readDatabases() {
		final XMLDocument doc = readDatabasesFile();
		int index = 0;
		for (XMLElement e : doc.getChildren()) {
			DatabaseConfiguration db = new DatabaseConfiguration(e);
			databases.add(db);
			if (!e.getValue("selected").isEmpty()) {
				current = index;
			}
			index++;
		}
	}
	
	private XMLDocument readDatabasesFile() {
		File f = new File(getDatabasesFileName());
		if (f.exists()) {
			return new XMLDocument(f);
		} else {
			return new XMLDocument("<configuration/>");
		}
	}
	
	@Override
	public List<String> getDatabaseNames() {
		List<String> names = new ArrayList<>();
		for (DatabaseConfiguration db : databases) {
			names.add(db.getName());
		}
		return names;
	}

	@Override
	public String getDriverClassName() {
		return databases.get(current).getDriverClassName();
	}

	@Override
	public String getConnectionString() {
		return databases.get(current).getConnectionString();
	}

	@Override
	public String getUser() {
		return databases.get(current).getUser();
	}

	@Override
	public String getPassword() {
		return databases.get(current).getPassword();
	}

	@Override
	public int getCurrentDatabase() {
		return current;
	}

	@Override
	public void setCurrentDatabase(int index) {
		current = index;

		final XMLDocument doc = readDatabasesFile();
		int i = 0;
		for (XMLElement e : doc.getChildren()) {
			if (i == current) {
				e.setValue("selected", "1");
			} else {
				e.removeAttribute("selected");
			}
			i++;
		}
		doc.saveFile(getDatabasesFileName());
	}

	private void readHistory() {
		history.clear();
		final File f = new File(getHistoryFileName());
		if (f.exists()) {
			final XMLDocument doc = new XMLDocument(f);
			for (XMLElement e : doc.getChildren()) {
				history.add(e.getText());
			}
		}
	}

	@Override
	public List<String> getHistoryList() {
		return Collections.unmodifiableList(history);
	}

	@Override
	public void addToHistory(String sql) {
		history.remove(sql);
		history.add(0, sql);
		while (history.size() > MAX_HISTORY_SIZE) {
			history.remove(history.size() - 1);
		}
		
		final XMLDocument doc = new XMLDocument("<history/>");
		final XMLElement root = doc.getElement();
		for (String aSQL : history) {
			root.add("SQL").setText(aSQL);
		}
		doc.saveFile(getHistoryFileName());
	}

	private String getDatabasesFileName() {
		return dateiname("databases");
	}

	private String getHistoryFileName() {
		return dateiname("sql");
	}
	
	private String dateiname(String dnt) {
		// TODO other folder?
		return "dacara-" + dnt + ".xml";
	}
}
