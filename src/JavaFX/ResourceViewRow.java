package JavaFX;

import java.util.Date;
import Core.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * This class holds row classes for the tables shown in the resource views, and
 * functions to generate the table views of these rows.
 * 
 * @author Ben
 */
public class ResourceViewRow {

	/**
	 * Creates a table view of the loaned copies of this resource.
	 * 
	 * @param selectedResource the resource to create the table view for.
	 * @return the table view of loaned copies of the resource.
	 */
	public static TableView<LoanRow>
		populateLoanedItems(Resource selectedResource) {

		// Create table containing the users' who have loaned items.
		TableView<LoanRow> loanedItems = new TableView<LoanRow>();

		// Create and add relevant columns.
		TableColumn<LoanRow, String> copyIdCol =
			new TableColumn<LoanRow, String>("Copy ID");
		copyIdCol.setMinWidth(35);
		copyIdCol.setResizable(false);

		TableColumn<LoanRow, String> usernameCol =
			new TableColumn<LoanRow, String>("Username");
		usernameCol.setMinWidth(80);
		usernameCol.setResizable(false);

		TableColumn<LoanRow, String> dueDateCol =
			new TableColumn<LoanRow, String>("Due Date");
		dueDateCol.setMinWidth(85);
		dueDateCol.setResizable(false);

		ObservableList<LoanRow> loanedCopies =
			FXCollections.observableArrayList();
		// Loop through this resource's copies
		for (Copy c : selectedResource.getCopies()) {
			// If there's a customer reference, this copy is loaned
			Customer cust = c.getCustRef();
			if (cust != null) {
				// Add it to the collection
				loanedCopies.add(new LoanRow(c));
			}
		}
		if (!loanedCopies.isEmpty()) {

			copyIdCol.setCellValueFactory(
				new PropertyValueFactory<LoanRow, String>("copyId"));

			usernameCol.setCellValueFactory(
				new PropertyValueFactory<LoanRow, String>("username"));

			dueDateCol.setCellValueFactory(
				new PropertyValueFactory<LoanRow, String>("dueDate"));

			loanedItems.setItems(loanedCopies);
		}

		loanedItems.getColumns().addAll(copyIdCol, usernameCol, dueDateCol);

		return loanedItems;
	}

	/**
	 * Creates a table view of the requests for the given resource.
	 * 
	 * @param selectedResource the resource to create the view of requests for.
	 * @return a table view of the requests for the given resource.
	 */
	public static TableView<RequestRow>
		populateRequestedItems(Resource selectedResource) {

		// Create table containing the users' who have reserved items.
		TableView<RequestRow> RequestedItems = new TableView<RequestRow>();

		// Create and add relevant column.

		TableColumn<RequestRow, String> usernameCol =
			new TableColumn<RequestRow, String>("Username");
		usernameCol.setMinWidth(250);
		usernameCol.setResizable(false);

		ObservableList<RequestRow> requestingCustomers =
			FXCollections.observableArrayList();
		// Loop through this resource's requests
		RequestQueue<Customer> requestQueue = selectedResource.getRequests();
		// Use the queue as a linked list
		RequestQueueElement<Customer> queueElem = requestQueue.getHead();
		while (queueElem != null) {
			Customer cust = queueElem.getElement();
			requestingCustomers.add(new RequestRow(cust));
			queueElem = queueElem.getNext();
		}
		// If there're any requests, then set the items
		if (!requestingCustomers.isEmpty()) {
			usernameCol.setCellValueFactory(
				new PropertyValueFactory<RequestRow, String>("username"));

			RequestedItems.setItems(requestingCustomers);
		}
		// Add column
		RequestedItems.getColumns().add(usernameCol);
		// Return tableview
		return RequestedItems;
	}

	/**
	 * Creates a table view of copies which are reserved, and which users
	 * they're reserved by.
	 * 
	 * @param selectedResource the resource the copies belong to.
	 * @return a table view of copies which are reserved, and which users
	 *         they're reserved by.
	 */
	public static TableView<ReserveRow>
		populateReservedItems(Resource selectedResource) {
		// Create table containing the users' who have reserved items.
		TableView<ReserveRow> reservedItems = new TableView<ReserveRow>();

		TableColumn<ReserveRow, String> copyIdCol =
			new TableColumn<ReserveRow, String>("Copy ID");
		copyIdCol.setMinWidth(100);
		copyIdCol.setResizable(false);

		TableColumn<ReserveRow, String> usernameCol =
			new TableColumn<ReserveRow, String>("Username");
		usernameCol.setMinWidth(160);
		usernameCol.setResizable(false);

		ObservableList<ReserveRow> reserves =
			FXCollections.observableArrayList();
		// Loop through this resource's reservers
		for (Copy c : selectedResource.getCopies()) {
			// If there's a reservedBy reference, this copy is loaned
			Customer cust = c.getReservedBy();
			if (cust != null) {
				// Add it to the collection
				reserves.add(new ReserveRow(c));
			}
		}
		// If there were any reserved copies
		if (!reserves.isEmpty()) {

			copyIdCol.setCellValueFactory(
				new PropertyValueFactory<ReserveRow, String>("copyId"));

			usernameCol.setCellValueFactory(
				new PropertyValueFactory<ReserveRow, String>("username"));

			reservedItems.setItems(reserves);
		}

		reservedItems.getColumns().addAll(copyIdCol, usernameCol);

		return reservedItems;
	}

	/**
	 * A row in the loaned items table.
	 * 
	 * @author Ben
	 */
	public static class LoanRow {
		/**
		 * The username of the customer loaning the copy.
		 */
		private SimpleStringProperty username;

		// The ID of the copy.
		private SimpleStringProperty copyId;
		/**
		 * The due date of the copy in dd/mm/yy format.
		 */
		private SimpleStringProperty dueDate;

		/**
		 * Takes a copy and extracts the correct attributes from it.
		 * 
		 * @param copy the copy to extract details from.
		 */
		public LoanRow(Copy copy) {
			copyId =
				new SimpleStringProperty(Integer.toString(copy.getUniqueId()));
			username =
				new SimpleStringProperty(copy.getCustRef().getUsername());
			Date tempDate = copy.getDueDate();
			if (tempDate != null) {
				dueDate =
					new SimpleStringProperty(UserUI.ddmmyy.format(tempDate));
			} else {
				dueDate = new SimpleStringProperty("No due date");
			}
		}

		/**
		 * @return The username of the customer borrowing the copy.
		 */
		public String getUsername() {
			return username.get();
		}

		/**
		 * @return The due date of the copy.
		 */
		public String getDueDate() {
			return dueDate.get();
		}

		/**
		 * @return the ID of the copy.
		 */
		public String getCopyId() {
			return copyId.get();
		}
	}

	/**
	 * A row in the requests table.
	 * 
	 * @author Ben
	 */
	public static class RequestRow {
		/**
		 * The username of the customer reserving the resource.
		 */
		private SimpleStringProperty username;

		/**
		 * Takes a customer and extracts the correct attributes from it.
		 * 
		 * @param cust the customer to get the attributes from.
		 */
		public RequestRow(Customer cust) {
			username = new SimpleStringProperty(cust.getUsername());
		}

		/**
		 * @return The username of the customer borrowing the copy.
		 */
		public String getUsername() {
			return username.get();
		}
	}

	/**
	 * A row in the reserves table of a resource.
	 * 
	 * @author Ben
	 */
	public static class ReserveRow {
		private SimpleStringProperty username;
		private SimpleStringProperty copyId;

		/**
		 * Takes a copy and extracts its id and the username of the customer
		 * reserving it.
		 * 
		 * @param copy the copy to extract information from.
		 */
		public ReserveRow(Copy copy) {
			copyId =
				new SimpleStringProperty(Integer.toString(copy.getUniqueId()));
			username =
				new SimpleStringProperty(copy.getReservedBy().getUsername());
		}

		/**
		 * @return The username of the customer borrowing the copy.
		 */
		public String getUsername() {
			return username.get();
		}

		/**
		 * @return the ID of the copy.
		 */
		public String getCopyId() {
			return copyId.get();
		}
	}
}
