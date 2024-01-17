package JavaFX;
import java.io.File;
import Core.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
 * Form that allows a librarian to add a new VideoGame to the system.
 * 
 * @author Billy Roberts
 */
public class CreateVideoGame extends Application {


	@Override
	public void start(Stage primaryStage) {

		// Create the Form pane for librarian details.
		GridPane gridPane = createVideoGameDetailFormPane();

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 400, 450);

		// Add UI controls to form
		addUIControls(gridPane);

		primaryStage.setTitle("Create VideoGame");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * GridPane layout for designing the CreateVideoGame form.
	 * 
	 * @return A GridPane for the form.
	 */
	private GridPane createVideoGameDetailFormPane() {

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
	 * @param gridPane that will be changed.
	 */
	private void addUIControls(GridPane gridPane) {

		//Add the title for the librarian details
		Text title = new Text("VideoGame Details:");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for librarian details
		Label titleLbl = new Label("Title:");
		TextField titleTextField = new TextField();

		Label yearLbl = new Label("Release Year:");
		TextField yearTextField = new TextField();

		Label publisherLbl = new Label("Publisher:");
		TextField publisherTextField = new TextField();

		Label genreLbl = new Label("Genre:");
		TextField genreTextField = new TextField();

		Label certificateLbl = new Label("Certificate Rating:");
		TextField certificateTextField = new TextField();

		Label multiplayerSupportLbl = new Label("Multiplayer Support:");
		CheckBox multiplayerCB = new CheckBox();


		Label thumbnailImagePathLbl = new Label("Thumbnail Image Path:");
		TextField thumbnailImagePathField = new TextField();

		Label confirmationMessage = new Label();
		confirmationMessage.setTextFill(Color.DARKGREEN);

		// Add submit button to submit the librarian details
		Button btn = new Button("Submit");
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
		gridPane.add(titleLbl, 0, 2);
		gridPane.add(titleTextField, 1, 2);
		gridPane.add(yearLbl, 0, 3);
		gridPane.add(yearTextField, 1, 3);
		gridPane.add(publisherLbl, 0, 4);
		gridPane.add(publisherTextField, 1, 4);
		gridPane.add(genreLbl, 0, 5);
		gridPane.add(genreTextField, 1, 5);
		gridPane.add(certificateLbl, 0, 6);
		gridPane.add(certificateTextField, 1, 6);
		gridPane.add(multiplayerSupportLbl, 0, 7);
		gridPane.add(multiplayerCB, 1, 7);
		gridPane.add(thumbnailImagePathLbl, 0, 8);
		gridPane.add(thumbnailImagePathField, 1, 8);
		gridPane.add(hboxBtn, 1, 9);
		gridPane.add(confirmationMessage, 1, 10);

		//When the button is clicked.
		btn.setOnAction(e  -> {
			try {
				File thumbPath = new File(thumbnailImagePathField.getText());
				//Check thumbnail path is valid
				if (!thumbPath.exists()) {
					throw new FileNotFoundException(
							"Thumbnail image path invalid!");
				}
				Resource createdResource = 
						Datastore.createVideoGame(titleTextField.getText(), 
								Integer.parseInt(yearTextField.getText()),
								publisherTextField.getText(), 
								genreTextField.getText(), 
								certificateTextField.getText(),
								multiplayerCB.isSelected());
				//Copy image from thumbnail path to correct destination
				Files.copy(thumbPath.toPath(),
						new File(createdResource.getThumbnailImagePath()).toPath(),
						StandardCopyOption.REPLACE_EXISTING);

				confirmationMessage.setText("New VideoGame added succesfully");
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

	}

	public static void main(String[] args) {

		launch(args);
	}
}