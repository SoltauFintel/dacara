package de.mwvb.dacara.gui.main;

import java.io.Closeable;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;

import de.mwvb.dacara.Configuration;
import de.mwvb.dacara.ExecuteResult;
import de.mwvb.dacara.SQLExecutor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;

/**
 * Main window controller
 * 
 * @author Marcus Warm
 */
// TODO MVC?
public class MainWindowController {
	@Inject
	private SQLExecutor logic;
	@Inject
	private Configuration cfg;

	@FXML
	private ComboBox<String> databases;
	@FXML
	private ComboBox<String> history;
	@FXML
	private TextArea sql;
	@FXML
	private TableView<ObservableList<String>> grid;
	@FXML
	private Label status;
	
	private boolean historySelectedOn = true;
	
	@FXML
	protected void initialize() {
		databases.setPromptText("Databases");
		for (String name : cfg.getDatabaseNames()) {
			databases.getItems().add(name);
		}
		if (cfg.getCurrentDatabase() >= 0) {
			databases.getSelectionModel().clearAndSelect(cfg.getCurrentDatabase());
		} else {
			status.setText("No databases configured.");
		}
		
		history.setPromptText("SQL History");
		showHistory();
		if (!history.getItems().isEmpty()) {
			history.getSelectionModel().clearAndSelect(0);
			historySelected();
		}
		
		Platform.runLater(() -> sql.requestFocus());
		
		// TODO load driver in parallel thread, so executing the first command will be faster
	}
	
	@FXML
	protected void databaseSelected() {
		cfg.setCurrentDatabase(databases.getSelectionModel().getSelectedIndex());
	}

	@FXML
	protected void historySelected() {
		if (historySelectedOn) {
			sql.setText(history.getSelectionModel().getSelectedItem());
		}
	}
	
	@FXML
	protected void execute() {
		// TODO how?  WAIT cursor
		try {
			// TODO how?  status "loading..."
			// TODO lock controls
			final ObservableList<ObservableList<String>> items = grid.getItems();
			items.clear();
			final ObservableList<TableColumn<ObservableList<String>, ?>> columns = grid.getColumns();
			columns.clear();

			final String aSQL = sql.getText().trim();
			ExecuteResult data = logic.execute(aSQL);
			// TODO display execution time
			
			if (data.getRecordsAffected() == null) {
				setupColumns(data, columns);
				addRows(data, items);
				// TODO show display time
			} else {
				status.setText("Records affected: " + formatNumber(data.getRecordsAffected()));
			}

			cfg.addToHistory(aSQL);
			showHistory();
		} catch(Exception e) {
			new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK).show();
			status.setText("error");
		} finally {
			// TODO unlock controls
			// TODO DEFAULT cursor
			sql.requestFocus();
		}
	}
	
	private String formatNumber(int no) {
		return NumberFormat.getIntegerInstance().format(no);
	}
	
	private void setupColumns(ExecuteResult data,
			final ObservableList<TableColumn<ObservableList<String>, ?>> columns) {
		for (int i = 0; i < data.getColumnHeaders().size(); i++) {
			TableColumn<ObservableList<String>, String> col = new TableColumn<>(data.getColumnHeaders().get(i));
			col.setCellValueFactory(getCellValueFactory(i));
			columns.add(col);
		}
	}

	private Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>> getCellValueFactory(
			final int i) {
		return new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<ObservableList<String>, String> param) {
				final String s = param.getValue().get(i);
				return new SimpleStringProperty(s == null ? null : s.toString());
			}
		};
	}

	private void addRows(ExecuteResult data, final ObservableList<ObservableList<String>> items) {
		final Iterator<List<String>> iter = data.getRows();
		try {
			int n = 0;
			while (iter.hasNext()) {
				items.add(FXCollections.observableArrayList(iter.next()));
				n++;
			}
			if (n == 1) {
				status.setText(n + " record");
			} else {
				status.setText(formatNumber(n) + " records");
			}
		} finally {
			close(iter);
		}
	}

	private void close(final Iterator<List<String>> iter) {
		if (iter instanceof Closeable) {
			try {
				((Closeable) iter).close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void showHistory() {
		historySelectedOn = false;
		// I'm not sure which behaviour would be good - int index = history.getSelectionModel().getSelectedIndex();
		final ObservableList<String> items = history.getItems();
		items.clear();
		for (String a : cfg.getHistoryList()) {
			items.add(a);
		}
		//history.getSelectionModel().clearAndSelect(-1);
		historySelectedOn = true;
	}
}