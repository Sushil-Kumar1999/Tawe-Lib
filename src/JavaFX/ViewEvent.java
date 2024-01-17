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
 * A form used by a user to view and to create/cancel a booking on an event
 * 
 * @author Mike
 */
public class ViewEvent extends Application {

	private final int MAIN_WINDOW_WIDTH = 400;
	private final int MAIN_WINDOW_HEIGHT = 400;

	private Event currentEvent;

	@Override
	public void start(Stage primaryStage) {
		// Create parent container for the form
		VBox parentContainer = new VBox(30);
		// Construct the scene
		Scene scene =
			new Scene(parentContainer, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);

		// Get the customer that is currently logged in from the datastore
		Customer currentCustomer = (Customer) Datastore.getCurrentUser();

		parentContainer.setAlignment(Pos.TOP_CENTER);

		// Get the event name
		Label eventName = new Label(currentEvent.getName());
		eventName.setFont(new Font(16));
		eventName.setAlignment(Pos.CENTER);

		// Create grid containing event information
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(8);
		grid.setVgap(20);
		grid.setPadding(new Insets(0, 10, 0, 10));

		// Create information titles
		Label descriptionLabel = new Label("Description:");
		Label dateLabel = new Label("Date and time:");
		Label placesLeftLabel = new Label("Remaining places:");

		// Create and populate the scroll pane containing the event description
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

		Button bookPlace = new Button("Book a place on this event");
		Button removePlace =
			new Button("Remove your place" + '\n' + "from this event");

		/*
		 * If a customer is already booked on this event, disable the bookPlace
		 * button and enable the removePlace button. If not the case, do the
		 * opposite.
		 */
		if (currentCustomer.getUpcommingEvents().contains(currentEvent)) {
			bookPlace.setDisable(true);
			removePlace.setDisable(false);
		} else {
			bookPlace.setDisable(false);
			removePlace.setDisable(true);
		}

		// Book the customers place on the event when the button is pressed
		bookPlace.setOnAction(e -> {
			try {
				// Call datastore method
				Datastore.createBooking(currentEvent.getUniqueID(),
					Datastore.getCurrentUser().getUniqueId());

				removePlace.setDisable(false);
				bookPlace.setDisable(true);

				// Display feedback message
				successMessage.setVisible(true);
				successMessage.setText("Place Booked Successfully");
				successMessage.setTextFill(Color.GREEN);

				placesLeft
					.setText(String.valueOf(currentEvent.getPlacesLeft()));
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (IndexOutOfBoundsException e2) {
				// Display error message
				successMessage.setVisible(true);
				successMessage.setText(e2.getMessage());
				successMessage.setTextFill(Color.RED);
			}
		});

		// Remove the customers place on the event when the button is pressed
		removePlace.setOnAction(e -> {
			try {
				// Call datastore method
				Datastore.removeBooking(currentEvent.getUniqueID(),
					Datastore.getCurrentUser().getUniqueId());

				bookPlace.setDisable(false);
				removePlace.setDisable(true);

				// Display feedback message
				successMessage.setVisible(true);
				successMessage.setText("Place Removed Successfully");
				successMessage.setTextFill(Color.GREEN);

				placesLeft
					.setText(String.valueOf(currentEvent.getPlacesLeft()));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		// Add the objects to the grid
		grid.addRow(0, descriptionLabel, descriptionContainer);
		grid.addRow(1, dateLabel, date);
		grid.addRow(2, placesLeftLabel, placesLeft);
		grid.addRow(3, bookPlace, removePlace);

		parentContainer.getChildren().addAll(eventName, grid, successMessage);

		// Construct the window.
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("View Event: " + currentEvent.getName());
		primaryStage.show();

	}

	/**
	 * Sets the current event of the form to 'e'.
	 * 
	 * @param e the event to set.
	 */
	public void setEvent(Event e) {
		currentEvent = e;
	}

	public static void main(String[] args) {
		launch(args);

	}

}
