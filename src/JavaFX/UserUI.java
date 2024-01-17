package JavaFX;

import javafx.application.Application;
import Core.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Creates and provides functionality for the user dashboard.
 * 
 * @author Mike.
 */
public class UserUI extends Application {

	private LocalDateTime lastLogin;
	/**
	 * The main pane for the window.
	 */
	private BorderPane borderPane = new BorderPane();
	private final int MAIN_WINDOW_WIDTH = 1110;
	private final int MAIN_WINDOW_HEIGHT = 600;
	private final Customer CURRENT_CUSTOMER =
		(Customer) Datastore.getCurrentUser();
	private Stage primaryStage;
	private Customer currentUser;
	public static final SimpleDateFormat ddmmyy =
		new SimpleDateFormat("dd/MM/YYY");
	private Stage s = new Stage();

	private TableView<ItemRow2> requestedItems = new TableView<ItemRow2>();
	private TableColumn<ItemRow2, String> resourceCol2 =
		new TableColumn<ItemRow2, String>("Item");

	private TableView<ItemRow1> reservedItems = new TableView<ItemRow1>();
	private TableColumn<ItemRow1, String> resourceCol1 =
		new TableColumn<ItemRow1, String>("Item");

	private final int SIDE_BOX_SPACING = 10;

	private TableView<EventRowUpcomming> upcommingEvents = new TableView<>();
	private TableColumn<EventRowUpcomming, String> eventNameU =
		new TableColumn<>("Event name");
	private TableColumn<EventRowUpcomming, String> eventDateU =
		new TableColumn<>("Event date and time");
	private TableColumn<EventRowUpcomming, String> placesLeft =
		new TableColumn<>("Places left");

	private TableView<EventRowBooked> bookedEvents = new TableView<>();
	private TableColumn<EventRowBooked, String> eventNameB =
		new TableColumn<>("Event name");
	private TableColumn<EventRowBooked, String> eventDateB =
		new TableColumn<>("Event date and time");

	private TableView<EventRowBooked> attendedEvents = new TableView<>();
	private TableColumn<EventRowBooked, String> eventNameA =
		new TableColumn<>("Event name");
	private TableColumn<EventRowBooked, String> eventDateA =
		new TableColumn<>("Event date and time");

	public UserUI(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;

	}

	@Override
	public void start(Stage primaryStage) throws SQLException {

		this.primaryStage = primaryStage;
		primaryStage.setTitle("Tawe-Lib User Dashboard");

		currentUser = (Customer) Datastore.getCurrentUser();

		initializeLeftPane();

		initializeCentrePane();
		try {
			initializeTopPane();
		} catch (SQLException e) {
			System.out.print(e.getMessage());
		}

		// Construct the window.
		Scene scene =
			new Scene(borderPane, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);

		MoveTo moveTo = new MoveTo();
		moveTo.setX(125.0);
		moveTo.setY(150.0);

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Creates all the objects inside the left pane.
	 */
	private void initializeLeftPane() {
		// Add all the objects for the sidebar
		VBox sideBox = new VBox();

		// Create button to open up search form.
		Button search = new Button(
			"Search" + '\n' + "and" + '\n' + "Request" + '\n' + "Resources");
		search.setTextAlignment(TextAlignment.CENTER);
		search.setMinWidth(77);

		search.setOnAction(event -> {

			ResourceSearch resourceSearch = new ResourceSearch();
			resourceSearch.start(s);
			s.setOnCloseRequest(e -> {
				updateRequestedItems();
				updateReservedItems();
			});
		});

		sideBox.getChildren().addAll(search);
		sideBox.setPadding(new Insets(SIDE_BOX_SPACING));
		sideBox.setSpacing(SIDE_BOX_SPACING);
		VBox.setMargin(search, new Insets(90, 0, 0, 0));

		borderPane.setLeft(sideBox);

	}

	/**
	 * Creates all the objects inside the centre pane.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private void initializeCentrePane() throws SQLException {
		/*
		 * Create tabPane which will contain Transaction History and Current
		 * Items tabs.
		 */
		TabPane centreTabs = new TabPane();

		// Make the user unable to close tabs
		centreTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		// Create Current Items tab and initialize it's content.
		Tab currentItems = new Tab("Current Items");
		currentItems = initializeCurrentItemsTab(currentItems);
		centreTabs.getTabs().add(currentItems);

		// Create Transaction History tab and initialize
		Tab transactionHistory = new Tab("Transaction History");
		transactionHistory =
			intializeTransactionHistoryTab(transactionHistory);
		centreTabs.getTabs().add(transactionHistory);

		// Create Events tab and initialize
		Tab events = initializeEventsTab();
		centreTabs.getTabs().add(events);

		// Create userViewStatistics tab and initialise
		centreTabs.getTabs().add(initialiseStatisticsTab());

		centreTabs.getTabs().add(initialiseNewAdditionsTab());

		// Set the centre of the border pane to the tab container.
		borderPane.setCenter(centreTabs);
	}

	/**
	 * Initialises the tab for borrowing statistics. The methods which do this
	 * are in the UserViewStatistics class.
	 * 
	 * @return the borrow statistics tab.
	 */
	private Tab initialiseStatisticsTab() {
		UserViewStatistics statistics = new UserViewStatistics();
		Tab statisticsTab = new Tab("Borrow Statistics");
		statisticsTab.setContent(statistics.buildRootPane());
		return statisticsTab;
	}

	private Tab initialiseNewAdditionsTab() throws SQLException {
		Tab newAdditionsTab = new Tab("New Additions");

		NewAdditionsUI newAdditions = new NewAdditionsUI(lastLogin);
		newAdditionsTab.setContent(newAdditions.buildRootPane());

		return newAdditionsTab;
	}

	/**
	 * Adds all necessary content to the current items tab
	 * 
	 * @param tab the current items tab.
	 * @return The current items tab with all the objects in it.
	 */
	private Tab initializeCurrentItemsTab(Tab tab) {
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
		TableView<ItemRow1> loanedItems = new TableView<ItemRow1>();

		TableColumn<ItemRow1, String> resourceCol =
			new TableColumn<ItemRow1, String>("Item");
		resourceCol.setMinWidth(150);
		resourceCol.setResizable(false);

		TableColumn<ItemRow1, String> dueDateCol =
			new TableColumn<ItemRow1, String>("Due Date");
		dueDateCol.setMinWidth(150);
		dueDateCol.setResizable(false);

		loanedItems =
			populateLoanedItems(loanedItems, resourceCol, dueDateCol);
		loanedItems.getColumns().addAll(resourceCol, dueDateCol);
		centreGrid.add(loanedItems, 0, 1);

		resourceCol1.setMinWidth(250);
		resourceCol1.setResizable(false);

		reservedItems = populateReservedItems(reservedItems, resourceCol1);
		reservedItems.getColumns().addAll(resourceCol1);
		centreGrid.add(reservedItems, 2, 1);

		// Create table containing users' requested items.
		resourceCol2.setMinWidth(250);
		resourceCol2.setResizable(false);

		requestedItems = populateRequestedItems(requestedItems, resourceCol2);
		requestedItems.getColumns().addAll(resourceCol2);
		centreGrid.add(requestedItems, 1, 1);

		// Add the GridPane containing the tables to the parent container.
		parentContainer.getChildren().add(centreGrid);

		// Set the content of the tab to the parent container.
		tab.setContent(parentContainer);

		return tab;
	}

	private void updateRequestedItems() {
		requestedItems = populateRequestedItems(requestedItems, resourceCol2);
	}

	private void updateReservedItems() {
		reservedItems = populateReservedItems(reservedItems, resourceCol1);
	}

	/**
	 * Adds all necessary content to the transaction history tab
	 * 
	 * @param tab the transaction history tab.
	 * @return The transaction history tab with all the objects in it.
	 */
	private Tab intializeTransactionHistoryTab(Tab tab) {
		// VBox that will contain all elements for the tab.
		VBox parentContainer = new VBox();
		parentContainer.setMaxWidth(900);
		parentContainer.setAlignment(Pos.TOP_CENTER);

		// Create title for the tab.
		Label title = new Label("Transaction History");
		title.setFont(new Font(16));
		parentContainer.getChildren().add(title);

		// Import the user's transaction history
		ArrayList<Transaction> transactionHistory =
			currentUser.getFineTransactions();
		// Reverse the list so the most recent ones are output first.
		Collections.reverse(transactionHistory);

		ObservableList<String> transactionHistoryStrings =
			FXCollections.observableArrayList();

		// Convert them all to strings and put them in a new list
		for (Transaction t : transactionHistory) {
			transactionHistoryStrings.add(t.toString());
		}

		// Create list view
		ListView<String> listView =
			new ListView<String>(transactionHistoryStrings);

		/*
		 * Add the table grid to the tab's parent container and add the parent
		 * container to the tab.
		 */
		parentContainer.getChildren().add(listView);
		tab.setContent(parentContainer);

		return tab;
	}

	/**
	 * Creates all the objects in the events tab
	 * 
	 * @return the events tab
	 */
	private Tab initializeEventsTab() {
		VBox parentContainer = new VBox();
		parentContainer.setMaxWidth(900);
		parentContainer.setAlignment(Pos.TOP_CENTER);

		// Create title for the tab.
		Label title = new Label("Events");
		title.setFont(new Font(16));
		parentContainer.getChildren().add(title);

		// Create grid containing tables
		GridPane tableGrid = new GridPane();
		tableGrid.setAlignment(Pos.CENTER);
		tableGrid.setHgap(10);

		// Modify table columns
		eventNameB.setMinWidth(140);
		eventDateB.setMinWidth(140);
		bookedEvents.setMinWidth(282);

		// Populate the table
		populateBookedEvents();

		// Add columns
		bookedEvents.getColumns().addAll(eventNameB, eventDateB);

		// Open an event details page when a row in the table is clicked
		bookedEvents.setRowFactory(tv -> {
			TableRow<EventRowBooked> row = new TableRow<EventRowBooked>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (!row.isEmpty())) {
					int eventID = row.getItem().getEventID();
					Event e = Datastore.getEvents().get(eventID);
					ViewEvent viewEvent = new ViewEvent();
					viewEvent.setEvent(e);
					viewEvent.start(s);
					s.setOnCloseRequest(e2 -> {
						upcommingEvents.getItems().clear();
						bookedEvents.getItems().clear();
						populateBookedEvents();
						populateUpcommingEvents();
					});

				}
			});

			return row;
		});

		// Modify upcomming events tables
		eventNameU.setMinWidth(140);
		eventDateU.setMinWidth(120);
		placesLeft.setMinWidth(120);
		upcommingEvents.setMinWidth(382);

		// Populate table
		populateUpcommingEvents();

		upcommingEvents.getColumns().addAll(eventNameU, eventDateU,
			placesLeft);

		// Open an event details page when a row in the table is clicked
		upcommingEvents.setRowFactory(tv -> {
			TableRow<EventRowUpcomming> row =
				new TableRow<EventRowUpcomming>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (!row.isEmpty())) {
					int eventID = row.getItem().getEventID();
					Event e = Datastore.getEvents().get(eventID);
					ViewEvent viewEvent = new ViewEvent();
					viewEvent.setEvent(e);
					viewEvent.start(s);
					s.setOnCloseRequest(e2 -> {
						populateBookedEvents();
						populateUpcommingEvents();
					});
				}
			});

			return row;
		});

		// Modify attended events tables
		eventNameA.setMinWidth(140);
		eventDateA.setMinWidth(140);
		attendedEvents.setMinWidth(282);

		// Populate table
		populateAttendedEvents();

		attendedEvents.getColumns().addAll(eventNameA, eventDateA);

		// Create table titles
		Label upcommingTitle = new Label("Upcomming events");
		upcommingTitle.setAlignment(Pos.CENTER);
		Label bookedTitle = new Label("Events you are booked on to");
		bookedTitle.setAlignment(Pos.CENTER);
		Label attendedTitle = new Label("Events you have attended");
		attendedTitle.setAlignment(Pos.CENTER);

		tableGrid.addColumn(0, upcommingTitle, upcommingEvents);
		tableGrid.addColumn(1, bookedTitle, bookedEvents);
		tableGrid.addColumn(2, attendedTitle, attendedEvents);

		parentContainer.getChildren().add(tableGrid);

		Tab eventsTab = new Tab("Events");
		eventsTab.setContent(parentContainer);

		return eventsTab;

	}

	/**
	 * Populates the booked events table
	 */
	private void populateBookedEvents() {
		// Get the current customer

		Customer currentCustomer = (Customer) Datastore.getCurrentUser();
		// If they have upcomming events...
		if (!currentCustomer.getUpcommingEvents().isEmpty()) {
			ObservableList<EventRowBooked> bookedEventsList =
				FXCollections.observableArrayList();
			// Turn all the events into row types
			for (Event e : currentCustomer.getUpcommingEvents()) {
				bookedEventsList.add(new EventRowBooked(e));
			}

			// Bind the properties of the row class to the relevant columns
			eventNameB.setCellValueFactory(
				new PropertyValueFactory<EventRowBooked, String>("name"));

			eventDateB.setCellValueFactory(
				new PropertyValueFactory<EventRowBooked, String>("date"));
			eventDateB.setSortType(SortType.ASCENDING);
			bookedEvents.setItems(bookedEventsList);

			// Sort the table by date
			bookedEvents.getSortOrder().add(eventDateB);
		}
	}

	/**
	 * Populates the attended events table
	 */
	private void populateAttendedEvents() {
		// Get the current customer

		Customer currentCustomer = (Customer) Datastore.getCurrentUser();
		// If they have attended events...
		if (!currentCustomer.getAttendedEvents().isEmpty()) {
			ObservableList<EventRowBooked> attendedEventsList =
				FXCollections.observableArrayList();
			// Turn all the events into row types
			for (Event e : currentCustomer.getAttendedEvents()) {
				attendedEventsList.add(new EventRowBooked(e));
			}

			// Bind the properties of the row class to the relevant columns
			eventNameA.setCellValueFactory(
				new PropertyValueFactory<EventRowBooked, String>("name"));

			eventDateA.setCellValueFactory(
				new PropertyValueFactory<EventRowBooked, String>("date"));

			attendedEvents.setItems(attendedEventsList);

			// Sort the table by date
			attendedEvents.getSortOrder().add(eventDateA);
		}
	}

	/**
	 * Populates the upcomming events table
	 */
	private void populateUpcommingEvents() {
		// Get a list of all the upcomming events
		ArrayList<Event> upcommingEventsDS = Datastore.getUpcommingEvents();
		// If there are upcomming events...
		if (!upcommingEventsDS.isEmpty()) {
			ObservableList<EventRowUpcomming> upcommingEventsList =
				FXCollections.observableArrayList();
			// Convert events into row types
			for (Event e : upcommingEventsDS) {
				upcommingEventsList.add(new EventRowUpcomming(e));
			}

			// Bind the properties of the row class to the relevant columns
			eventNameU.setCellValueFactory(
				new PropertyValueFactory<EventRowUpcomming, String>("name"));

			eventDateU.setCellValueFactory(
				new PropertyValueFactory<EventRowUpcomming, String>("date"));

			placesLeft.setCellValueFactory(
				new PropertyValueFactory<EventRowUpcomming, String>(
					"placesLeft"));

			upcommingEvents.setItems(upcommingEventsList);

			// Sort the table by date
			eventDateU.setSortType(SortType.DESCENDING);
			upcommingEvents.getSortOrder().add(eventDateU);
		}
	}

	/**
	 * Creates all objects in the top pane.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private void initializeTopPane() throws SQLException {
		// HBox object containing all the objects in the top pane.
		HBox topParentContainer = new HBox();
		/*
		 * GridPane object containing all objects related to changing the
		 * users' profile image.
		 */
		GridPane profileImageGrid = new GridPane();

		// Fetch the file names of the default avatars.
		ObservableList<String> avatarNames = fetchAvatarNames();

		// Import the default avatar images.
		ObservableList<Image> avatars = fetchAvatars(avatarNames);

		// Create label containing the profile image.
		Label profileImage = new Label();
		profileImage.setPadding(new Insets(10, 10, 10, 10));
		profileImageGrid.add(profileImage, 1, 0);

		final int PROFILE_IMAGE_WIDTH = 75;
		final int PROFILE_IMAGE_HEIGHT = 75;
		Image currentProfileImage =
			new Image(Datastore.getCurrentUser().getProfileImagePath(),
				PROFILE_IMAGE_WIDTH, PROFILE_IMAGE_HEIGHT, false, false);
		profileImage.setGraphic(new ImageView(currentProfileImage));

		// Create ComboBox containing all default avatar image names.
		ComboBox<String> selectAvatar = new ComboBox<String>(avatarNames);
		selectAvatar.setPromptText("Select a default avatar");

		// Create Button that opens the profile image drawing tool.
		Button drawProfileImageButton = new Button("Draw new Avatar");
		drawProfileImageButton.setMaxWidth(400);
		profileImageGrid.add(drawProfileImageButton, 0, 1);

		/*
		 * When the "Draw new Avatar" button is pressed, open the avatar
		 * drawing tool. When the window is closed, update the profile image.
		 */
		drawProfileImageButton.setOnAction(event -> {
			AvatarCreator avatarCreator = new AvatarCreator();
			Stage s = new Stage();
			avatarCreator.start(s);
			s.setOnCloseRequest(event2 -> {
				Image newProfileImage = new Image(
					Datastore.getCurrentUser().getProfileImagePath(),
					PROFILE_IMAGE_WIDTH, PROFILE_IMAGE_HEIGHT, false, false);
				profileImage.setGraphic(new ImageView(newProfileImage));
			});
		});

		/*
		 * Create a VBox that contains the ComboBox to select a default avatar
		 * and the button to draw a new one.
		 */
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.getChildren().addAll(selectAvatar, drawProfileImageButton);
		vbox.setMargin(drawProfileImageButton, new Insets(10, 10, 10, 0));

		profileImageGrid.add(vbox, 0, 0);

		/*
		 * When the image's name is selected from the drop down, change the
		 * profile image label to match it.
		 */
		selectAvatar.setOnAction((ActionEvent e) -> {
			String selectedImage = selectAvatar.getValue();
			switch (selectedImage) {
			case "Avatar 1":
				profileImage.setGraphic(new ImageView(avatars.get(0)));
				break;
			case "Avatar 2":
				profileImage.setGraphic(new ImageView(avatars.get(1)));
				break;
			case "Avatar 3":
				profileImage.setGraphic(new ImageView(avatars.get(2)));
				break;
			case "Avatar 4":
				profileImage.setGraphic(new ImageView(avatars.get(3)));
				break;
			case "Avatar 5":
				profileImage.setGraphic(new ImageView(avatars.get(4)));
				break;
			case "Avatar 6":
				profileImage.setGraphic(new ImageView(avatars.get(5)));
				break;
			}
			try {
				Datastore.getCurrentUser()
					.setProfileImageToDefault(selectedImage);
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
		});

		// Get today's date and put it in a new Text object.
		Date today = new Date();
		Text date = new Text(ddmmyy.format(today));

		// Create new Text objects that contain the users' balance and
		// username.
		Text username = new Text(Datastore.getCurrentUser().getUsername());
		Text balance = new Text("£" + currentUser.getBalance());
		Text balanceTitle = new Text("Current balance:");

		// Create logout button
		Button logout = new Button("Logout");
		logout.setOnAction((ActionEvent e) -> {
			LoginForm loginForm = new LoginForm();
			Datastore.logOut();
			loginForm.start(primaryStage);
		});

		// Create a GridPane to contain all these Text objects.
		GridPane labelContainer = new GridPane();
		labelContainer.setVgap(10);
		labelContainer.setHgap(5);

		labelContainer.add(username, 0, 1);
		labelContainer.add(date, 0, 0);
		labelContainer.add(balanceTitle, 2, 1);
		labelContainer.add(balance, 3, 1);
		labelContainer.add(logout, 0, 2);

		labelContainer.setMargin(date, new Insets(0, 0, 5, 0));
		labelContainer.setMargin(balanceTitle, new Insets(0, 0, 0, 15));
		labelContainer.setMargin(balance, new Insets(0, 0, 0, 5));

		/*
		 * Add all the created scene-graph nodes to the main parent container
		 * for the top of the border pane.
		 */
		topParentContainer.getChildren().setAll(labelContainer,
			profileImageGrid);

		topParentContainer.setMargin(labelContainer,
			new Insets(0, 400, 0, 10));
		profileImageGrid.setAlignment(Pos.CENTER_RIGHT);

		// Add the parent container to the border pane.
		borderPane.setTop(topParentContainer);

	}

	/**
	 * Imports all the default avatars from the user_avatars image folder.
	 * 
	 * @param avatarNames The file names of all the default avatar images.
	 * @return An ObservableList containing Image files with the default
	 *         avatars.
	 */
	public static ObservableList<Image>
		fetchAvatars(ObservableList<String> avatarNames) {
		final int PROFILE_IMAGE_WIDTH = 75;
		final int PROFILE_IMAGE_HEIGHT = 75;

		ObservableList<Image> avatars = FXCollections.observableArrayList();
		// Import all the default avatar images and initialize their size.
		SortedList<String> sortedNames = avatarNames.sorted();
		for (String e : sortedNames) {
			Image avatar = new Image("file:" + User.getDefaultAvatar(e),
				PROFILE_IMAGE_WIDTH, PROFILE_IMAGE_HEIGHT, false, false);
			avatars.add(avatar);
		}

		return avatars;
	}

	/**
	 * Fetches all the files names of the default avatar images.
	 * 
	 * @return An ObervableList containing all default avatar file names.
	 */
	public static ObservableList<String> fetchAvatarNames() {
		ObservableList<String> avatarNames =
			FXCollections.observableArrayList();
		// Get the avatar image file names from the User class.
		Set<String> avatarNamesArray = User.getAvatarNames();
		// Add each one to the new ObservableList.
		for (String e : avatarNamesArray) {
			avatarNames.add(e);
		}

		return avatarNames;
	}

	/**
	 * Creates and populates the table containing a users' loaned items.
	 * 
	 * @param loanedItems the table of loaned items.
	 * @param resourceCol the resource column.
	 * @param dueDateCol  the due date column.
	 * @return A table containing the users' loaned items.
	 */
	private TableView<ItemRow1> populateLoanedItems(TableView loanedItems,
		TableColumn resourceCol, TableColumn dueDateCol) {
		if (!currentUser.getCurrentLoans().isEmpty()) {
			ObservableList<ItemRow1> loanedCopies =
				FXCollections.observableArrayList();
			for (Copy c : currentUser.getCurrentLoans()) {
				loanedCopies.add(new ItemRow1(c));
			}

			resourceCol.setCellValueFactory(
				new PropertyValueFactory<ItemRow1, String>("title"));
			dueDateCol.setCellValueFactory(
				new PropertyValueFactory<ItemRow1, String>("dueDate"));

			loanedItems.setItems(loanedCopies);

		}

		return loanedItems;
	}

	private TableView<ItemRow1> populateReservedItems(TableView reservedItems,
		TableColumn resourceCol) {
		if (!currentUser.getCurrentReserves().isEmpty()) {
			ObservableList<ItemRow1> reservedCopies =
				FXCollections.observableArrayList();
			for (Copy c : currentUser.getCurrentReserves()) {
				reservedCopies.add(new ItemRow1(c));
			}

			resourceCol.setCellValueFactory(
				new PropertyValueFactory<ItemRow1, String>("title"));
			reservedItems.setItems(reservedCopies);
		}

		return reservedItems;
	}

	/**
	 * Creates and populates the table containing a users' requested items.
	 * 
	 * @param requestedItems the table to populate.
	 * @param resourceCol    the column for Resources.
	 * @return A table containing the users' requested items.
	 */
	private TableView<ItemRow2> populateRequestedItems(
		TableView requestedItems, TableColumn resourceCol) {

		if (!currentUser.getCurrentRequests().isEmpty()) {
			ObservableList<ItemRow2> loanedCopies =
				FXCollections.observableArrayList();
			for (Resource r : currentUser.getCurrentRequests()) {
				loanedCopies.add(new ItemRow2(r));
			}

			resourceCol.setCellValueFactory(
				new PropertyValueFactory<ItemRow2, String>("title"));

			requestedItems.setItems(loanedCopies);

		}

		return requestedItems;

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
		 * Takes a copy and extracts the correct attributes from it.
		 * 
		 * @param copy the copy to extract attributes from.
		 */
		public ItemRow1(Copy copy) {
			title = new SimpleStringProperty(copy.getResourceRef().getTitle()
				+ " (" + copy.getResourceRef().getType() + ")");
			Date tempDate = copy.getDueDate();
			if (tempDate != null) {
				dueDate = new SimpleStringProperty(ddmmyy.format(tempDate));
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
	}

	/**
	 * Defines a row in the requested items table that contains data from a
	 * resource class.
	 * 
	 * @author Mike
	 */
	public class ItemRow2 {
		/**
		 * The title of the resource with its type.
		 */
		private SimpleStringProperty title;

		/**
		 * Creates a new ItemRow2 object.
		 * 
		 * @param resource The resource that the data is extracted from.
		 */
		public ItemRow2(Resource resource) {
			title = new SimpleStringProperty(
				resource.getTitle() + " (" + resource.getType() + ")");
		}

		/**
		 * @return The title of the resource of the copy.
		 */
		public String getTitle() {
			return title.get();
		}
	}

	/**
	 * Defines a row in the table displaying booked events for the user
	 * 
	 * @author Mike
	 */
	public class EventRowBooked {
		private int eventID;
		/**
		 * The name of the event
		 */
		private SimpleStringProperty name;
		/**
		 * The date the event takes place
		 */
		private SimpleStringProperty date;

		public EventRowBooked(Event event) {
			name = new SimpleStringProperty(event.getName());
			date = new SimpleStringProperty(
				event.getDate().toString().substring(0, 16));
			eventID = event.getUniqueID();
		}

		public String getName() {
			return name.get();
		}

		public String getDate() {
			return date.get();
		}

		public int getEventID() {
			return eventID;
		}
	}

	/**
	 * Defines a row in the table displaying upcomming events
	 * 
	 * @author Mike
	 */
	public class EventRowUpcomming {
		private int eventID;
		/**
		 * The name of the event
		 */
		private SimpleStringProperty name;
		/**
		 * The date the event takes place
		 */
		private SimpleStringProperty date;
		/**
		 * The places left on the event
		 */
		private SimpleStringProperty placesLeft;

		public EventRowUpcomming(Event event) {
			name = new SimpleStringProperty(event.getName());
			date = new SimpleStringProperty(
				event.getDate().toString().substring(0, 16));
			placesLeft = new SimpleStringProperty(
				String.valueOf(event.getPlacesLeft()));
			eventID = event.getUniqueID();

		}

		public String getName() {
			return name.get();
		}

		public String getDate() {
			return date.get();
		}

		public String getPlacesLeft() {
			return placesLeft.get();
		}

		public int getEventID() {
			return eventID;
		}
	}

}
