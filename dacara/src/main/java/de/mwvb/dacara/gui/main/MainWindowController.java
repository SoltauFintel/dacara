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
import de.mwvb.dacara.gui.Window;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Main window controller
 * 
 * @author Marcus Warm
 */
// TODO MVC?
// TODO idea: content of sql textbox survives program end/restart
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
	private Button execute;
	@FXML
	private Button newWindow;
	@FXML
	private Button biggerSQLField;
	@FXML
	private TableView<ObservableList<String>> grid;
	@FXML
	private Label status;
	
	private boolean historySelectedOn = true;
	private transient ExecuteResult data;
	private transient ObservableList<ObservableList<String>> items;
	private transient ObservableList<TableColumn<ObservableList<String>, ?>> columns;
	private transient long loadTime;
	
	@FXML
	protected void initialize() {
		// Register this Controller instance to corresponding Window instance
		Window.registerController(this);
		
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
	protected void onExecute() {
		items = grid.getItems();
		columns = grid.getColumns();
		execute.setDisable(true);
		data = null;
		sql.getScene().setCursor(Cursor.WAIT);
		status.setText("loading...");
		final String aSQL = sql.getText().trim();
		
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				long start = System.currentTimeMillis();
				data = logic.execute(aSQL);
				loadTime = (System.currentTimeMillis() - start) / 1000;
				return null;
			};
		};
		task.setOnFailed(event -> {
			clearTable(); // should at begin of onExecute()
			
			new Alert(AlertType.ERROR, task.getException().getMessage(), ButtonType.OK).show();

			status.setText("error");
			// duplicate code :-(
			execute.setDisable(false);
			sql.getScene().setCursor(Cursor.DEFAULT);
			sql.requestFocus();
		});
		task.setOnSucceeded(event -> {
			clearTable(); // should at begin of onExecute()
			
			String timetext = "load time: " + formatNumber(loadTime) + " seconds"; // TODO singular
			String statustext;
			if (data.getRecordsAffected() == null) {
				long start = System.currentTimeMillis();
				setupColumns(data, columns);
				addRows(data, items);
				long time = (System.currentTimeMillis() - start) / 1000;
				statustext = formatNumber(items.size()) + " records loaded, "; // TODO singular
				timetext += ", display time: " + formatNumber(time) + " seconds"; // TODO singular
			} else {
				statustext = "Records affected: " + formatNumber(data.getRecordsAffected().intValue()) + ", ";
			}

			cfg.addToHistory(aSQL);
			showHistory();
			
			status.setText(statustext + timetext);
			execute.setDisable(false);
			sql.getScene().setCursor(Cursor.DEFAULT);
			sql.requestFocus();
		});
		new Thread(task).start();
	}

	@FXML
	protected void onNewWindow() {
		new MainWindow().show(new Stage());
	}

	@FXML
	protected void onBiggerSQLField() {
		final double max = 300;
		final double h;
		final String text;
		if (sql.getMaxHeight() == max) {
			h = 100;
			text = "SQL _bigger";
		} else {
			h = max;
			text = "SQL _smaller";
		}
		Platform.runLater(() -> {
			sql.setMaxHeight(h);
			sql.setPrefHeight(h);
			biggerSQLField.setText(text);
		});
	}
	
	private void clearTable() {
		items.clear();
		columns.clear();
	}
	
	private String formatNumber(long no) {
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