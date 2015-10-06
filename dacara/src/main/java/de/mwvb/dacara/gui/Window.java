package de.mwvb.dacara.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
public abstract class Window<CTR> {
	/** ctr, latestedId and myId is for Window <-> Controller binding */
	private static Map<Integer, Object> ctr = new HashMap<>();
	private static Integer latestId;
	private final Integer myId;
	private final String title;
	private final int width;
	private final int height;
	private final int minWidth;
	private final int minHeight;
	
	public Window(String title, int width, int height, int minWidth, int minHeight) {
		this.myId = hashCode();
		latestId = this.myId;
		this.title = title;
		this.width = width;
		this.height = height;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}
	
	public static void registerController(Object controller) {
		ctr.put(latestId, controller);
	}
	
	public void show(Stage stage) {
		stage.getIcons().add(new Image(getClass().getResourceAsStream(getClass().getSimpleName() + ".png")));
		Scene scene = new Scene(root(), width, height);
		stage.setScene(scene);
		keyBindings(scene);
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

	protected abstract void keyBindings(final Scene scene);
	
	@SuppressWarnings("unchecked")
	protected CTR getController() {
		return (CTR) ctr.get(myId);
	}
}
