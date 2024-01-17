package JavaFX;

import java.io.FileNotFoundException;
import Core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * A JavaFX class used to search for a Resource in the database.
 * 
 * @author Billy Roberts
 * @version 1.0
 */
public class ResourceSearch extends Application {

	private final int SCENE_WIDTH = 600;
	private final int SCENE_HEIGHT = 600;
	private final int GRID_COLUMN_NUMBER = 2;
	private final int TABLE_COLUMN_NUMBER = 3;
	private ArrayList<Book> booksFound = new ArrayList<Book>();
	private ArrayList<DVD> dvdsFound = new ArrayList<>();
	private ArrayList<LaptopComputer> laptopsFound = new ArrayList<>();
	private ArrayList<VideoGame> videoGamesFound = new ArrayList<>();
	TableView<ItemRow1> table = new TableView<>();
	public static int selectedResourceID;

	public void start(Stage primaryStage) {

		// Set title of form
		primaryStage.setTitle("Search for Resource");

		// Create a new grid pane to build upon
		GridPane root = new GridPane();

		// Create StackPane where a TextField and CheckBox will be on top of
		// each other.
		StackPane stack = new StackPane();

		// Code below is creating the rows and columns and
		// setting their widths and heights.
		root.getColumnConstraints()
			.add(new ColumnConstraints(SCENE_WIDTH / GRID_COLUMN_NUMBER));
		root.getColumnConstraints()
			.add(new ColumnConstraints(SCENE_WIDTH / GRID_COLUMN_NUMBER));

		// Create and add the label to the grid.
		Label title = new Label("Search for:");
		title.setFont(new Font("Arial", 25));
		root.add(title, 0, 0);

		// Create and add the combobox to the grid.
		ObservableList<String> resources = FXCollections
			.observableArrayList("Book", "DVD", "Laptop", "VideoGame");
		ComboBox comboResource = new ComboBox(resources);
		comboResource.getSelectionModel().selectFirst();
		root.add(comboResource, 1, 0);

		// Create and add the search button to the grid.
		Button searchButton = new Button("Search");
		root.add(searchButton, 1, 9);

		// Create and add the labels to the grid.
		Label lbl1 = new Label("Title:");
		root.add(lbl1, 0, 2);
		Label lbl2 = new Label("Year:");
		root.add(lbl2, 0, 3);
		Label lbl3 = new Label("Author:");
		root.add(lbl3, 0, 4);
		Label lbl4 = new Label("Publisher:");
		root.add(lbl4, 0, 5);
		Label lbl5 = new Label("Genre:");
		root.add(lbl5, 0, 6);
		Label lbl6 = new Label("ISBN:");
		root.add(lbl6, 0, 7);
		Label lbl7 = new Label("Language:");
		root.add(lbl7, 0, 8);

		// Create CheckBox for whether a VideoGame has Multiplayer Support.
		CheckBox multiplayerCB = new CheckBox("");
		multiplayerCB.setIndeterminate(true);
		multiplayerCB.setAllowIndeterminate(true);
		;
		multiplayerCB.setVisible(false);

		// Create and add the text boxes to the grid.
		TextField text1 = new TextField();
		root.add(text1, 1, 2);
		TextField text2 = new TextField();
		root.add(text2, 1, 3);
		TextField text3 = new TextField();
		root.add(text3, 1, 4);

		TextField text4 = new TextField();
		root.add(text4, 1, 5);

		TextField text5 = new TextField();
		root.add(text5, 1, 6);

		TextField text6 = new TextField();
		stack.getChildren().addAll(text6, multiplayerCB);
		root.add(stack, 1, 7);

		TextField text7 = new TextField();
		root.add(text7, 1, 8);

		// Event occurs when the text in the Combobox changes.
		comboResource.setOnAction(e -> {

			// If the text in the Combobox is equal to 'Book'.
			if (comboResource.getValue() == "Book") {
				lbl3.setVisible(true);
				lbl4.setVisible(true);
				lbl5.setVisible(true);
				lbl6.setVisible(true);
				lbl7.setVisible(true);
				lbl3.setText("Author:");
				lbl4.setText("Publisher:");
				lbl5.setText("Genre:");
				lbl6.setText("ISBN:");
				lbl7.setText("Language:");

				text3.setVisible(true);
				text4.setVisible(true);
				text5.setVisible(true);
				text6.setVisible(true);
				text7.setVisible(true);

				multiplayerCB.setVisible(false);

				// If the text in the Combobox is equal to 'DVD'.
			} else if (comboResource.getValue() == "DVD") {
				lbl7.setVisible(false);
				text7.setVisible(false);
				multiplayerCB.setVisible(false);

				lbl3.setVisible(true);
				lbl4.setVisible(true);
				lbl5.setVisible(true);
				lbl6.setVisible(true);
				lbl3.setText("Director:");
				lbl4.setText("Runtime:");
				lbl5.setText("Language:");
				lbl6.setText("Subtitle language:");

				text3.setVisible(true);
				text4.setVisible(true);
				text5.setVisible(true);
				text6.setVisible(true);

				// If the text in the Combobox is equal to 'Laptop'.
			} else if (comboResource.getValue() == "Laptop") {
				lbl6.setVisible(false);
				lbl7.setVisible(false);
				text6.setVisible(false);
				text7.setVisible(false);
				multiplayerCB.setVisible(false);

				lbl3.setVisible(true);
				lbl4.setVisible(true);
				lbl5.setVisible(true);
				lbl3.setText("Manufacturer:");
				lbl4.setText("Model:");
				lbl5.setText("Operating System:");

				text3.setVisible(true);
				text4.setVisible(true);
				text5.setVisible(true);
			} else if (comboResource.getValue() == "VideoGame") {

				lbl7.setVisible(false);
				text6.setVisible(false);
				text7.setVisible(false);

				lbl3.setVisible(true);
				lbl4.setVisible(true);
				lbl5.setVisible(true);
				lbl6.setVisible(true);
				lbl3.setText("Publisher:");
				lbl4.setText("Genre:");
				lbl5.setText("Certificate Rating:");
				lbl6.setText("Multiplayer Support:");

				text3.setVisible(true);
				text4.setVisible(true);
				text5.setVisible(true);

				multiplayerCB.setVisible(true);

			}
			text1.clear();
			text2.clear();
			text3.clear();
			text4.clear();
			text5.clear();
			text6.clear();
			text7.clear();
		});

		// Defining the columns of the table and setting their headers and
		// widths.
		TableColumn uniqueIDColumn = new TableColumn("Unique ID");
		uniqueIDColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);
		TableColumn titleColumn = new TableColumn("Title");
		titleColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);
		TableColumn yearColumn = new TableColumn("Year");
		yearColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);

		// Adding the columns to the table and placing the table on the plane.
		table.getColumns().addAll(uniqueIDColumn, titleColumn, yearColumn);
		root.add(table, 0, 10, 2, 20);

		// Handle a button event of the user clicking search.
		searchButton.setOnAction(e -> {

			if (comboResource.getValue() == "Book") {
				try {
					booksFound = Datastore.searchBooks(text1.getText(),
						text2.getText(), text3.getText(), text4.getText(),
						text5.getText(), text6.getText(), text7.getText());
					table = populateBooks(table, uniqueIDColumn, titleColumn,
						yearColumn, booksFound);
				} catch (SQLException e1) {
				}
			}
			if (comboResource.getValue() == "DVD") {
				try {
					dvdsFound = Datastore.searchDVDs(text1.getText(),
						text2.getText(), text3.getText(), text4.getText(),
						text5.getText(), text6.getText());
					table = populateDVDs(table, uniqueIDColumn, titleColumn,
						yearColumn, dvdsFound);
				} catch (SQLException e1) {
				}
			}
			if (comboResource.getValue() == "Laptop") {
				try {
					laptopsFound = Datastore.searchLaptopComputer(
						text1.getText(), text2.getText(), text3.getText(),
						text4.getText(), text5.getText());
					table = populateLaptops(table, uniqueIDColumn, titleColumn,
						yearColumn, laptopsFound);
				} catch (SQLException e1) {
				}
			}
			if (comboResource.getValue() == "VideoGame") {
				try {
					int multiplayer;
					if (multiplayerCB.isIndeterminate()) {
						multiplayer = -1;
					} else if (multiplayerCB.isSelected()) {
						multiplayer = 1;
					} else {
						multiplayer = 0;
					}
					videoGamesFound = Datastore.searchVideoGame(
						text1.getText(), text2.getText(), text3.getText(),
						text4.getText(), text5.getText(), multiplayer);
					table = populateVideoGames(table, uniqueIDColumn,
						titleColumn, yearColumn, videoGamesFound);
				} catch (SQLException e1) {
				}
			}
			text1.clear();
			text2.clear();
			text3.clear();
			text4.clear();
			text5.clear();
			text6.clear();
			text7.clear();
		});

		// Handles When a row in the table has been clicked.
		table.setRowFactory(tv -> {
			TableRow<ItemRow1> row = new TableRow<ItemRow1>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (!row.isEmpty())) {
					ItemRow1 rowData = row.getItem();
					selectedResourceID =
						Integer.parseInt(rowData.getUniqueID());
				}
				switch ((String) comboResource.getValue()) {
				case "Book":
					if (Datastore.getCurrentUser().isLibrarian()) {
						LibrarianBook librarianBook = new LibrarianBook();
						librarianBook.start(new Stage());
					} else {
						UserBook userBook = new UserBook();
						userBook.start(new Stage());
					}
					break;
				case "DVD":
					if (Datastore.getCurrentUser().isLibrarian()) {
						LibrarianDVD librarianDVD = new LibrarianDVD();
						try {
							librarianDVD.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					} else {
						UserDVD userDVD = new UserDVD();
						try {
							userDVD.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
					break;
				case "Laptop":
					if (Datastore.getCurrentUser().isLibrarian()) {
						LibrarianLaptop librarianLaptop =
							new LibrarianLaptop();
						try {
							librarianLaptop.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					} else {
						UserLaptop userLaptop = new UserLaptop();
						try {
							userLaptop.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
					break;
				case "VideoGame":
					if (Datastore.getCurrentUser().isLibrarian()) {
						LibrarianVideoGame librarianVideoGame =
							new LibrarianVideoGame();
						try {
							librarianVideoGame.start(new Stage());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					} else {
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

		// Create a scene based on the pane.
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

		// Show the scene
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public class ItemRow1 {
		/**
		 * Unique code to identify a product.
		 */
		private SimpleStringProperty uniqueID;
		/**
		 * The title of the resource.
		 */
		private SimpleStringProperty title;
		/**
		 * The year attribute of the resource.
		 */
		private SimpleStringProperty year;

		/**
		 * Takes a resource and extracts the correct attributes from it.
		 * 
		 * @param resource the resource to extract details from.
		 */
		public ItemRow1(Resource resource) {
			uniqueID = new SimpleStringProperty(
				Integer.toString(resource.getUniqueID()));
			title = new SimpleStringProperty(resource.getTitle());
			year =
				new SimpleStringProperty(Integer.toString(resource.getYear()));
		}

		/**
		 * @return The unique ID of the resource..
		 */
		public String getUniqueID() {
			return uniqueID.get();
		}

		/**
		 * @return The title of the resource.
		 */
		public String getTitle() {
			return title.get();
		}

		/**
		 * @return The year of the resource.
		 */
		public String getYear() {
			return year.get();
		}
	}

	private TableView<ItemRow1> populateBooks(TableView resourceTable,
		TableColumn uniqueIDCol, TableColumn titleCol, TableColumn yearCol,
		ArrayList<Book> resources) {

		ObservableList<ItemRow1> resources2 =
			FXCollections.observableArrayList();
		for (Resource r : resources) {
			resources2.add(new ItemRow1(r));
		}
		uniqueIDCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("uniqueID"));
		titleCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("title"));
		yearCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("year"));

		resourceTable.setItems(resources2);

		return resourceTable;
	}

	private TableView<ItemRow1> populateDVDs(TableView resourceTable,
		TableColumn uniqueIDCol, TableColumn titleCol, TableColumn yearCol,
		ArrayList<DVD> resources) {

		ObservableList<ItemRow1> resources2 =
			FXCollections.observableArrayList();
		for (Resource r : resources) {
			resources2.add(new ItemRow1(r));
		}
		uniqueIDCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("uniqueID"));
		titleCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("title"));
		yearCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("year"));

		for (int i = 0; i < resources2.size(); i++) {
			uniqueIDCol.setUserData(resources2.get(i).getUniqueID());
			titleCol.setUserData(resources2.get(i).getTitle());
			yearCol.setUserData(resources2.get(i).getYear());
		}
		resourceTable.setItems(resources2);

		return resourceTable;
	}

	private TableView<ItemRow1> populateLaptops(TableView resourceTable,
		TableColumn uniqueIDCol, TableColumn titleCol, TableColumn yearCol,
		ArrayList<LaptopComputer> resources) {

		ObservableList<ItemRow1> resources2 =
			FXCollections.observableArrayList();
		for (Resource r : resources) {
			resources2.add(new ItemRow1(r));
		}
		uniqueIDCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("uniqueID"));
		titleCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("title"));
		yearCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("year"));

		for (int i = 0; i < resources2.size(); i++) {
			uniqueIDCol.setUserData(resources2.get(i).getUniqueID());
			titleCol.setUserData(resources2.get(i).getTitle());
			yearCol.setUserData(resources2.get(i).getYear());
		}
		resourceTable.setItems(resources2);

		return resourceTable;
	}

	private TableView<ItemRow1> populateVideoGames(TableView resourceTable,
		TableColumn uniqueIDCol, TableColumn titleCol, TableColumn yearCol,
		ArrayList<VideoGame> resources) {

		ObservableList<ItemRow1> resources2 =
			FXCollections.observableArrayList();
		for (Resource r : resources) {
			resources2.add(new ItemRow1(r));
		}
		uniqueIDCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("uniqueID"));
		titleCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("title"));
		yearCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("year"));

		for (int i = 0; i < resources2.size(); i++) {
			uniqueIDCol.setUserData(resources2.get(i).getUniqueID());
			titleCol.setUserData(resources2.get(i).getTitle());
			yearCol.setUserData(resources2.get(i).getYear());
		}
		resourceTable.setItems(resources2);

		return resourceTable;
	}
}
