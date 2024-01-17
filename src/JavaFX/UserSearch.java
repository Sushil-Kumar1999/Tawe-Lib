package JavaFX;

import java.sql.SQLException;
import Core.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * A JavaFX class used to search for a User or Librarian in the database.
 * 
 * @author Billy Roberts
 * @version 1.0
 */
public class UserSearch extends Application {

	private final int SCENE_WIDTH = 600;
	private final int SCENE_HEIGHT = 550;
	private final int GRID_COLUMN_NUMBER = 2;
	private final int TABLE_COLUMN_NUMBER = 4;
	private ArrayList<User> usersFound = new ArrayList<User>();
	private ArrayList<User> librariansFound = new ArrayList<User>();

	TableView table = new TableView<ItemRow1>();
	TableColumn<ItemRow1, String> usernameColumn =
		new TableColumn<ItemRow1, String>("Username");
	TableColumn<ItemRow1, String> firstNameColumn =
		new TableColumn<ItemRow1, String>("First Name");
	TableColumn<ItemRow1, String> lastNameColumn =
		new TableColumn<ItemRow1, String>("Last Name");
	TableColumn<ItemRow1, String> phoneNumberColumn =
		new TableColumn<ItemRow1, String>("UK Telephone Number");

	public void start(Stage primaryStage) {
		// Create a new grid pane to build upon
		GridPane root = new GridPane();

		// Code below is creating the rows and columns and setting their widths
		// and heights.
		root.getColumnConstraints()
			.add(new ColumnConstraints(SCENE_WIDTH / GRID_COLUMN_NUMBER));
		root.getColumnConstraints()
			.add(new ColumnConstraints(SCENE_WIDTH / GRID_COLUMN_NUMBER));

		// Create and add the label to the grid.
		Label title = new Label("Search for:");
		title.setFont(new Font("Arial", 25));
		root.add(title, 0, 0);

		// Create and add the combobox to the grid.
		ObservableList<String> resources =
			FXCollections.observableArrayList("Customer", "Librarian");

		ComboBox comboResource = new ComboBox(resources);
		comboResource.setValue("Customer");
		root.add(comboResource, 1, 0);

		// Create and add the search button to the grid
		Button searchButton = new Button("Search");
		root.add(searchButton, 1, 5);

		// Create and add the labels to the grid.
		Label lbl1 = new Label("Username:");
		root.add(lbl1, 0, 1);
		Label lbl2 = new Label("First Name:");
		root.add(lbl2, 0, 2);
		Label lbl3 = new Label("Last Name:");
		root.add(lbl3, 0, 3);
		Label lbl4 = new Label("UK Telephone Number:");
		root.add(lbl4, 0, 4);

		// Create and add the text boxes to the grid.
		TextField text1 = new TextField();
		root.add(text1, 1, 1);
		TextField text2 = new TextField();
		root.add(text2, 1, 2);
		TextField text3 = new TextField();
		root.add(text3, 1, 3);
		TextField text4 = new TextField();
		root.add(text4, 1, 4);
		TextField text5 = new TextField();

		comboResource.setOnAction(e -> {
			text1.clear();
			text2.clear();
			text3.clear();
			text4.clear();
		});

		// Defining the columns of the table and setting their headers and
		// widths.

		usernameColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);
		firstNameColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);
		lastNameColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);
		phoneNumberColumn.setMinWidth(SCENE_WIDTH / TABLE_COLUMN_NUMBER);

		// Adding the columns to the table and placing the table on the plane
		table.getColumns().addAll(usernameColumn, firstNameColumn,
			lastNameColumn, phoneNumberColumn);
		root.add(table, 0, 10, 2, 20);

		// Handle a button event
		searchButton.setOnAction(e -> {

			if (comboResource.getValue() == "Customer") {
				try {
					usersFound =
						Datastore.searchUsers(text1.getText(), text2.getText(),
							text3.getText(), text4.getText(), false);
					table =
						populateUsers(table, usernameColumn, firstNameColumn,
							lastNameColumn, phoneNumberColumn, usersFound);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (comboResource.getValue() == "Librarian") {
				try {
					librariansFound =
						Datastore.searchUsers(text1.getText(), text2.getText(),
							text3.getText(), text4.getText(), true);
					table = populateLibrarians(table, usernameColumn,
						firstNameColumn, lastNameColumn, phoneNumberColumn,
						librariansFound);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			text1.clear();
			text2.clear();
			text3.clear();
			text4.clear();

		});

		table.setRowFactory(tv -> {
			TableRow<ItemRow1> row = new TableRow<ItemRow1>();
			if (comboResource.getValue() == "Customer") {
				row.setOnMouseClicked(event -> {
					String selectedUserName = null;

					if (event.getClickCount() == 1 && (!row.isEmpty())) {
						ItemRow1 rowData = row.getItem();
						selectedUserName = rowData.getUsername();
						Customer selectedUser = (Customer) Datastore.getUsers()
							.get(selectedUserName);
						UserDetails userDetails = new UserDetails();
						userDetails.setCurrentCustomer(selectedUser);
						userDetails.start(new Stage());
					}
				});
			}

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
		 * Username for the user to log in with.
		 */
		private SimpleStringProperty username;
		/**
		 * The first name of the user.
		 */
		private SimpleStringProperty firstName;
		/**
		 * The last name of the user.
		 */
		private SimpleStringProperty surname;
		/**
		 * The users phone number.
		 */
		private SimpleStringProperty phoneNumber;

		/**
		 * Takes a user and extracts the correct attributes from it.
		 * 
		 * @param user the user to get the data from.
		 */
		public ItemRow1(User user) {
			username = new SimpleStringProperty(user.getUsername());
			firstName = new SimpleStringProperty(user.getFirstName());
			surname = new SimpleStringProperty(user.getSurname());
			phoneNumber = new SimpleStringProperty(user.getMobileNumber());
		}

		/**
		 * @return The Users username.
		 */
		public String getUsername() {
			return username.get();
		}

		/**
		 * @return The Users first name.
		 */
		public String getFirstName() {
			return firstName.get();
		}

		/**
		 * @return The Users surname.
		 */
		public String getSurname() {
			return surname.get();
		}

		/**
		 * @return The Users phone number.
		 */
		public String getPhoneNumber() {
			return phoneNumber.get();
		}
	}

	public class ItemRow2 {
		/**
		 * Username for the user to log in with.
		 */
		private SimpleStringProperty username;
		/**
		 * The first name of the user.
		 */
		private SimpleStringProperty firstName;
		/**
		 * The last name of the user.
		 */
		private SimpleStringProperty surname;
		/**
		 * The users phone number.
		 */
		private SimpleStringProperty phoneNumber;

		/**
		 * Takes a librarian and extracts the correct attributes from it.
		 * 
		 * @param librarian the librarian to get the data from.
		 */
		public ItemRow2(User librarian) {
			username = new SimpleStringProperty(librarian.getUsername());
			firstName = new SimpleStringProperty(librarian.getFirstName());
			surname = new SimpleStringProperty(librarian.getSurname());
			phoneNumber =
				new SimpleStringProperty(librarian.getMobileNumber());
		}

		/**
		 * @return The Users username.
		 */
		public String getUsername() {
			return username.get();
		}

		/**
		 * @return The Users first name.
		 */
		public String getFirstName() {
			return firstName.get();
		}

		/**
		 * @return The Users surname.
		 */
		public String getSurname() {
			return surname.get();
		}

		/**
		 * @return The Users phone number.
		 */
		public String getPhoneNumber() {
			return phoneNumber.get();
		}
	}

	private TableView<ItemRow1> populateUsers(TableView<ItemRow1> userTable,
		TableColumn<ItemRow1, String> usernameCol,
		TableColumn<ItemRow1, String> firstNameCol,
		TableColumn<ItemRow1, String> surnameCol,
		TableColumn<ItemRow1, String> phoneNumberCol, ArrayList<User> users) {

		ObservableList<ItemRow1> users2 = FXCollections.observableArrayList();
		for (User u : users) {
			users2.add(new ItemRow1(u));
		}
		usernameCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("username"));
		firstNameCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("firstName"));
		surnameCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("surname"));
		phoneNumberCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("phoneNumber"));

		userTable.setItems(users2);
		return userTable;
	}

	private TableView<ItemRow2> populateLibrarians(TableView librarianTable,
		TableColumn usernameCol, TableColumn firstNameCol,
		TableColumn surnameCol, TableColumn phoneNumberCol,
		ArrayList<User> librarians) {

		ObservableList<ItemRow2> librarians2 =
			FXCollections.observableArrayList();
		for (User l : librarians) {
			librarians2.add(new ItemRow2(l));
		}
		usernameCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("username"));
		firstNameCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("firstName"));
		surnameCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("surname"));
		phoneNumberCol.setCellValueFactory(
			new PropertyValueFactory<ItemRow1, String>("phoneNumber"));

		librarianTable.setItems(librarians2);
		return librarianTable;
	}
}
