package JavaFX;

import javafx.application.Application;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import Core.*;

/**
 * Allows the user to see the newly added resources since last login.
 * 
 * @author Tyunay Kamber and Gacem Ben Cherif Taibi
 */
public class NewAdditionsUI extends Application {

	private LocalDateTime lastLogin;// last login of a user
	private BorderPane borderPane = new BorderPane();
	// The arraylist that will be populated with new additions
	private ArrayList<Resource> newAdditionsList = new ArrayList<Resource>();

	public NewAdditionsUI(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public void start(Stage primaryStage) {

	}

	public Pane buildRootPane() throws SQLException {
		// Create the main pane
		VBox vb = new VBox();
		vb.setMaxWidth(700);
		// populating the arraylist of new additions.
		newAdditionsList = Datastore.newAdditions(lastLogin);

		System.out.println(newAdditionsList); // for testing purposes

		// Creating a table view
		TableView<Resource> newAdditionsTableView = new TableView<Resource>();

		// Set up a table column
		TableColumn<Resource, String> titleColumn =
			new TableColumn<Resource, String>("Title");
		titleColumn.setMinWidth(700 / 3);
		titleColumn.setResizable(true);
		// add values to the column
		titleColumn.setCellValueFactory(
			new PropertyValueFactory<Resource, String>("title"));

		// Set up a table column
		TableColumn<Resource, Integer> yearColumn =
			new TableColumn<Resource, Integer>("Year");
		yearColumn.setResizable(false);
		yearColumn.setMinWidth(700 / 3);
		// add values to the column
		yearColumn.setCellValueFactory(
			new PropertyValueFactory<Resource, Integer>("year"));

		// Set up a table column
		TableColumn<Resource, String> dateColumn =
			new TableColumn<Resource, String>("Addition Date");
		dateColumn.setResizable(false);
		dateColumn.setMinWidth(700 / 3);
		// add values to the column
		dateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
			cellData.getValue().getAdditionDate().toString()));

		ObservableList<Resource> newResourceList =
			FXCollections.observableArrayList(newAdditionsList);

		// Sets the on click action for each row
		newAdditionsTableView.setRowFactory(tv -> {
			TableRow<Resource> row = new TableRow<Resource>();
			row.setOnMouseClicked(e -> {
				if (e.getClickCount() == 1 && (!row.isEmpty())) {
					Resource r = row.getItem();
					// Required on account of how the ResourceSearch was
					// implemented
					ResourceSearch.selectedResourceID = r.getUniqueID();
					switch (r.getType()) {
					case "Book":
						UserBook userBook = new UserBook();
						userBook.start(new Stage());
						break;
					case "DVD":
						UserDVD userDVD = new UserDVD();
						try {
							userDVD.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						break;
					case "Laptop":
						UserLaptop userLaptop = new UserLaptop();
						try {
							userLaptop.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						break;
					case "VideoGame":
						UserVideoGame userVideoGame = new UserVideoGame();
						try {
							userVideoGame.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			return row;
		});

		newAdditionsTableView.setItems(newResourceList);

		// add the columns to the table
		newAdditionsTableView.getColumns().addAll(titleColumn, yearColumn,
			dateColumn);

		// add the table to the vbox
		vb.getChildren().add(newAdditionsTableView);
		vb.setAlignment(Pos.CENTER);

		borderPane.setCenter(vb);

		return vb;

	}
}
