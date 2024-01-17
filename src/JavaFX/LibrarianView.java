package JavaFX;

import java.io.FileInputStream;
import Core.*;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Create an interactive javafx application with multiple UI controls. A class
 * that represent a details of librarians.
 * 
 * @author Ali Alowais
 * @version 1.0
 */
public class LibrarianView extends Application {

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {

		// Create the Form pane for librarian details.
		GridPane gridPane = createUserDetailFormPane();

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 800, 700);

		// Add UI controls to form
		addUIControls(gridPane);

		primaryStage.setTitle("Librarian View");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * GridPane layout for designing the librarians details.
	 * 
	 * @return gridPane.
	 */
	private GridPane createUserDetailFormPane() {

		// Instantiate a new Grid Pane
		GridPane gridPane = new GridPane();

		// Position the pane at the center of the screen
		gridPane.setAlignment(Pos.TOP_CENTER);

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
	 * @param gridPane the grid pane to add the controls to.
	 * @throws FileNotFoundException remove the error if pathname does not
	 *                               exist.
	 */
	private void addUIControls(GridPane gridPane)
		throws FileNotFoundException {

		// Add the title of librarians details
		Label title = new Label("Librarian Details:");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for librarians details
		Label username = new Label("username:");
		Label usernameValue = new Label();

		Label firstname = new Label("firstName:");
		Label firstnameValue = new Label();

		Label surname = new Label("surname:");
		Label surnameValue = new Label();

		Label mobileNumber = new Label("mobileNumber:");
		Label mobileNumberValue = new Label();

		Label address = new Label("address:");
		Label addressValue = new Label();

		Label staffNumber = new Label("staffNumber:");
		Label staffNumberValue = new Label();

		Label employmentDate = new Label("employmentDate:");
		Label employmentDateValue = new Label();

		String profileImagePath = "images/resource_thumbs/1.jpg";
		Image image = new Image(new FileInputStream(profileImagePath));
		ImageView imageView = new ImageView(image);

		// setting the fit height and width of the image view
		imageView.setFitHeight(100);
		imageView.setFitWidth(50);

		// Setting the preserve ratio of the image view
		imageView.setPreserveRatio(true);

		Label updateLibrarian = new Label("Edit Librarian:");
		Button updateLibrarianeBtn = new Button("Edit");
		HBox hboxBtn1 = new HBox(10);

		// HBox that contain a bottom to edit the librarian.
		hboxBtn1.setAlignment(Pos.BOTTOM_RIGHT);
		hboxBtn1.getChildren().add(updateLibrarianeBtn);

		gridPane.add(title, 0, 0, 2, 1);
		gridPane.add(returnLine("Line1"), 0, 1, 5, 1);
		gridPane.add(imageView, 0, 2, 2, 1);
		gridPane.add(username, 0, 5);
		gridPane.add(usernameValue, 1, 5);
		gridPane.add(firstname, 2, 5);
		gridPane.add(firstnameValue, 3, 5);
		gridPane.add(surname, 0, 6);
		gridPane.add(surnameValue, 1, 6);
		gridPane.add(mobileNumber, 2, 6);
		gridPane.add(mobileNumberValue, 3, 6);
		gridPane.add(address, 0, 7);
		gridPane.add(addressValue, 1, 7);
		gridPane.add(staffNumber, 2, 7);
		gridPane.add(staffNumberValue, 3, 7);
		gridPane.add(employmentDate, 0, 8);
		gridPane.add(employmentDateValue, 1, 8);
		gridPane.add(returnLine("Line2"), 0, 10, 5, 1);
		gridPane.add(updateLibrarian, 0, 11, 2, 1);
		gridPane.add(updateLibrarianeBtn, 1, 11, 2, 1);

	}

	/**
	 * This method to create a line between the layout.
	 * 
	 * @param id. a string of id.
	 * @return line.
	 */
	private static Line returnLine(String id) {

		Line line = new Line(0, 0, 500, 0);
		line.setStroke(Color.BLACK);
		line.maxWidth(10);
		line.setId(id);
		return line;
	}

	public static void main(String[] args) {
		launch(args);
	}
}