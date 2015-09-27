package de.mwvb.dacara.gui;

import java.io.IOException;

import de.mwvb.dacara.gui.main.MainWindow;
import javafx.stage.Stage;

/**
 * Creates and shows MainWindow.
 * 
 * @author Marcus Warm
 */
public class Application extends javafx.application.Application {
	
	@Override
	public void start(Stage stage) throws IOException {
		new MainWindow().show(stage);
	}
}
