package JavaFX;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import Core.*;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.stage.Stage;

/**
 * Class for displaying trailers of resources.
 * 
 * @author Ben Kennard (965798)
 */
public class TrailerViewer extends Application {

	private final Resource res;

	/**
	 * Initialises the TrailerViewer with the given resource.
	 * 
	 * @param selectedResource the resource to display trailers for.
	 */
	public TrailerViewer(Resource selectedResource) {
		this.res = selectedResource;
	}

	/**
	 * Starts the application.
	 */
	@Override
	public void start(Stage stage) {
		WebView webview = new WebView();
		webview.getEngine().load(this.generateURL());
		webview.setPrefSize(640, 390);

		stage.setScene(new Scene(webview));
		stage.show();
		// Disable the web engine, so the video doesn't play in the background
		stage.setOnCloseRequest(e -> {
			webview.getEngine().load(null);
		});

	}

	/**
	 * Generates a URL string for a Youtube search of the given resource.
	 * 
	 * @return a URL string for a Youtube search of the given resource.
	 */
	private String generateURL() {
		return "https://www.youtube.com/embed/?listType=search&list="
			+ res.getTitle() + " " + res.getYear() + "trailer";
	}

}
