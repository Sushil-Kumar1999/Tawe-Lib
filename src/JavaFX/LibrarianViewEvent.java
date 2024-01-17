package JavaFX;

import java.sql.SQLException;

import Core.Customer;
import Core.Datastore;
import Core.Event;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A form used by a Librarian to view and cancel an event.
 * 
 * @author Mike
 */
public class LibrarianViewEvent extends Application {

	private final int MAIN_WINDOW_WIDTH = 400;
	private final int MAIN_WINDOW_HEIGHT = 400;

	private Event currentEvent;

	@Override
	public void start(Stage primaryStage) {
		// Create parent container
		VBox parentContainer = new VBox(30);
		// Construct scene
		Scene scene =
			new Scene(parentContainer, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		parentContainer.setAlignment(Pos.TOP_CENTER);

		// Get event name
		Label eventName = new Label(currentEvent.getName());
		eventName.setFont(new Font(16));
		eventName.setAlignment(Pos.CENTER);

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(8);
		grid.setVgap(20);
		grid.setPadding(new Insets(0, 10, 0, 10));

		// Create field headers
		Label descriptionLabel = new Label("Description:");
		Label dateLabel = new Label("Date and time:");
		Label placesLeftLabel = new Label("Remaining places:");

		// Create and populate the scroll pane with the events description
		Text description = new Text(currentEvent.getDescription());
		description.setWrappingWidth(168);
		ScrollPane descriptionContainer = new ScrollPane();
		descriptionContainer.setContent(description);
		descriptionContainer.setMaxWidth(170);
		descriptionContainer.setMinHeight(100);

		// Populate the remaining fields
		Label date = new Label(currentEvent.getDate().toString());
		Label placesLeft =
			new Label(String.valueOf(currentEvent.getPlacesLeft()));

		Label successMessage = new Label("[message]");
		successMessage.setTextFill(Color.GREEN);
		successMessage.setVisible(false);

		Button cancelEvent = new Button("Cancel Event");

		// Cancel the event when the button is pressed
		cancelEvent.setOnAction(e -> {
			try {
				// Call datastore method
				Datastore.cancelEvent(currentEvent.getUniqueID());

				// Display success message
				successMessage.setText("Event cancelled successfully" + '\n'
					+ "All bookings removed");
				successMessage.setTextFill(Color.GREEN);
				successMessage.setVisible(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// Add objects to the grid
		grid.addRow(0, descriptionLabel, descriptionContainer);
		grid.addRow(1, dateLabel, date);
		grid.addRow(2, placesLeftLabel, placesLeft);

		parentContainer.getChildren().addAll(eventName, grid, cancelEvent,
			successMessage);

		// Construct the window.
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("View Event: " + currentEvent.getName());
		primaryStage.show();

	}

	/**
	 * Sets the current event of the form to 'e'
	 * 
	 * @param e the event to set as current.
	 */
	public void setEvent(Event e) {
		currentEvent = e;
	}

	public static void main(String[] args) {
		launch(args);

	}

}
