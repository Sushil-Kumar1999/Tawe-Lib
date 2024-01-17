package JavaFX;

import java.io.FileInputStream;
import Core.*;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Create an interactive javafx application with multiple UI controls. A class
 * that represent a details of librarian in a LaptopComputer class.
 * 
 * @author Ali Alowais
 * @version 1.0
 */
public class LibrarianLaptop extends Application {

	// Search for a particular resource using its ID
	private LaptopComputer selectedResource;

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {

		// Casts it to type LaptopComputer and makes selectedResource refer to
		// this
		selectedResource = (LaptopComputer) Datastore.getResources()
			.get(ResourceSearch.selectedResourceID);

		// Create the Form pane for librarian view of Laptop and Camputer
		// class.
		GridPane gridPane = createUserDetailFormPane();

		// Create a scene form gridPane
		Scene scene = new Scene(gridPane, 800, 700);

		// Add UI controls to form
		addUIControls(gridPane);

		primaryStage.setTitle("Librarian Laptop View");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * GridPane layout for designing the librarian view of laptop and computer
	 * class.
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

		// Add the title for the Laptop and Computer class.
		Text title = new Text("Title: " + selectedResource.getTitle());
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for Laptop and Computer class
		Label manufacturer = new Label("Manufacturer:");
		Label manufacturerTextField =
			new Label(selectedResource.getManufacturer());

		Label model = new Label("Model:");
		Label modelTextField = new Label(selectedResource.getModel());

		Label os = new Label("OS:");
		Label osTextField = new Label(selectedResource.getOS());

		String profileImagePath = (selectedResource.getThumbnailImagePath());
		Image image = new Image(new FileInputStream(profileImagePath));
		ImageView imageView = new ImageView(image);

		// setting the fit height and width of the image view
		imageView.setFitHeight(100);
		imageView.setFitWidth(50);

		// Setting the preserve ratio of the image view
		imageView.setPreserveRatio(true);

		Label laptopType =
			new Label("Laptop Type: " + selectedResource.getType());
		ComboBox<String> laptopTypeComboBox = new ComboBox<String>();
		laptopTypeComboBox.getItems().addAll("Laptop1", "Laptop2", "Laptop3",
			"Laptop4");
		laptopTypeComboBox.setPromptText("Select Resource");

		HBox hboxBtn = new HBox(10);
		hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);

		gridPane.add(title, 0, 0, 2, 1);
		gridPane.add(returnLine("Line1"), 0, 1, 5, 1);
		gridPane.add(imageView, 0, 2, 2, 1);
		gridPane.add(manufacturer, 0, 4);
		gridPane.add(manufacturerTextField, 1, 4);
		gridPane.add(model, 2, 4);
		gridPane.add(modelTextField, 3, 4);
		gridPane.add(os, 0, 5);
		gridPane.add(osTextField, 1, 5);
		gridPane.add(laptopType, 2, 5);
		gridPane.add(laptopTypeComboBox, 3, 5);
		gridPane.add(returnLine("Line2"), 0, 9, 5, 1);
		gridPane.add(addCurrentItem(), 0, 11, 10, 1);
		gridPane.add(returnLine("Line3"), 0, 15, 5, 1);
		addAddCopy(gridPane);
	}

	/**
	 * Implements the add copy functionality
	 * 
	 * @param gridPane the grid pane to add the controls to.
	 */
	private void addAddCopy(GridPane gridPane) {
		Label addCopyLabel = new Label();
		addCopyLabel.setTextFill(Color.DARKGREEN);
		TextField addCopyTextField = new TextField();
		addCopyTextField.setPromptText("Enter number of copies");
		addCopyTextField.setFocusTraversable(false);
		Button addCopyButton = new Button("Add Copy");
		addCopyButton.setOnAction(event -> {
			try {
				selectedResource
					.addCopies(Integer.parseInt(addCopyTextField.getText()));
				addCopyLabel.setText(
					addCopyTextField.getText() + " copies added succesfully");
				addCopyTextField.clear();
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}

		});

		gridPane.add(addCopyTextField, 4, 19);
		gridPane.add(addCopyButton, 5, 19);
		gridPane.add(addCopyLabel, 4, 20);
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
	 * This the table for librarian to view over all the information about the
	 * customer.
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
		Label loanedHeader = new Label("The Customer who have Loaned Items");
		Label reservedHeader =
			new Label("The Customer who have Reserved Items");
		Label requestedHeader =
			new Label("The Customer who have Requested Items");

		centreGrid.add(loanedHeader, 0, 0);
		centreGrid.add(requestedHeader, 1, 0);
		centreGrid.add(reservedHeader, 2, 0);

		// Get loaned items
		TableView<ResourceViewRow.LoanRow> loanedItems =
			ResourceViewRow.populateLoanedItems(selectedResource);

		centreGrid.add(loanedItems, 0, 1);

		// Create table containing users' who have requested items.
		TableView<ResourceViewRow.RequestRow> requestedItems =
			ResourceViewRow.populateRequestedItems(selectedResource);

		centreGrid.add(requestedItems, 1, 1);

		// Get reserved items
		TableView<ResourceViewRow.ReserveRow> reservedItems =
			ResourceViewRow.populateReservedItems(selectedResource);

		centreGrid.add(reservedItems, 2, 1);

		// Create drop down containing copies of the resource.
		Label copySelectTitle = new Label("IDs of Copies of this resource:");
		Button viewDetails = new Button("View Copy Details");

		ArrayList<Copy> resourceCopies = selectedResource.getCopies();
		ObservableList<String> copyIDs = FXCollections.observableArrayList();
		if (selectedResource.getCopies().size() != 0) {
			for (Copy c : resourceCopies) {
				copyIDs.add(Integer.toString(c.getUniqueId()));
			}
		}

		ComboBox<String> copySelect = new ComboBox<String>(copyIDs);
		copySelect.getSelectionModel().selectFirst();

		viewDetails.setOnAction(e -> {
			Copy selectedCopy = null;
			int selectedCopyID = Integer.parseInt(copySelect.getValue());
			for (Copy c : resourceCopies) {
				if (c.getUniqueId() == selectedCopyID) {
					selectedCopy = c;
				}
			}
			CopyDetails copyDetails = new CopyDetails();
			copyDetails.setCopyReference(selectedCopy);
			try {
				copyDetails.start(new Stage());
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});

		centreGrid.addRow(2, copySelectTitle, copySelect, viewDetails);

		// Add the GridPane containing the tables to the parent container.
		parentContainer.getChildren().add(centreGrid);

		return centreGrid;

	}

	public static void main(String[] args) {
		launch(args);
	}
}