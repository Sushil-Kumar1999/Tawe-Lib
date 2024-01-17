package JavaFX;

import java.sql.SQLException;
import Core.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Create an interactive javafx application with multiple UI controls. Create
 * Librarian class that to add a new stuff.
 * 
 * @author Ali Alowais
 */
public class CreateLibrarian extends Application {

	@Override
	public void start(Stage primaryStage) {

		// Create the Form pane for librarian details.
		GridPane gridPane = createUserDetailFormPane();

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 400, 450);

		// Add UI controls to form
		addUIControls(gridPane);

		primaryStage.setTitle("Create New Librarian");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * GridPane layout for designing the librarian form.
	 * 
	 * @return gridPane.
	 */
	private GridPane createUserDetailFormPane() {

		// Instantiate a new Grid Pane
		GridPane gridPane = new GridPane();
		// Position the pane at the center of the screen
		gridPane.setAlignment(Pos.CENTER);

		// Set the horizontal gap between columns
		gridPane.setHgap(10);

		// Set the vertical gap between rows
		gridPane.setVgap(10);

		// Set a padding on each side
		gridPane.setPadding(new Insets(25, 25, 25, 25));

		return gridPane;
	}

	/**
	 * Add UI Controls to the layout.
	 * 
	 * @param gridPane the grid pane to add the controls to.
	 */
	private void addUIControls(GridPane gridPane) {

		// Add the title for the librarian details
		Text title = new Text("Librarian Details:");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for librarian details
		Label userName = new Label("Username:");
		TextField userTextField = new TextField();

		Label firstName = new Label("Firstname:");
		TextField firstNameTextField = new TextField();

		Label lastName = new Label("Lastname:");
		TextField lastNameTextField = new TextField();

		Label number = new Label("Telephone Number:");
		TextField numberTextField = new TextField();

		Label address = new Label("Address:");
		TextField addressTextField = new TextField();

		Label postalCode = new Label("Postal Code:");
		TextField postalCodeTextField = new TextField();

		Label employmentDate = new Label("Employment Date:");
		DatePicker employmentDateSelect = new DatePicker();

		Label staffNumber = new Label("Staff Number:");
		TextField staffNumberTextField = new TextField();

		Label confirmationMessage = new Label();
		confirmationMessage.setTextFill(Color.DARKGREEN);

		// Add submit button to submit the librarian details
		Button btn = new Button("Submit");
		btn.setOnAction(e -> {
			try {
				Datastore.createLibrarian(userTextField.getText(),
					firstNameTextField.getText(), lastNameTextField.getText(),
					numberTextField.getText(), addressTextField.getText(),
					User.getDefaultAvatar("Avatar 1"),
					Integer.parseInt(staffNumberTextField.getText()),
					employmentDateSelect.getValue());
				confirmationMessage.setText("New librarian added succesfully");
			} catch (SQLException ev) {
				System.out.println(ev.getMessage());
			}
		});

		HBox hboxBtn = new HBox(10);
		hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hboxBtn.getChildren().add(btn);

		Path path = new Path();

		// Moving to the starting point
		MoveTo moveTo = new MoveTo();
		moveTo.setX(125.0);
		moveTo.setY(150.0);

		// Instantiating the HLineTo class
		HLineTo hLineTo = new HLineTo();

		// Setting the properties of the path element horizontal line
		hLineTo.setX(10.0);

		// Adding the path elements to Observable list of the Path class
		path.getElements().add(moveTo);
		path.getElements().add(hLineTo);

		gridPane.add(title, 0, 0, 2, 1);
		gridPane.add(path, 0, 1, 2, 1);
		gridPane.add(userName, 0, 2);
		gridPane.add(userTextField, 1, 2);
		gridPane.add(firstName, 0, 3);
		gridPane.add(firstNameTextField, 1, 3);
		gridPane.add(lastName, 0, 4);
		gridPane.add(lastNameTextField, 1, 4);
		gridPane.add(number, 0, 5);
		gridPane.add(numberTextField, 1, 5);
		gridPane.add(address, 0, 6);
		gridPane.add(addressTextField, 1, 6);
		gridPane.add(postalCode, 0, 7);
		gridPane.add(postalCodeTextField, 1, 7);
		gridPane.add(employmentDate, 0, 8);
		gridPane.add(employmentDateSelect, 1, 8);
		gridPane.add(staffNumber, 0, 9);
		gridPane.add(staffNumberTextField, 1, 9);
		gridPane.add(hboxBtn, 1, 10);
		gridPane.add(confirmationMessage, 1, 11);
	}

	public static void main(String[] args) {

		launch(args);
	}
}