package JavaFX;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import Core.*;
import JavaFX.UserUI.EventRowBooked;
import JavaFX.UserUI.EventRowUpcomming;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import java.util.ArrayList;
import java.util.Date;

/**
 * Creates and provides functionality for the Librarian dashboard.
 * 
 * @author Mike
 */
public class LibrarianUI extends Application {

	/**
	 * The main pane for the window.
	 */
	private BorderPane borderPane = new BorderPane();
	private final int MAIN_WINDOW_WIDTH = 1100;
	private final int MAIN_WINDOW_HEIGHT = 700;
	private Librarian currentLibrarian =
		(Librarian) Datastore.getCurrentUser();
	private Stage primaryStage;

	private TableView<EventRowUpcomming> upcommingEvents = new TableView<>();
	private TableColumn<EventRowUpcomming, String> eventNameU =
		new TableColumn<>("Event name");
	private TableColumn<EventRowUpcomming, String> eventDateU =
		new TableColumn<>("Event date and time");
	private TableColumn<EventRowUpcomming, String> placesLeft =
		new TableColumn<>("Places left");

	public static void main(String[] args) {
		User.addDefaultAvatar("Avatar 1",
			"file:images/user_avatars/Avatar2.jpg");
		User.addDefaultAvatar("Avatar 2",
			"file:images/user_avatars/Avatar3.jpg");
		User.addDefaultAvatar("Avatar 3",
			"file:images/user_avatars/Avatar4.jpg");
		User.addDefaultAvatar("Avatar 4",
			"file:images/user_avatars/Avatar5.jpg");
		User.addDefaultAvatar("Avatar 5",
			"file:images/user_avatars/Avatar6.jpg");
		User.addDefaultAvatar("Avatar 6",
			"file:images/user_avatars/Avatar7.jpg");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		primaryStage.setTitle("Tawe-Lib Librarian Dashboard");

		initializeCentrePane();

		initializeLeftPane();

		initializeTopPane();

		initializeBottomPane();

		// Construct the window.
		Scene scene =
			new Scene(borderPane, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);

		MoveTo moveTo = new MoveTo();
		moveTo.setX(0.0);
		moveTo.setY(0.0);

		primaryStage.show();
	}

	/**
	 * Creates all objects in the centre pane of the border pane.
	 */
	private void initializeCentrePane() {
		// Create parent container
		VBox centreParentContainer = new VBox(10);
		centreParentContainer.setAlignment(Pos.TOP_CENTER);
		// Create tab title
		Label title = new Label("Upcomming Events");
		title.setFont(new Font(17));

		// Modify the table columns
		eventNameU.setMinWidth(300);
		eventDateU.setMinWidth(240);
		placesLeft.setMinWidth(180);
		upcommingEvents.setMaxWidth(722);

		// Populate the table
		populateUpcommingEvents();

		// Add the columns to the table
		upcommingEvents.getColumns().addAll(eventNameU, eventDateU,
			placesLeft);

		// Open the details page for an event when the row is clicked
		upcommingEvents.setRowFactory(tv -> {
			TableRow<EventRowUpcomming> row =
				new TableRow<EventRowUpcomming>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (!row.isEmpty())) {
					int eventID = row.getItem().getEventID();
					Event e = Datastore.getEvents().get(eventID);
					LibrarianViewEvent librarianViewEvent =
						new LibrarianViewEvent();
					librarianViewEvent.setEvent(e);
					Stage s = new Stage();
					librarianViewEvent.start(s);
					s.setOnCloseRequest(e2 -> {
						upcommingEvents.getItems().clear();
						populateUpcommingEvents();
					});
				}
			});

			return row;
		});

		centreParentContainer.getChildren().addAll(title, upcommingEvents);

		// Add the centre container to the borderPane.
		borderPane.setCenter(centreParentContainer);

	}

	private void populateUpcommingEvents() {
		// Get a list of all the upcomming events
		ArrayList<Event> upcommingEventsDS = Datastore.getUpcommingEvents();
		// If there are upcomming events...

		ObservableList<EventRowUpcomming> upcommingEventsList =
			FXCollections.observableArrayList();
		// Convert events into row types
		for (Event e : upcommingEventsDS) {
			upcommingEventsList.add(new EventRowUpcomming(e));
		}

		// Bind properties of the row class to their respective columns
		eventNameU.setCellValueFactory(
			new PropertyValueFactory<EventRowUpcomming, String>("name"));

		eventDateU.setCellValueFactory(
			new PropertyValueFactory<EventRowUpcomming, String>("date"));

		placesLeft.setCellValueFactory(
			new PropertyValueFactory<EventRowUpcomming, String>("placesLeft"));

		// Add the items to the table
		upcommingEvents.setItems(upcommingEventsList);
		// Make them sort by date
		upcommingEvents.getSortOrder().add(eventDateU);
	}

	/**
	 * Creates all objects in the left pane of the border pane.
	 */
	private void initializeLeftPane() {
		// VBox that will contain all buttons for Librarians to navigate the
		// system.
		VBox buttonBox = new VBox(8);
		buttonBox.setPadding(new Insets(65, 0, 0, 10));

		// Create buttons.
		Button accessResources = new Button("Access Resources");
		Button accessCustomers = new Button("Search Users");
		Button addUser = new Button("Add new Customer");
		Button addLibrarian = new Button("Add new Librarian");
		Button addEvent = new Button("Add new event");
		Button viewFineStats = new Button("View fine statistics");
		Button viewPopularResourceStats =
			new Button("View popular resource statistics");

		accessResources.setOnAction(event -> {
			ResourceSearch resourceSearch = new ResourceSearch();
			resourceSearch.start(new Stage());
		});

		accessCustomers.setOnAction(event -> {
			UserSearch userSearch = new UserSearch();
			userSearch.start(new Stage());
		});

		addUser.setOnAction(event -> {
			UserForm userForm = new UserForm();
			userForm.start(new Stage());
		});

		addLibrarian.setOnAction(event -> {
			CreateLibrarian createLibrarian = new CreateLibrarian();
			createLibrarian.start(new Stage());
		});

		addEvent.setOnAction(event -> {
			CreateEvent createEvent = new CreateEvent();
			Stage s = new Stage();
			createEvent.start(s);
			s.setOnCloseRequest(e -> {
				populateUpcommingEvents();
			});
		});

		viewFineStats.setOnAction(e -> {
			LibrarianFineStatistics librarianFineStatistics =
				new LibrarianFineStatistics();
			librarianFineStatistics.start(new Stage());
		});

		viewPopularResourceStats.setOnAction(e -> {
			LibrarianPopularResources librarianPopularResources =
				new LibrarianPopularResources();
			librarianPopularResources.start(new Stage());
		});

		// Add buttons to the button box.
		buttonBox.getChildren().addAll(accessResources, accessCustomers,
			addUser, addLibrarian, addEvent, viewFineStats,
			viewPopularResourceStats);

		// Add the button box to the main border pane.
		borderPane.setLeft(buttonBox);
	}

	/**
	 * Creates all objects in the top pane of the border pane.
	 */
	private void initializeTopPane() {
		// HBox object containing all the objects in the top pane.
		HBox topParentContainer = new HBox();
		/*
		 * GridPane object containing all objects related to changing the
		 * users' profile image.
		 */
		GridPane profileImageGrid = new GridPane();

		// Fetch the file names of the default avatars.
		ObservableList<String> avatarStrings = UserUI.fetchAvatarNames();

		// Import the default avatar images
		ObservableList<Image> avatars = UserUI.fetchAvatars(avatarStrings);

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
		ComboBox<String> selectAvatar = new ComboBox<String>(avatarStrings);
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
		vbox.getChildren().add(selectAvatar);
		vbox.getChildren().add(drawProfileImageButton);
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
		Text date = new Text(UserUI.ddmmyy.format(today));

		// Create new Text objects that contain the users' username.
		Text username = new Text(Datastore.getCurrentUser().getUsername());

		// Create a GridPane to contain all these Text objects.
		GridPane lableContainer = new GridPane();
		lableContainer.setVgap(15);

		// Create logout button
		Button logout = new Button("Logout");
		logout.setOnAction((ActionEvent e) -> {
			LoginForm loginForm = new LoginForm();
			loginForm.start(primaryStage);
		});

		lableContainer.add(username, 0, 1);
		lableContainer.add(date, 0, 0);
		lableContainer.add(logout, 0, 2);

		lableContainer.setMargin(date, new Insets(0, 0, 5, 0));

		/*
		 * Add all the created scene-graph nodes to the main parent container
		 * for the top of the border pane.
		 */
		topParentContainer.getChildren().setAll(lableContainer,
			profileImageGrid);

		topParentContainer.setMargin(lableContainer,
			new Insets(0, 400, 0, 10));
		profileImageGrid.setAlignment(Pos.CENTER_RIGHT);

		// Add the parent container to the border pane.
		borderPane.setTop(topParentContainer);

	}

	/**
	 * Creates all objects in the top pane of the border pane.
	 */
	private void initializeBottomPane() {
		// VBox that will contain all objects for changing account details
		VBox accountDetailsContainer = new VBox(30);
		accountDetailsContainer.setAlignment(Pos.TOP_CENTER);

		// Create title.
		Label title = new Label("Librarian Account Info");
		title.setFont(new Font(17));
		accountDetailsContainer.getChildren().add(title);

		/*
		 * GridPane that will contain all titles, text boxes and confirm
		 * buttons that allow librarians to change their details.
		 */
		GridPane centreGrid = new GridPane();
		centreGrid.setHgap(30);
		centreGrid.setVgap(5);
		centreGrid.setAlignment(Pos.TOP_CENTER);

		// Create all field title Labels.
		Label firstNameTitle = new Label("First name:");
		Label lastNameTitle = new Label("Last name:");
		Label telephoneNoTitle = new Label("Telephone Number:");
		Label addressTitle = new Label("Address:");
		Label postCodeTitle = new Label("Post Code:");
		Label confirmationMessage = new Label();
		confirmationMessage.setTextFill(Color.DARKGREEN);

		// Create all textboxes where Librarians can enter new info.
		TextField fNameInput = new TextField(currentLibrarian.getFirstName());
		fNameInput.setMinWidth(300);
		TextField lNameInput = new TextField(currentLibrarian.getSurname());
		lNameInput.setMinWidth(200);
		TextField phoneNoInput =
			new TextField(currentLibrarian.getMobileNumber());
		phoneNoInput.setMinWidth(200);
		TextField addressInput = new TextField(currentLibrarian.getAddress());
		addressInput.setMinWidth(200);

		// Create a confirm button.
		Button OK = new Button("Change");
		OK.setAlignment(Pos.CENTER);
		OK.setOnAction(e -> {
			try {
				currentLibrarian.editUser(currentLibrarian.getUsername(),
					fNameInput.getText(), lNameInput.getText(),
					phoneNoInput.getText(), addressInput.getText());
				confirmationMessage
					.setText("Account information updated succesfully");
			} catch (SQLException ev) {
				System.out.println(ev);
			}
		});

		// Add all the objects to the centre grid.
		centreGrid.addRow(0, firstNameTitle, fNameInput);
		centreGrid.addRow(1, lastNameTitle, lNameInput);
		centreGrid.addRow(2, telephoneNoTitle, phoneNoInput);
		centreGrid.addRow(3, addressTitle, addressInput);
		centreGrid.addRow(4, OK, confirmationMessage);
		// centreGrid.addRow(5, confirmationMessage);

		// Add the centre grid to the centre container.
		accountDetailsContainer.getChildren().add(centreGrid);

		// VBox containing all objects in the bottom pane.
		VBox newResourcesContainer = new VBox(10);
		newResourcesContainer.setAlignment(Pos.TOP_CENTER);
		newResourcesContainer.setPadding(new Insets(0, 0, 50, 0));

		// Create title for the bottom pane.
		Label resourcesTitle = new Label("Add New Resource");
		title.setFont(new Font(15));

		/*
		 * HBox containing a ComboBox containing all resource types and a label
		 * for the ComboBox
		 */
		HBox newResourceTypeContainer = new HBox(10);
		newResourceTypeContainer.setAlignment(Pos.CENTER);

		Label type = new Label("Select resource type:");

		// Create ObsevableList containing all resource types in the system.
		ObservableList<String> resourceTypes = FXCollections
			.observableArrayList("Book", "DvD", "Laptop", "VideoGame");
		// Create ComboBox containing created ObservableList.
		ComboBox<String> selectResourceType =
			new ComboBox<String>(resourceTypes);
		selectResourceType.getSelectionModel().selectFirst();

		newResourceTypeContainer.getChildren().addAll(type,
			selectResourceType);

		Button confirm = new Button("Create new resource");

		confirm.setOnAction(e -> {
			switch (selectResourceType.getValue()) {
			case "Book":
				CreateBook createBook = new CreateBook();
				createBook.start(new Stage());
				break;
			case "DvD":
				CreateDVD createDVD = new CreateDVD();
				createDVD.start(new Stage());
				break;
			case "Laptop":
				CreateLaptop createLaptop = new CreateLaptop();
				createLaptop.start(new Stage());
				break;
			case "VideoGame":
				CreateVideoGame createVideoGame = new CreateVideoGame();
				createVideoGame.start(new Stage());
			}
		});

		newResourcesContainer.getChildren().addAll(resourcesTitle,
			newResourceTypeContainer, confirm);

		HBox bottomParentContainer = new HBox(60);
		bottomParentContainer.setAlignment(Pos.TOP_CENTER);
		bottomParentContainer.getChildren().addAll(newResourcesContainer,
			accountDetailsContainer);
		bottomParentContainer.setPadding(new Insets(20, 0, 20, 0));

		borderPane.setBottom(bottomParentContainer);

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
		 * The number of places left on the event
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
