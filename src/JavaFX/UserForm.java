package JavaFX;

import java.sql.SQLException;
import Core.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Create an interactive javafx application with multiple UI controls. Allows a
 * librarian to register a new user.
 * 
 * @author Ali Alowais
 * @version 1.0
 */
public class UserForm extends Application {

	private Customer customerReference;
	private boolean isEdited = false;

	/**
	 * @param customerReference The customer whose details are being edited.
	 */
	public void setCustomerReference(Customer customerReference) {
		this.customerReference = customerReference;
	}

	/**
	 * Set this parameter to true if the form is being used to edit a users
	 * details. The parameter is set to false by default.
	 * 
	 * @param isEdited if the details have been edited.
	 */
	public void setEdited(boolean isEdited) {
		this.isEdited = isEdited;
	}

	@Override
	public void start(Stage primaryStage) {

		// Create the Form pane for user details.
		GridPane gridPane = createUserDetailFormPane();

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 400, 375);

		// Add UI controls to form
		addUIControls(gridPane);

		primaryStage.setTitle("User Detail Form");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * GridPane layout for designing the user form.
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
	 * Add UI Contolrs to the layout.
	 * 
	 * @param gridPane the pane to add the controls to.
	 */
	private void addUIControls(GridPane gridPane) {

		// Add the title for the user details
		Text title = new Text("User Details:");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for user details
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

		Text successMessage = new Text();

		// Add submit button to submit the user details
		Button btn = new Button("Submit");
		btn.setOnAction(e -> {
			try {
				Datastore.createCustomer(userTextField.getText(),
					firstNameTextField.getText(), lastNameTextField.getText(),
					numberTextField.getText(), addressTextField.getText(),
					User.getDefaultAvatar("Avatar 1"));
				successMessage.setFill(Color.GREEN);
				successMessage.setText("User created");

			} catch (SQLException ev) {
				System.out.println(ev.getMessage());
			}
		});

		HBox hboxBtn = new HBox(10);
		hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hboxBtn.getChildren().addAll(btn, successMessage);

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
		gridPane.add(hboxBtn, 1, 9);

		if (isEdited) {
			userTextField.setText(customerReference.getUsername());
			firstNameTextField.setText(customerReference.getFirstName());
			lastNameTextField.setText(customerReference.getSurname());
			numberTextField.setText(customerReference.getMobileNumber());
			addressTextField.setText(customerReference.getAddress());

			btn.setOnAction(e -> {
				try {
					customerReference.editUser(userTextField.getText(),
						firstNameTextField.getText(),
						lastNameTextField.getText(), numberTextField.getText(),
						addressTextField.getText());
					successMessage.setFill(Color.GREEN);
					successMessage.setText("User edited");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});
		}
	}

	public static void main(String[] args) {

		launch(args);
	}
}