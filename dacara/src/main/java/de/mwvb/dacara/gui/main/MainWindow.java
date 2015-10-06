package de.mwvb.dacara.gui.main;

import de.mwvb.dacara.Start;
import de.mwvb.dacara.gui.Window;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Main window
 * 
 * @author Marcus Warm
 */
public class MainWindow extends Window<MainWindowController> {
	
	public MainWindow() {
		super("Dacara " + Start.VERSION, 800, 500, 400, 400);
	}
	
	@Override
	protected void keyBindings(Scene scene) {
		scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ev) {
				final KeyCode code = ev.getCode();
				if (code.equals(KeyCode.F5)) {
					getController().onExecute();
				} else if (code.equals(KeyCode.F6)) {
					getController().onNewWindow();
				} else if (code.equals(KeyCode.F12)) {
					getController().onBiggerSQLField();
				}
			}
		});
	}
}
