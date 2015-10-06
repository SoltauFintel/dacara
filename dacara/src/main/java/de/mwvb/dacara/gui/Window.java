package de.mwvb.dacara.gui;

import java.io.IOException;

import de.mwvb.dacara.Start;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * JavaFX window base class
 * 
 * @author Marcus Warm
 */
public abstract class Window {
	private final String title;
	private final int width;
	private final int height;
	private final int minWidth;
	private final int minHeight;
	
	public Window(String title, int width, int height, int minWidth, int minHeight) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}
	
	public void show(Stage stage) {
		stage.getIcons().add(new Image(getClass().getResourceAsStream(getClass().getSimpleName() + ".png")));
		stage.setScene(new Scene(root(), width, height));
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.setTitle(title);
        // TODO restore window position and size
        stage.show();
	}
	
	protected Parent root() {
		try {
			Callback<Class<?>, Object> guiceControllerFactory = new Callback<Class<?>, Object>() {
				@Override
				public Object call(Class<?> clazz) {
					return Start.getInjector().getInstance(clazz);
				}
			};
			return FXMLLoader.load(getClass().getResource(getClass().getSimpleName() + ".fxml"),
					null, new JavaFXBuilderFactory(), guiceControllerFactory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
