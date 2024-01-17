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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Create an interactive javafx application with multiple UI controls. A class
 * that represent a details of users.
 * 
 * @author Ali Alowais
 * @version 1.0
 */

public class UserView extends Application {

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {

		// Create the Form pane for librarian details.
		GridPane gridPane = createUserDetailFormPane();

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 800, 700);

		// Add UI controls to form
		addUIControls(gridPane);

		primaryStage.setTitle("User View");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * GridPane layout for designing the users details.
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
	 * @param gridPane the gridpane to add the controls to.
	 * @throws FileNotFoundException remove the error if pathname does not
	 *                               exist.
	 */

	private void addUIControls(GridPane gridPane)
		throws FileNotFoundException {

		// Add the title of users details
		Label title = new Label("User Details:");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for users details
		Label Username = new Label("Username:");
		Label UsernameValue = new Label();

		Label Firstname = new Label("Firstname:");
		Label FirstnameValue = new Label();

		Label Lastname = new Label("Lastname:");
		Label LastnameValue = new Label();

		Label TelephoneNumber = new Label("Telephone Number:");
		Label TelephoneNumberValue = new Label();

		Label Address = new Label("Address:");
		Label AddressValue = new Label();

		Label PostalCode = new Label("Postal Code:");
		Label PostalCodeValue = new Label();

		Label UNIQUE_ID = new Label("UNIQUE_ID:");
		Label UNIQUE_IDValue = new Label();

		String profileImagePath = "images/resource_thumbs/1.jpg";
		Image image = new Image(new FileInputStream(profileImagePath));
		ImageView imageView = new ImageView(image);

		// setting the fit height and width of the image view
		imageView.setFitHeight(100);
		imageView.setFitWidth(50);

		// Setting the preserve ratio of the image view
		imageView.setPreserveRatio(true);

		Label requestResource = new Label("Edit this Customer:");
		Button requestResourceBtn = new Button("Edit");

		// HBox that contain a bottom to edit the the user for librarian.
		HBox hboxBtn = new HBox(10);
		hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hboxBtn.getChildren().add(requestResourceBtn);

		gridPane.add(title, 0, 0, 2, 1);
		gridPane.add(returnLine("Line1"), 0, 1, 5, 1);
		gridPane.add(imageView, 0, 2, 2, 1);
		gridPane.add(Username, 0, 5);
		gridPane.add(UsernameValue, 1, 5);
		gridPane.add(Firstname, 2, 5);
		gridPane.add(FirstnameValue, 3, 5);
		gridPane.add(Lastname, 0, 6);
		gridPane.add(LastnameValue, 1, 6);
		gridPane.add(TelephoneNumber, 2, 6);
		gridPane.add(TelephoneNumberValue, 3, 6);
		gridPane.add(Address, 0, 7);
		gridPane.add(AddressValue, 1, 7);
		gridPane.add(PostalCode, 2, 7);
		gridPane.add(PostalCodeValue, 3, 7);
		gridPane.add(UNIQUE_ID, 0, 8);
		gridPane.add(UNIQUE_IDValue, 1, 8);
		gridPane.add(returnLine("Line2"), 0, 9, 5, 1);
		gridPane.add(addCurrentItem(), 0, 11, 10, 1);
		gridPane.add(returnLine("Line3"), 0, 13, 5, 1);
		gridPane.add(requestResource, 0, 15, 2, 1);
		gridPane.add(requestResourceBtn, 2, 15, 2, 1);

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

	/**
	 * This the table for user to view all the information.
	 * 
	 * @return The current objects in it.
	 */
	private GridPane addCurrentItem() {
		// VBox that will contain all elements for the tab.
		VBox parentContainer = new VBox();
		parentContainer.setAlignment(Pos.TOP_CENTER);

		// Create tab title.
		Label title = new Label("Current Items");
		title.setFont(new Font(16));
		parentContainer.getChildren().add(title);

		// GridPane that will contain all tables and headers in the tab
		GridPane centreGrid = new GridPane();
		centreGrid.setHgap(5);
		centreGrid.setAlignment(Pos.CENTER);

		// Create and add table headers.
		Label loanedHeader = new Label("Loaned Items");
		Label reservedHeader = new Label("Reserved Items");
		Label requestedHeader = new Label("Requested Items");

		centreGrid.add(loanedHeader, 0, 0);
		centreGrid.add(requestedHeader, 1, 0);
		centreGrid.add(reservedHeader, 2, 0);

		// Create table containing the users' loaned items.
		TableView loanedItems = new TableView();

		// Create and add relavent columns.
		TableColumn resourceCol1 = new TableColumn("Resource");
		resourceCol1.setMinWidth(150);
		resourceCol1.setResizable(false);
		TableColumn dueDateCol = new TableColumn("Due Date");
		dueDateCol.setMinWidth(150);
		dueDateCol.setResizable(false);

		loanedItems.getColumns().add(resourceCol1);
		loanedItems.getColumns().add(dueDateCol);
		centreGrid.add(loanedItems, 0, 1);

		// Create table containing the users' reserved items.
		TableView reservedItems = new TableView();

		// Create and add relevant table columns.
		TableColumn resourceCol2 = new TableColumn("Resource");
		resourceCol2.setMinWidth(250);
		resourceCol2.setResizable(false);
		reservedItems.getColumns().addAll(resourceCol2);
		centreGrid.add(reservedItems, 2, 1);

		// Create table containing users' requested items.
		TableView requestedItems = new TableView();

		// Create and add relevant table columns.
		TableColumn resourceCol3 = new TableColumn("Resource");
		resourceCol3.setMinWidth(250);
		resourceCol3.setResizable(false);
		requestedItems.getColumns().addAll(resourceCol3);
		centreGrid.add(requestedItems, 1, 1);

		// Add the GridPane containing the tables to the parent container.
		parentContainer.getChildren().add(centreGrid);

		return centreGrid;

	}

	public static void main(String[] args) {
		launch(args);
	}
}