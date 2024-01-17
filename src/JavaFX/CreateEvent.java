package JavaFX;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import Core.Datastore;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
/**
 * A form used by a Librarian to create an event
 * @author Mike
 */
public class CreateEvent extends Application{
	private final int MAIN_WINDOW_WIDTH = 400;
	private final int MAIN_WINDOW_HEIGHT = 400;

	@Override
	public void start(Stage primaryStage) {
		//Create parent container
		GridPane parentContainer = new GridPane();
		parentContainer.setHgap(10);
		parentContainer.setVgap(10);
		parentContainer.setPadding(new Insets(10, 10, 0, 10));
		
		//Create header
		Label title = new Label("Enter details of new event");
		title.setFont(new Font(17));
		
		//Create field labels
		Label nameLabel = new Label("Enter event name:");
		Label dateLabel = new Label("Select event date:");
		Label timeLabel = new Label("Select event time:");
		Label descriptionLabel = new Label("Enter event description:");
		Label maxAttendeesLabel = new Label("Enter maximum attendees:");
		Label feedbackLabel = new Label();
		
		//Create field inputs
		TextField nameInput = new TextField();
		DatePicker dateInput = new DatePicker();
		TextField descriptionInput = new TextField();
		TextField maxAttendeesInput = new TextField();
		
		//Create list of possible event times
		ObservableList<String> times = FXCollections.observableArrayList(
				"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", 
				"13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16;30", "17:00");
		//Create time input object
		ComboBox<String> timeInput = new ComboBox<>(times);
		timeInput.getSelectionModel().selectFirst();
		
		Button createEvent = new Button("Create event");
		
		//Create the event when the button is clicked
		createEvent.setOnAction(e -> {
			try {
				//Check for blank fields
				if (nameInput.getText() == null || dateInput.getValue() == null 
						|| maxAttendeesInput.getText() == null) {
					throw new IllegalArgumentException("1 or more necessary fields left blank");
				}

				
				/*Dates are represented by milliseconds, so new ones need to be created by 
				summing the hours and minutes in milliseconds and the date in milliseconds*/			
				Date d = Date.valueOf(dateInput.getValue());
				int eventHour = Integer.parseInt(timeInput.getValue().substring(0, 2));
				int eventMin = Integer.parseInt(timeInput.getValue().substring(3, 5));
				long dateInMs = (long) (d.getTime() + (eventHour * 36E5) + (eventMin * 60000));
				Timestamp eventDateTime = new Timestamp(dateInMs);
				
				Timestamp today = new Timestamp(System.currentTimeMillis());
				if (eventDateTime.before(today)) {
					throw new IllegalArgumentException("Cannot book events in the past");
				}
				
				//Call datastore method
				Datastore.createEvent(nameInput.getText(), eventDateTime, descriptionInput.getText(), 
						Integer.parseInt(maxAttendeesInput.getText()));
				
				//Clear input fields
				nameInput.clear();
				dateInput.setValue(null);
				descriptionInput.clear();
				maxAttendeesInput.clear();
				timeInput.getSelectionModel().selectFirst();
				feedbackLabel.setText("Event created successfully");
				feedbackLabel.setTextFill(Color.GREEN);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				feedbackLabel.setText(e1.getMessage());
				feedbackLabel.setTextFill(Color.RED);
			}
			

		});
		
		//Add objects to parent container
		parentContainer.addRow(0, title);
		parentContainer.addRow(1, nameLabel, nameInput);
		parentContainer.addRow(2, dateLabel, dateInput);
		parentContainer.addRow(3, timeLabel, timeInput);
		parentContainer.addRow(4, descriptionLabel, descriptionInput);
		parentContainer.addRow(5, maxAttendeesLabel, maxAttendeesInput);		
		parentContainer.addRow(6, createEvent);
		parentContainer.addRow(7, feedbackLabel);
		
		//Construct the window.
		Scene scene = new Scene(parentContainer, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Create event");
		
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
