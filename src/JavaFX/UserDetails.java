package JavaFX;

import java.io.FileNotFoundException;
import Core.*;
import java.sql.SQLException;
import java.util.Date;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * A JavaFX Class that represents all details about a customer
 * 
 * @author Tyunay Kamber
 * @version 1.0
 */

public class UserDetails extends Application {

	/*
	 * final int COLUMN_NUMBER = 3; final int ROW_NUMBER = 11;
	 */
	private TableView<ItemRow1> table = new TableView<ItemRow1>();
	private Customer currentCustomer;

	public void start(Stage primaryStage) {
		final int SCENE_WIDTH = 450;
		final int SCENE_HEIGHT = 650;

		VBox parentContainer = new VBox(10);

		// Create a new grid pane to build upon
		GridPane root = new GridPane();

		// Code below is creating the rows and columns and setting their widths
		// and heights.

		root.setVgap(10);

		// Create and add the label to the grid.
		Label title = new Label("User Details");
		title.setFont(new Font("Arial", 20));
		root.add(title, 0, 0);

		// Create and add the labels to the grid.
		Label lbl1 = new Label("Username:");
		root.add(lbl1, 0, 1);
		Label lbl2 = new Label("First Name:");
		root.add(lbl2, 0, 2);
		Label lbl3 = new Label("Last Name:");
		root.add(lbl3, 0, 3);
		Label lbl4 = new Label("UK Telephone Number:");
		root.add(lbl4, 0, 4);
		Label lbl5 = new Label("UK Address:");
		root.add(lbl5, 0, 5);
		Label lbl7 = new Label("Balance: ");
		root.add(lbl7, 0, 7);
		Label lbl10 = new Label("Enter amount to pay: ");
		root.add(lbl10, 0, 10);

		// Create and add the text boxes to the grid.
		Text text1 = new Text(currentCustomer.getUsername());
		root.add(text1, 1, 1);
		Text text2 = new Text(currentCustomer.getFirstName());
		root.add(text2, 1, 2);
		Text text3 = new Text(currentCustomer.getSurname());
		root.add(text3, 1, 3);
		Text text4 = new Text(currentCustomer.getMobileNumber());
		root.add(text4, 1, 4);
		Text text5 = new Text(currentCustomer.getAddress());
		root.add(text5, 1, 5);
		Text text7 =
			new Text("£" + Integer.toString(currentCustomer.getBalance()));
		root.add(text7, 1, 7);
		Button editDetails = new Button("Edit user details");
		root.add(editDetails, 1, 8);
		editDetails.setOnAction(e -> {
			UserForm userForm = new UserForm();
			Stage s = new Stage();
			userForm.setCustomerReference(currentCustomer);
			userForm.setEdited(true);
			userForm.start(s);
			s.setOnCloseRequest(e2 -> {
				text1.setText(currentCustomer.getUsername());
				text2.setText(currentCustomer.getFirstName());
				text3.setText(currentCustomer.getSurname());
				text4.setText(currentCustomer.getMobileNumber());
				text5.setText(currentCustomer.getAddress());
			});
		});

		TextField textField2 = new TextField();
		root.add(textField2, 1, 10);
		parentContainer.getChildren().add(root);

		// Create and add the pay button to the grid
		Button payButton = new Button("Pay");
		Text successMessage = new Text();
		root.add(successMessage, 1, 11);
		root.add(payButton, 2, 10);
		// Handle a button event
		payButton.setOnAction(e -> {
			try {
				currentCustomer
					.payFine(Integer.parseInt(textField2.getText()));
			} catch (NumberFormatException e1) {
				successMessage.setFill(Color.RED);
				successMessage.setText("Incorrect formatting");
			} catch (IllegalArgumentException e1) {
				successMessage.setFill(Color.RED);
				successMessage
					.setText("Payment amount cannot exceed users' balance");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		/**
		 * Creating a table and its rows and adding them to the grid.
		 */

		Text tableHeader = new Text("Loaned Copies ");
		tableHeader.setTextAlignment(TextAlignment.CENTER);
		parentContainer.getChildren().add(tableHeader);

		TableColumn<ItemRow1, String> copyID =
			new TableColumn<ItemRow1, String>("Copy ID");
		copyID.setMinWidth(140);
		TableColumn<ItemRow1, String> item =
			new TableColumn<ItemRow1, String>("Item");
		item.setMinWidth(200);
		TableColumn<ItemRow1, String> dueDate =
			new TableColumn<ItemRow1, String>("Due Date");
		dueDate.setMinWidth(200);
		table.getColumns().addAll(item, dueDate);
		table = populateTable(table, item, dueDate);
		table.setMaxSize(400, 240);

		parentContainer.getChildren().add(table);

		table.setRowFactory(tv -> {
			TableRow<ItemRow1> row = new TableRow<ItemRow1>();

			row.setOnMouseClicked(event -> {
				ItemRow1 data = row.getItem();
				Copy selectedCopy = null;
				for (Copy c : currentCustomer.getCurrentLoans()) {
					System.out.println(c);
					System.out.println(c.getUniqueId());
					if (c.getUniqueId() == Integer
						.parseInt(data.getCopyID())) {
						selectedCopy = c;
					}
				}
				if (selectedCopy != null) {
					CopyDetails copyDetails = new CopyDetails();
					copyDetails.setCopyReference(selectedCopy);
					System.out.println(selectedCopy);
					try {
						copyDetails.start(new Stage());
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			return row;
		});

		parentContainer.setMargin(root, new Insets(10, 0, 0, 10));
		parentContainer.setMargin(tableHeader, new Insets(10, 0, 0, 10));
		parentContainer.setMargin(table, new Insets(0, 0, 0, 10));

		// Create a scene based on the pane.
		Scene scene = new Scene(parentContainer, SCENE_WIDTH, SCENE_HEIGHT);

		// Show the scene
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private TableView<ItemRow1> populateTable(TableView<ItemRow1> table,
		TableColumn<ItemRow1, String> itemCol,
		TableColumn<ItemRow1, String> dueDateCol) {

		if (!currentCustomer.getCurrentLoans().isEmpty()) {
			ObservableList<ItemRow1> loanedCopies =
				FXCollections.observableArrayList();
			for (Copy c : currentCustomer.getCurrentLoans()) {
				loanedCopies.add(new ItemRow1(c));
			}

			itemCol.setCellValueFactory(
				new PropertyValueFactory<ItemRow1, String>("title"));
			dueDateCol.setCellValueFactory(
				new PropertyValueFactory<ItemRow1, String>("dueDate"));
			/*
			 * copyIDCol.setCellValueFactory( new
			 * PropertyValueFactory<ItemRow1,String>("copyID") );
			 */
			table.setItems(loanedCopies);

		}
		return table;

	}

	public void setCurrentCustomer(Customer customer) {
		this.currentCustomer = customer;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Defines a row in the loaned items table and reserved items table that
	 * contains data from a Copy class.
	 * 
	 * @author Mike
	 */
	public class ItemRow1 {
		/**
		 * The title of the resource (with its type) the copy refers to.
		 */
		private SimpleStringProperty title;
		/**
		 * The due date of the resource in dd/mm/yy format.
		 */
		private SimpleStringProperty dueDate;
		/**
		 * The copyID of the copy.
		 */
		private SimpleStringProperty copyID;

		/**
		 * Takes a copy and extracts the correct attributes from it.
		 * 
		 * @param copy the copy to extract details from.
		 */
		public ItemRow1(Copy copy) {
			title = new SimpleStringProperty(copy.getResourceRef().getTitle()
				+ " (" + copy.getResourceRef().getType() + ")");
			copyID =
				new SimpleStringProperty(Integer.toString(copy.getUniqueId()));
			Date tempDate = copy.getDueDate();
			if (tempDate != null) {
				dueDate =
					new SimpleStringProperty(UserUI.ddmmyy.format(tempDate));
			} else {
				dueDate = new SimpleStringProperty("No due date");
			}
		}

		/**
		 * @return The title of the resource of the copy.
		 */
		public String getTitle() {
			return title.get();
		}

		/**
		 * @return The due date of the copy.
		 */
		public String getDueDate() {
			return dueDate.get();
		}

		/**
		 * @return The ID of the copy
		 */
		public String getCopyID() {
			return copyID.get();
		}
	}
}
