package JavaFX;

import java.sql.SQLException;
import java.time.LocalDateTime;

import Core.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
 * Create an interactive javafx application with multiple UI controls. A class
 * that represent a login form for user and librarian.
 *
 * @author Ali Alowais
 * @version 1.0
 */
public class LoginForm extends Application {

	@Override
	/**
	 * Starts the application.
	 */
	public void start(Stage primaryStage) {

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

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 300, 275);

		//// Add the title for login form
		Text title = new Text("Please Login:");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label and textField for login
		Label userName = new Label("Username:");
		TextField userTextField = new TextField();
		userTextField.setPromptText("Enter your username");
		userTextField.setFocusTraversable(false);

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

		gridPane.setGridLinesVisible(false);

		// Add Sign In and Exit Button
		Button signInButton = new Button("Sign In");
		Button exitButton = new Button("Exit");
		exitButton.setMinWidth(50);

		// HBox object containing the signInButton and exitButton.
		HBox hboxBtn = new HBox(10);
		hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hboxBtn.getChildren().addAll(signInButton, exitButton);
		gridPane.add(hboxBtn, 1, 4);

		final Text target = new Text();
		gridPane.add(target, 1, 6);

		// Set up the event handlers
		signInButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
				throws IllegalArgumentException {
				logIn(primaryStage, userTextField, target);
			}
		});

		userTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent enter)
				throws IllegalArgumentException {
				if (enter.getCode().equals(KeyCode.ENTER)) {
					logIn(primaryStage, userTextField, target);
				}
			}
		});

		exitButton.setOnAction(event -> {
			Platform.exit();
		});

		// The title of primary stage to the login form
		primaryStage.setTitle("Login Form");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Attempts to log the user in.
	 * 
	 * @param primaryStage  the stage of this application.
	 * @param userTextField the text field the user has entered their username
	 *                      in.
	 * @param target        the text used to display error messages.
	 * @throws IllegalArgumentException if the logIn action fails.
	 */
	public void logIn(Stage primaryStage, TextField userTextField, Text target)
		throws IllegalArgumentException {
		try {
			String username = userTextField.getText();
			User user = Datastore.logIn(username);
			if (user.isLibrarian()) {
				LibrarianUI librarianUI = new LibrarianUI();
				librarianUI.start(primaryStage);
			} else {

				LocalDateTime loginTime = ((Customer) user).getLastLogin();

				UserUI userUI = new UserUI(loginTime);
				((Customer) user).updateLastSeen();
				try {
					userUI.start(primaryStage);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IllegalArgumentException e) {
			target.setFill(Color.FIREBRICK);
			target.setText(e.getMessage());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * For testing purposes only.
	 * 
	 * @param args unused.
	 */
	public static void main(String[] args) {

		launch(args);
	}

}
