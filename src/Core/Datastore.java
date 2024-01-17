package Core;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;

import java.util.Date;

/**
 * Class which holds and manipulates the data classes in the database. This
 * entire class is static, with its variables public; any and every GUI class
 * should be able to access and manipulate the data in this class.
 * 
 * @author Benjamin Kennard
 * @version 1.1
 */

public class Datastore {
	private static Connection db;
	private static HashMap<String, User> users;
	private static HashMap<Integer, Resource> resources;
	private static HashMap<Integer, Event> events;
	private static HashMap<Integer, Resource> newAdditions;
	private static User currentUser;

	/**
	 * Method to return a populated arraylist of type resource
	 * 
	 * @param lastLogin -last login time of a user
	 * @return newAdditions arraylist containing only new additions since last
	 *         login
	 * @throws SQLException if SQL errors.
	 */
	public static ArrayList<Resource> newAdditions(LocalDateTime lastLogin)
		throws SQLException {
		ArrayList<Resource> newAdditions = new ArrayList<Resource>();
		PreparedStatement readNewAdditions = Datastore.db.prepareStatement(
			"SELECT resourcesId FROM resources WHERE creationDate > ?;");

		readNewAdditions.setTimestamp(1, Timestamp.valueOf(lastLogin));
		ResultSet rs = readNewAdditions.executeQuery();

		// Add them to the array
		while (rs.next()) {
			newAdditions.add(resources.get(rs.getInt("ResourcesId")));
		}
		return newAdditions;
	}

	/**
	 * This method updates the last login time of a customer. Finds the
	 * customer by the passed customerId and updates last login in the
	 * database.
	 * 
	 * @param lastLogin  - last login of a user
	 * @param customerId - the id of the customer whose last login will be
	 *                   updated
	 * @throws SQLException if SQL errors.
	 */
	public static void updateLastSeen(LocalDateTime lastLogin, int customerId)
		throws SQLException {
		PreparedStatement updateLastSeen = db.prepareStatement(
			"UPDATE Customers SET LastSeen = ? where usersId = ?;");

		updateLastSeen.setTimestamp(1, Timestamp.valueOf(lastLogin));
		updateLastSeen.setInt(2, customerId);

		updateLastSeen.executeUpdate();
	}

	/**
	 * Initialises the data for the GUI.
	 * 
	 * @param dbConnection connection string to the MySQL database, without any
	 *                     parameters. Format is jdbc:mysql://localhost:[sql
	 *                     port number]/[database name] .
	 * @param dbUser       the username for logging in to the database.
	 * @param dbPass       the password for logging in to the database.
	 * @throws SQLException if SQL errors.
	 */
	public static void init(String dbConnection, String dbUser, String dbPass)
		throws SQLException {
		// Initialise variables
		Datastore.users = new HashMap<String, User>();
		Datastore.resources = new HashMap<Integer, Resource>();
		Datastore.events = new HashMap<Integer, Event>();
		Datastore.newAdditions = new HashMap<Integer, Resource>();
		Datastore.currentUser = null;
		// Add allowMultiQueries parameter to SQL connection string
		Datastore.db = DriverManager.getConnection(
			dbConnection + "?allowMultiQueries=true", dbUser, dbPass);
		// Load data
		Datastore.loadEvents();
		Datastore.loadUsers();
		Datastore.loadResources();
		Datastore.updateUsersPostResources();
		Datastore.loadUserAvatars();
		Datastore.loadReviewsAndRatingsForAllResources();
	}

	/**
	 * Creates a new customer, adds them to the database and the collection of
	 * customers.
	 * 
	 * @param userName         The customer's username.
	 * @param firstName        The customer's first name.
	 * @param surname          The customer's surname.
	 * @param mobileNumber     The customer's mobile number.
	 * @param address          The customer's address.
	 * @param profileImagePath The path to the customer's profile image.
	 * @throws SQLException if SQL errors.
	 * @return the customer created.
	 */
	public static Customer createCustomer(String userName, String firstName,
		String surname, String mobileNumber, String address,
		String profileImagePath) throws SQLException {
		// Create Customer in DB
		PreparedStatement createCustomer = db.prepareStatement(
			"INSERT INTO users (userName, firstName, surname,"
				+ " mobileNumber, address, profileImagePath) "
				+ " VALUES (?, ?, ?, ?, ?, ?); INSERT INTO customers"
				+ " (usersId, balance) VALUES (LAST_INSERT_ID(), 0);");
		createCustomer.setString(1, userName);
		createCustomer.setString(2, firstName);
		createCustomer.setString(3, surname);
		createCustomer.setString(4, mobileNumber);
		createCustomer.setString(5, address);
		createCustomer.setString(6, profileImagePath);
		createCustomer.executeUpdate();
		// Get ID which was just generated
		int custId = getLatestUniqueId("customers", "usersId");
		// Add customer to users list
		Customer out = new Customer(custId, userName, firstName, surname,
			mobileNumber, address, profileImagePath);
		users.put(userName, out);
		return out;
	}

	/**
	 * Loads all users into the Users collection.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadUsers() throws SQLException {
		Datastore.loadCustomers();
		Datastore.loadLibrarians();
	}

	/**
	 * Loads librarians into the users collection.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadLibrarians() throws SQLException {
		// Get librarian information
		PreparedStatement readLibrarian = db.prepareStatement("SELECT Users.*,"
			+ "Librarians.staffNumber, Librarians.employmentDate FROM "
			+ "Librarians JOIN Users ON"
			+ " Users.usersId = Librarians.usersId;");
		ResultSet rs = readLibrarian.executeQuery();
		while (rs.next()) {
			int usersId = rs.getInt("usersId");
			String userName = rs.getString("username");
			String firstName = rs.getString("firstName");
			String surname = rs.getString("surname");
			String mobileNumber = rs.getString("mobileNumber");
			String address = rs.getString("address");
			String profileImagePath = rs.getString("profileImagePath");
			int staffNumber = rs.getInt("staffNumber");
			Date employmentDate = rs.getDate("employmentDate");
			// Add librarian to users list
			Datastore.users.put(userName,
				new Librarian(usersId, userName, firstName, surname,
					mobileNumber, address, profileImagePath, staffNumber,
					employmentDate));
		}
	}

	/**
	 * Loads the customers into the users collection.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadCustomers() throws SQLException {
		// Get customer information
		PreparedStatement readCustomer = db.prepareStatement("SELECT Users.*,"
			+ "Customers.balance, Customers.LastSeen FROM Customers JOIN Users ON"
			+ " Users.usersId = Customers.usersId;");
		ResultSet rs = readCustomer.executeQuery();
		while (rs.next()) {
			int usersId = rs.getInt("usersId");
			String userName = rs.getString("username");
			String firstName = rs.getString("firstName");
			String surname = rs.getString("surname");
			String mobileNumber = rs.getString("mobileNumber");
			String address = rs.getString("address");
			String profileImagePath = rs.getString("profileImagePath");
			int balance = rs.getInt("balance");

			LocalDateTime lastLogin =
				rs.getTimestamp("LastSeen").toLocalDateTime();
			// Get TransactionPayments for this user
			ArrayList<Transaction> paymentHistory =
				Datastore.getTransactionPayments(usersId);
			// Load the users event bookings
			ArrayList<ArrayList<Event>> eventsArrayLists =
				Datastore.getEventBookings(usersId);
			ArrayList<Event> attendedEvents = eventsArrayLists.get(0);
			ArrayList<Event> upcommingEvents = eventsArrayLists.get(1);

			// Add customer to users list
			Datastore.users.put(userName,
				new Customer(usersId, userName, firstName, surname,
					mobileNumber, address, profileImagePath, balance,
					paymentHistory, attendedEvents, upcommingEvents,
					lastLogin));
		}
	}

	/**
	 * Gets the customer's transaction payments. Payments can be generated
	 * before copies are loaded, and so are loaded seperately and prior to
	 * fines.
	 * 
	 * @param CustId The unique ID of the customer whose payments are being
	 *               loaded.
	 * @return an arraylist of the customer's loans.
	 * @throws SQLException if SQL errors.
	 */
	private static ArrayList<Transaction> getTransactionPayments(int CustId)
		throws SQLException {
		ArrayList<Transaction> paymentHistory = new ArrayList<Transaction>();
		// Read transactionPayments
		PreparedStatement readTransactionPayments =
			Datastore.db.prepareStatement(
				"SELECT * FROM transactionpayments tp WHERE tp.custRef = ?;");
		readTransactionPayments.setInt(1, CustId);
		ResultSet rs = readTransactionPayments.executeQuery();
		// Add them to the array
		while (rs.next()) {
			int id = rs.getInt("transactionPaymentsId");
			Date transactionDate = rs.getDate("transactionDate");
			int amount = rs.getInt("amount");
			paymentHistory
				.add(new TransactionPayment(id, amount, transactionDate));
		}
		return paymentHistory;
	}

	/**
	 * Gets the event bookings for a user and sorts them in to two arraylists:
	 * attended events and upcomming events.
	 * 
	 * @param custID The ID of the customer
	 * @return an array containing two Event ArrayLists
	 * @throws SQLException if SQL errors.
	 * @author Mike
	 */
	private static ArrayList<ArrayList<Event>> getEventBookings(int custID)
		throws SQLException {
		ArrayList<Event> upcomming = new ArrayList<Event>();
		ArrayList<Event> attended = new ArrayList<Event>();
		Date today = new Date();
		PreparedStatement readEventBookings = Datastore.db.prepareStatement(
			"SELECT * FROM eventbookings WHERE customerRef = ?;");
		readEventBookings.setInt(1, custID);
		ResultSet rs = readEventBookings.executeQuery();
		// Sort through the bookings to see if they have already taken place
		while (rs.next()) {
			int eventID = rs.getInt("eventsId");
			Event e = Datastore.events.get(eventID);
			e.addAttendee();
			if (today.compareTo(e.getDate()) > 0) {
				attended.add(e);
			} else {
				upcomming.add(e);
			}
		}

		ArrayList<ArrayList<Event>> eventArrayLists =
			new ArrayList<ArrayList<Event>>();
		eventArrayLists.add(attended);
		eventArrayLists.add(upcomming);

		return eventArrayLists;
	}

	/**
	 * Loads the events from the database into the events hashmap.
	 * 
	 * @author Mike
	 * @throws SQLException if SQL errors.
	 */
	private static void loadEvents() throws SQLException {
		PreparedStatement readEvents =
			db.prepareStatement("SELECT * FROM EVENTS;");
		ResultSet rs = readEvents.executeQuery();
		while (rs.next()) {
			int eventID = rs.getInt("eventsId");
			String eventName = rs.getString("title");
			Timestamp eventDate = rs.getTimestamp("eventDate");
			int maxAttendees = rs.getInt("maxAttendees");
			String description = rs.getString("description");
			Datastore.events.put(eventID, new Event(eventID, eventDate,
				eventName, maxAttendees, description));
		}
	}

	/**
	 * Gets the events that are taking place after the current date/time
	 * 
	 * @return An arraylist of type event
	 * @author Mike
	 */
	public static ArrayList<Event> getUpcommingEvents() {
		Date today = new Date();
		ArrayList<Event> upcommingEvents = new ArrayList<>();
		// Loop through each element in the hashmap
		for (Entry<Integer, Event> entry : events.entrySet()) {
			Date eventDate = entry.getValue().getDate();
			// If the event date is after the current date, add it to the
			// arraylist
			if (eventDate.after(today)) {
				upcommingEvents.add(entry.getValue());
			}
		}
		return upcommingEvents;
	}

	/**
	 * Loads resources and their copies from the database. Additionally,
	 * updates Customers with resource-related information.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadResources() throws SQLException {
		// Load resources
		loadBooks();
		loadDVDs();
		loadLaptopComputers();
		loadVideoGames();
		// Load additional resource information
		loadCopiesAndRequests();

	}

	/**
	 * Loads all books from the database.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadBooks() throws SQLException {
		// Get book information
		PreparedStatement readBook = db.prepareStatement(
			"SELECT Resources.title, Resources.year, Resources.creationDate ,"
				+ " books.* FROM Books JOIN Resources ON"
				+ " Books.ResourcesId = Resources.ResourcesId;");
		ResultSet rs = readBook.executeQuery();
		while (rs.next()) {
			// Get resource fields
			int resourcesID = rs.getInt("resourcesId");
			String title = rs.getString("title");
			int year = rs.getInt("year");
			LocalDateTime additionDate =
				rs.getTimestamp("creationDate").toLocalDateTime();

			// Get book fields
			String author = rs.getString("author");
			String publisher = rs.getString("publisher");
			String genre = rs.getString("genre");
			String ISBN = rs.getString("ISBN");
			String language = rs.getString("language");
			// Create new resource
			resources.put(resourcesID, new Book(resourcesID, title, year,
				author, publisher, genre, ISBN, language, additionDate));
		}
	}

	/**
	 * Loads all DVDs from the database. Additionally, loads the collection of
	 * their subtitle languages.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadDVDs() throws SQLException {
		// Get book information
		PreparedStatement readDVDs = db.prepareStatement(
			"SELECT Resources.title, Resources.year,Resources.creationDate,"
				+ " DVDs.* FROM DVDs JOIN Resources ON"
				+ " DVDs.ResourcesId = Resources.ResourcesId;");
		ResultSet rs = readDVDs.executeQuery();
		while (rs.next()) {
			// Get resource fields
			int resourcesID = rs.getInt("resourcesId");
			String title = rs.getString("title");
			int year = rs.getInt("year");
			LocalDateTime additionDate =
				rs.getTimestamp("creationDate").toLocalDateTime();
			// Get DVD fields
			String director = rs.getString("director");
			int runtime = rs.getInt("runtime");
			String language = rs.getString("language");
			// Get DVD subtitle languages
			ArrayList<String> dvdSubs = loadDVDSubtitles(resourcesID);
			// Create new resource
			resources.put(resourcesID, new DVD(resourcesID, title, year,
				director, runtime, language, dvdSubs, additionDate));
		}
	}

	/**
	 * Gets the subtitle languages of a particular DVD.
	 * 
	 * @param resourcesId The id of the DVD whose subtitles should be gotten.
	 * @return The collection of languages the DVD has subtitles for.
	 * @throws SQLException if SQL errors.
	 */
	private static ArrayList<String> loadDVDSubtitles(int resourcesId)
		throws SQLException {
		ArrayList<String> dvdSubs = new ArrayList<String>();
		// Get data from db
		PreparedStatement readDVDSubs = db.prepareStatement("SELECT "
			+ "subtitleLanguage FROM dvdSubtitles WHERE resourcesId =" + " ?");
		readDVDSubs.setInt(1, resourcesId);
		ResultSet rs = readDVDSubs.executeQuery();
		while (rs.next()) {
			dvdSubs.add(rs.getString(1));
		}
		return dvdSubs;
	}

	/**
	 * Loads all laptop computers from the database.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadLaptopComputers() throws SQLException {
		// Get laptopComputer information
		PreparedStatement readlaptopComputers = db.prepareStatement("SELECT "
			+ "Resources.title, Resources.year,Resources.creationDate,"
			+ " laptopComputers.* FROM laptopComputers JOIN Resources ON"
			+ " laptopComputers.ResourcesId = Resources.ResourcesId;");
		ResultSet rs = readlaptopComputers.executeQuery();
		while (rs.next()) {
			// Get resource fields
			int resourcesID = rs.getInt("resourcesId");
			String title = rs.getString("title");
			int year = rs.getInt("year");

			LocalDateTime additionDate =
				rs.getTimestamp("creationDate").toLocalDateTime();
			// Get laptopComputer fields
			String manufacturer = rs.getString("manufacturer");
			String model = rs.getString("model");
			String OS = rs.getString("OS");
			// Create new resource
			resources.put(resourcesID, new LaptopComputer(resourcesID, title,
				year, manufacturer, model, OS, additionDate));
		}
	}

	/**
	 * Loads all VideoGames from the database.
	 * 
	 * @author Billy Roberts
	 * @throws SQLException if SQL errors.
	 */
	private static void loadVideoGames() throws SQLException {
		// Get VideoGame information
		PreparedStatement readVideoGames = db.prepareStatement("SELECT "
			+ "Resources.title, Resources.year,Resources.creationDate,"
			+ " videogames.* FROM videogames JOIN Resources ON"
			+ " videogames.ResourcesId = Resources.ResourcesId;");
		ResultSet rs = readVideoGames.executeQuery();
		while (rs.next()) {
			// Get resource fields
			int resourcesID = rs.getInt("resourcesId");
			String title = rs.getString("title");
			int year = rs.getInt("year");

			LocalDateTime additionDate =
				rs.getTimestamp("creationDate").toLocalDateTime();
			// Get VideoGame fields
			String publisher = rs.getString("Publisher");
			String genre = rs.getString("Genre");
			String certificateRating = rs.getString("CertificateRating");
			int tempMultiplayerSupport = rs.getInt("MultiplayerSupport");
			boolean multiplayerSupport;
			if (tempMultiplayerSupport == 1) {
				multiplayerSupport = true;
			} else {
				multiplayerSupport = false;
			}
			// Create new resource
			resources.put(resourcesID,
				new VideoGame(resourcesID, title, year, publisher, genre,
					certificateRating, multiplayerSupport, additionDate));
		}
	}

	/**
	 * Loads all requests for a specific resource, and updates the customers
	 * who made the requests.
	 * 
	 * @param res The resource whose requests are being dealt with.
	 * @return The queue of requests for the resource given.
	 * @throws SQLException if SQL errors.
	 */
	private static RequestQueue<Customer> loadResourceRequests(Resource res)
		throws SQLException {
		RequestQueue<Customer> requestQueue = new RequestQueue<Customer>();
		// Get requests information
		PreparedStatement getRequests = db.prepareStatement("SELECT "
			+ "users.username FROM resourceRequests resReq JOIN users ON "
			+ "users.usersId = resReq.customerRef WHERE "
			+ "resReq.resourceRef = ? ORDER BY seq_resourceRequests");
		getRequests.setInt(1, res.getUniqueID());
		ResultSet rs = getRequests.executeQuery();
		while (rs.next()) {
			// get Customer
			Customer cust = (Customer) users.get(rs.getString("username"));
			// add customer to request queue
			requestQueue.enqueue(cust);
			// Add resource to customer's request collection
			cust.importRequest(res);
		}
		return requestQueue;
	}

	/**
	 * This method loads ratings and reviews from the database for a resource.
	 * 
	 * @author Sushil Kumar
	 * @param selectedResource The resource for which rating and reviews have
	 *                         to be loaded
	 * @return the collection of reviews and ratings for the given resource.
	 * @throws SQLException if SQL errors
	 */
	private static ArrayList<ReviewAndRating>
		loadReviewsAndRatings(Resource selectedResource) throws SQLException {

		ArrayList<ReviewAndRating> reviewsAndRatings =
			new ArrayList<ReviewAndRating>();
		PreparedStatement readReviewAndRating =
			db.prepareStatement("SELECT resourceRef, customerRef, rating, "
				+ "review FROM ratings WHERE resourceRef = ?");
		readReviewAndRating.setInt(1, selectedResource.getUniqueID());
		ResultSet rs = readReviewAndRating.executeQuery();

		while (rs.next()) {

			float ratingNumber = rs.getFloat("rating");
			String reviewString = rs.getString("review");
			int customerRefID = rs.getInt("customerRef");
			// reference to customer having customerRefID
			User reviewAndRatingCustomer = null;
			// search through all users to find which user has customerRefID
			for (User user : users.values()) {
				if (!user.isLibrarian()) {
					if (user.getUniqueId() == customerRefID) {
						reviewAndRatingCustomer = user;
					}
				}
			}

			ReviewAndRating reviewAndRating =
				new ReviewAndRating(reviewString, ratingNumber,
					(Customer) reviewAndRatingCustomer, selectedResource);
			reviewsAndRatings.add(reviewAndRating);
		}

		return reviewsAndRatings;
	}

	/**
	 * Runs through each resource and loads its copies and requests.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadCopiesAndRequests() throws SQLException {
		// Loop through each resource
		for (Resource res : resources.values()) {
			// Set the copies and requests for those resources.
			res.importCopies(loadCopies(res));
			res.importRequests(loadResourceRequests(res));
		}
	}

	/**
	 * This method loads reviews and ratings for all resources
	 * 
	 * @author Sushil Kumar
	 * @throws SQLException if SQL errors.
	 */
	private static void loadReviewsAndRatingsForAllResources()
		throws SQLException {
		// Loop through each resource
		for (Resource res : resources.values()) {
			// Set the rating and review for those resources.
			res.setReviewsAndRatings(loadReviewsAndRatings(res));
		}
	}

	/**
	 * Gets all copies of a particular resource. Additionally, updates the
	 * customer who has the copy borrowed or reserved. Additionally, updates
	 * fineHistory of all customers who have ever been fined from loaning this
	 * copy.
	 * 
	 * @param res the resource to get the copies of.
	 * @return The collection of copies in that resource.
	 * @throws SQLException if SQL errors.
	 */
	private static ArrayList<Copy> loadCopies(Resource res)
		throws SQLException {
		// Get copy information
		ArrayList<Copy> copies = new ArrayList<Copy>();
		PreparedStatement getCopies = db.prepareStatement("SELECT copies.*,"
			+ " borrow.username AS borrower, reserve.username AS reserver"
			+ " FROM copies LEFT JOIN users borrow ON borrow.usersId = "
			+ "copies.custRef LEFT JOIN users reserve ON reserve.usersId ="
			+ " copies.reservedBy WHERE copies.resourceRef = ?");
		getCopies.setInt(1, res.getUniqueID());
		ResultSet rs = getCopies.executeQuery();
		while (rs.next()) {
			// Get copy information
			int copyId = rs.getInt("copiesId");
			int loanDuration = rs.getInt("loanDuration");
			Date dueDate = rs.getDate("dueDate");
			Customer borrower = (Customer) users.get(rs.getString("borrower"));
			Customer reserver = (Customer) users.get(rs.getString("reserver"));
			Date loanDate = rs.getTimestamp("loanDate");
			// Get transactionLoans for this copy
			ArrayList<TransactionLoan> transactionLoans =
				loadTransactionLoans(copyId);
			// Add copy to arraylist
			Copy thisCopy = new Copy(copyId, res, loanDuration, dueDate,
				borrower, reserver, loanDate, transactionLoans);
			copies.add(thisCopy);
			/*
			 * Update the borrower/reserver's borrowing/reserving lists, if
			 * they exist
			 */
			if (borrower != null) {
				borrower.importLoan(thisCopy);
			} else if (reserver != null) {
				reserver.importReserve(thisCopy);
			}
			// Create customer's fine transactions involving this copy
			loadTransactionFines(thisCopy);
		}
		return copies;
	}

	/**
	 * Gets the transaction history of loans of the given copy.
	 * 
	 * @param copyId the Id of the copy to get the history of.
	 * @return the transaction history of the copy.
	 * @throws SQLException if SQL errors.
	 */
	private static ArrayList<TransactionLoan> loadTransactionLoans(int copyId)
		throws SQLException {
		// Get transactionLoan information
		ArrayList<TransactionLoan> transactionLoans =
			new ArrayList<TransactionLoan>();
		PreparedStatement getTransactionLoans = db.prepareStatement("SELECT "
			+ "tl.transactionDate, tl.returnDate, users.username,"
			+ "tl.transactionLoansId FROM "
			+ "TransactionLoans tl JOIN users ON users.usersId = "
			+ "tl.custRef WHERE CopyRef = ? ORDER BY " + "tl.transactionDate");
		getTransactionLoans.setInt(1, copyId);
		ResultSet rs = getTransactionLoans.executeQuery();
		while (rs.next()) {
			// Read transactionLoan information
			int id = rs.getInt("transactionLoansId");
			Date transactionDate = rs.getDate("transactionDate");
			Date returnDate = rs.getDate("returnDate");
			Customer borrower = (Customer) users.get(rs.getString("username"));
			// Add transaction to collection
			transactionLoans.add(new TransactionLoan(id, borrower,
				transactionDate, returnDate));
		}
		return transactionLoans;
	}

	/**
	 * Loads the transaction fines which were accrued from loaning the
	 * specified copy, and adds them to the offending Customer.
	 * 
	 * @param copyRef The copy to load fines for.
	 * @throws SQLException if SQL errors.
	 */
	private static void loadTransactionFines(Copy copyRef)
		throws SQLException {
		// Get transactionFine information
		PreparedStatement getTransactionFines = db.prepareStatement("SELECT "
			+ "tf.*, users.username FROM transactionFines tf JOIN users "
			+ "ON users.usersId = tf.custRef WHERE tf.copyRef = ?");
		getTransactionFines.setInt(1, copyRef.getUniqueId());
		ResultSet rs = getTransactionFines.executeQuery();
		while (rs.next()) {
			// read TransactionFine information
			int id = rs.getInt("transactionfinesId");
			Date transactionDate = rs.getDate("transactionDate");
			int amount = rs.getInt("amount");
			int daysOverdue = rs.getInt("daysOverdue");
			Customer custRef = (Customer) users.get(rs.getString("username"));
			// Write transactionFine to customer
			custRef.importTransaction(new TransactionFine(id, amount, copyRef,
				daysOverdue, transactionDate));
		}
	}

	/**
	 * Updates the users now that resources are instantiated. Currently, this
	 * merely sorts the customer's transaction history.
	 */
	private static void updateUsersPostResources() {
		// Loop through each user
		for (User u : users.values()) {
			// filter to librarians
			if (u.isLibrarian()) {
				// no librarian updates are currently needed
				// Librarian lib = (Librarian) u;
			}
			// Filter to customers
			else {
				// Sort transaction history
				Customer cust = (Customer) u;
				cust.sortTransactionHistory();
			}
		}
	}

	/**
	 * Tries to log a user into the system.
	 * 
	 * @param username The username the user has entered.
	 * @return the user object, if the username is valid.
	 * @throws IllegalArgumentException if the user is not in the system.
	 */
	public static User logIn(String username) throws IllegalArgumentException {
		User loginUser = users.get(username);
		// Login fail
		if (loginUser == null) {
			throw new IllegalArgumentException("Invalid username entered!");
		} else {
			// Login success
			Datastore.currentUser = loginUser;
			return loginUser;
		}
	}

	/**
	 * Loads the default avatars into the static User collection.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	private static void loadUserAvatars() throws SQLException {
		// Get default avatar information
		PreparedStatement getDefaultAvatars =
			Datastore.db.prepareStatement("SELECT " + "* FROM defaultAvatars");
		ResultSet rs = getDefaultAvatars.executeQuery();
		while (rs.next()) {
			// Read avatar information
			String avatarName = rs.getString("avatarName");
			String avatarPath = rs.getString("avatarPath");
			// Update User class
			User.addDefaultAvatar(avatarName, avatarPath);
		}
	}

	/**
	 * Commits a resource request to the database.
	 * 
	 * @param res  the resource being requested.
	 * @param cust the customer requesting the resource.
	 * @throws SQLException if SQL errors.
	 */
	public static void insertResourceRequest(Resource res, Customer cust)
		throws SQLException {
		// insert record into the table
		PreparedStatement updateResourceRequest =
			Datastore.db.prepareStatement("INSERT INTO resourceRequests "
				+ "SELECT ? AS res, ? AS cust, t1.nextSeq FROM ("
				+ "SELECT (IFNULL(MAX(seq_resourceRequests),0) + 1) "
				+ "AS nextSeq FROM resourcerequests WHERE resourceRef "
				+ "= ?) t1");
		updateResourceRequest.setInt(1, res.getUniqueID());
		updateResourceRequest.setInt(2, cust.getUniqueId());
		updateResourceRequest.setInt(3, res.getUniqueID());
		// Insert new row
		updateResourceRequest.executeUpdate();
	}

	/**
	 * Creates a new librarian, commits them to the database, and adds them to
	 * the users collection.
	 * 
	 * @param userName         The username of the user
	 * @param firstName        The first name of the user
	 * @param surname          The surname of the user
	 * @param mobileNumber     The mobile phone number of the user
	 * @param address          The address of the user of the system
	 * @param profileImagePath The path to the profile image the user
	 * @param staffNumber      the librarian's staff number.
	 * @param employmentDate   The date this librarian was employed.
	 * @return the librarian created.
	 * @throws SQLException if SQL errors.
	 */
	public static Librarian createLibrarian(String userName, String firstName,
		String surname, String mobileNumber, String address,
		String profileImagePath, int staffNumber, LocalDate employmentDate)
		throws SQLException {
		// Generate query
		PreparedStatement createLibrarian = Datastore.db.prepareStatement(
			"INSERT INTO users (userName, firstName, surname,"
				+ " mobileNumber, address, profileImagePath) "
				+ " VALUES (?, ?, ?, ?, ?, ?); INSERT INTO librarians"
				+ " (usersId, staffNumber, employmentDate) VALUES "
				+ "(LAST_INSERT_ID(), ?, ?);");
		// Set parameters
		createLibrarian.setString(1, userName);
		createLibrarian.setString(2, firstName);
		createLibrarian.setString(3, surname);
		createLibrarian.setString(4, mobileNumber);
		createLibrarian.setString(5, address);
		createLibrarian.setString(6, profileImagePath);
		createLibrarian.setInt(7, staffNumber);
		// Convert localDate to SQL Date
		createLibrarian.setDate(8, java.sql.Date.valueOf(employmentDate));
		// Execute
		createLibrarian.executeUpdate();
		// Get generated ID value
		int id = Datastore.getLatestUniqueId("Librarians", "usersId");
		Librarian out = new Librarian(id, userName, firstName, surname,
			mobileNumber, address, profileImagePath, staffNumber,
			java.sql.Date.valueOf(employmentDate));
		Datastore.users.put(userName, out);
		return out;
	}

	/**
	 * Creates a new book and adds it to the database and the Datastore's
	 * resource collection.
	 * 
	 * @param title     the title of book.
	 * @param year      the year of book.
	 * @param author    the author of book.
	 * @param publisher the publisher of book.
	 * @param genre     the genre of book.
	 * @param isbn      the isbn of book.
	 * @param language  the language of book.
	 * @return the created book.
	 * @throws SQLException if SQL errors.
	 */
	public static Book createBook(String title, int year, String author,
		String publisher, String genre, String isbn, String language)
		throws SQLException {
		// Generate query
		LocalDateTime additionDate = LocalDateTime.now();
		PreparedStatement createBook = Datastore.db.prepareStatement(
			"INSERT INTO resources (title, year,creationDate)"
				+ " VALUES (?, ?, ?); INSERT INTO Books (resourcesId, "
				+ "author,  publisher, genre, ISBN, language) VALUES "
				+ "(LAST_INSERT_ID(), ?, ?, ?, ?, ?);");
		// Set parameters
		createBook.setString(1, title);
		createBook.setInt(2, year);
		createBook.setString(4, author);
		createBook.setString(5, publisher);
		createBook.setString(6, genre);
		createBook.setString(7, isbn);
		createBook.setString(8, language);

		createBook.setTimestamp(3, Timestamp.valueOf(additionDate));

		// Execute
		createBook.executeUpdate();
		// Get generated ID value
		int id = Datastore.getLatestUniqueId("Books", "resourcesId");
		// Create object
		Book out = new Book(id, title, year, author, publisher, genre, isbn,
			language, additionDate);
		// Put into collection
		Datastore.resources.put(id, out);
		// Return
		return out;
	}

	/**
	 * Creates a new DVD and adds it to the database and the Datastore's
	 * resource collection.
	 * 
	 * @param title        the title of dvd.
	 * @param year         the year of dvd.
	 * @param director     the director of dvd.
	 * @param runtime      the runtime of dvd.
	 * @param language     the language of dvd.
	 * @param subtitleList the subtitle of dvd.
	 * @return the created DVD.
	 * @throws SQLException if SQL errors.
	 */
	public static DVD createDVD(String title, int year, String director,
		int runtime, String language, ArrayList<String> subtitleList)
		throws SQLException {

		LocalDateTime additionDate = LocalDateTime.now();
		// Generate query
		PreparedStatement createDVD = Datastore.db.prepareStatement(
			"INSERT INTO resources (title, year, creationDate)"
				+ " VALUES (?, ?, ?); INSERT INTO DVDs (resourcesId, director,"
				+ " runtime, language) VALUES (LAST_INSERT_ID(), ?, ?, ?);");
		// Set parameters
		createDVD.setString(1, title);
		createDVD.setInt(2, year);
		createDVD.setString(4, director);
		createDVD.setInt(5, runtime);
		createDVD.setString(6, language);

		createDVD.setTimestamp(3, Timestamp.valueOf(additionDate));
		// Execute
		createDVD.executeUpdate();
		// Get generated ID value
		int id = Datastore.getLatestUniqueId("DVDs", "resourcesId");
		// add subtitles for this resource
		Datastore.insertDVDSubtitles(id, subtitleList);
		// Create object
		DVD out = new DVD(id, title, year, director, runtime, language,
			subtitleList, additionDate);
		// Put into collection
		Datastore.resources.put(id, out);
		// Return
		return out;
	}

	/**
	 * Adds the given arraylist of subtitle languages into the database for the
	 * given resource's ID.
	 * 
	 * @param resourceId the ID of the resource which the subtitles belong to.
	 * @param subs       the arraylist of subtitles.
	 * @throws SQLException if SQL errors.
	 */
	private static void insertDVDSubtitles(int resourceId,
		ArrayList<String> subs) throws SQLException {
		// Generate query
		PreparedStatement insertSubs = Datastore.db
			.prepareStatement("INSERT " + "INTO dvdSubtitles VALUES (?, ?)");
		// Add each subtitle language as a batch
		for (String language : subs) {
			insertSubs.setInt(1, resourceId);
			insertSubs.setString(2, language);
			insertSubs.addBatch();
		}
		insertSubs.executeBatch();
	}

	/**
	 * Creates a new laptopcomputer and adds it to the database and the
	 * datastore's resources collection.
	 * 
	 * @param title        the title of laptop and computer.
	 * @param year         the year of laptop and computer.
	 * @param manufacturer the manfacturer of laptop and computer.
	 * @param model        the model of laptop and computer.
	 * @param OS           the OS of laptop and computer.
	 * @return the new laptopComputer.
	 * @throws SQLException if SQL errors.
	 */
	public static LaptopComputer createLaptopComputer(String title, int year,
		String manufacturer, String model, String OS) throws SQLException {

		LocalDateTime additionDate = LocalDateTime.now();
		// Generate query
		PreparedStatement createLaptop = Datastore.db.prepareStatement(
			"INSERT INTO resources (title, year,creationDate)"
				+ " VALUES (?, ?, ?); INSERT INTO LaptopComputers (resourcesId, "
				+ "manufacturer,  model, OS) VALUES "
				+ "(LAST_INSERT_ID(), ?, ?, ?);");
		// Set parameters
		createLaptop.setString(1, title);
		createLaptop.setInt(2, year);
		createLaptop.setString(4, manufacturer);
		createLaptop.setString(5, model);
		createLaptop.setString(6, OS);

		createLaptop.setTimestamp(3, Timestamp.valueOf(additionDate));
		// Execute
		createLaptop.executeUpdate();
		// Get generated ID value
		int id = Datastore.getLatestUniqueId("LaptopComputers", "resourcesId");
		// Create object
		LaptopComputer out = new LaptopComputer(id, title, year, manufacturer,
			model, OS, additionDate);
		// Put into collection
		Datastore.resources.put(id, out);
		// Return
		return out;
	}

	/**
	 * Creates a new VideoGame and adds it to the database and resource
	 * collection.
	 * 
	 * @param title              The title of the VideoGame.
	 * @param year               The year the VideoGame was published.
	 * @param publisher          The publisher of the VideoGame.
	 * @param genre              The genre of the VideoGame.
	 * @param certificateRating  The certificate Rating of the VideoGame.
	 * @param multiplayerSupport Whether the VideoGame has multiplayer support
	 *                           (true if so).
	 * @return The VideGame that has been created.
	 * @throws SQLException If SQL errors.
	 */
	public static VideoGame createVideoGame(String title, int year,
		String publisher, String genre, String certificateRating,
		boolean multiplayerSupport) throws SQLException {

		LocalDateTime additionDate = LocalDateTime.now();
		// Generate query
		PreparedStatement createVideoGame = Datastore.db.prepareStatement(
			"INSERT INTO resources (title, year, creationDate)"
				+ " VALUES (?, ?, ?); INSERT INTO videogames (resourcesId, "
				+ "Publisher,  Genre, CertificateRating, MultiplayerSupport) VALUES "
				+ "(LAST_INSERT_ID(), ?, ?, ?, ?);");
		// Set parameters
		createVideoGame.setString(1, title);
		createVideoGame.setInt(2, year);
		createVideoGame.setTimestamp(3, Timestamp.valueOf(additionDate));
		createVideoGame.setString(4, publisher);
		createVideoGame.setString(5, genre);
		createVideoGame.setString(6, certificateRating);
		createVideoGame.setBoolean(7, multiplayerSupport);

		// Execute
		createVideoGame.executeUpdate();
		// Get generated ID value
		int id = Datastore.getLatestUniqueId("videogames", "resourcesId");
		// Create object
		VideoGame out = new VideoGame(id, title, year, publisher, genre,
			certificateRating, multiplayerSupport, additionDate);
		// Put into collection
		Datastore.resources.put(id, out);
		// Return
		return out;

	}

	/**
	 * Adds a new event to the database and the hashmap
	 * 
	 * @param name         the event name
	 * @param date         the event date
	 * @param description  the event description
	 * @param maxAttendees the number of max attendees
	 * @return the newly created event object
	 * @throws SQLException if SQL errors.
	 * @author Mike
	 */
	public static Event createEvent(String name, Timestamp date,
		String description, int maxAttendees) throws SQLException {
		// Create query
		PreparedStatement createEvent =
			Datastore.db.prepareStatement("INSERT INTO events"
				+ " (title, eventDate, maxAttendees, description)"
				+ " VALUES (?, ?, ? , ?);");
		// Insert values
		createEvent.setString(1, name);
		createEvent.setTimestamp(2, date);
		createEvent.setInt(3, maxAttendees);
		createEvent.setString(4, description);

		createEvent.executeUpdate();

		int id = Datastore.getLatestUniqueId("events", "eventsId");

		Event eventOut = new Event(id, date, name, maxAttendees, description);

		Datastore.events.put(id, eventOut);

		return eventOut;
	}

	/**
	 * Removes an event and all the bookings on the event from the database
	 * 
	 * @param eventId the event to cancel
	 * @throws SQLException if SQL errors.
	 * @author Mike
	 */
	public static void cancelEvent(int eventId) throws SQLException {
		// Create query to delete bookings
		PreparedStatement deleteBookings = Datastore.db
			.prepareStatement("DELETE FROM eventbookings WHERE eventsId = ?");
		deleteBookings.setInt(1, eventId);
		deleteBookings.executeUpdate();

		// Create query to delete event
		PreparedStatement deleteEvent = Datastore.db
			.prepareStatement("DELETE FROM events WHERE eventsId = ?");
		deleteEvent.setInt(1, eventId);
		deleteEvent.executeUpdate();

		// Remove event from hashmap
		events.remove(eventId);

		// Reload all customer event bookings
		for (Entry<String, User> entry : users.entrySet()) {
			User u = entry.getValue();
			if (!u.isLibrarian()) {
				Customer c = (Customer) u;
				ArrayList<ArrayList<Event>> eventsArrayLists =
					Datastore.getEventBookings(u.getUniqueId());
				c.setUpcommingEvents(eventsArrayLists.get(1));
			}
		}

	}

	/**
	 * Commits a new copy of the given resource to the database.
	 * 
	 * @param resId the unique ID of the resource the copy is of.
	 * @return the id of the copy.
	 * @throws SQLException if SQL errors.
	 */
	public static int insertCopy(int resId) throws SQLException {
		// Create the new copy in the database
		PreparedStatement insertCopy = Datastore.db
			.prepareStatement("INSERT INTO copies (resourceRef) VALUES (?);");
		insertCopy.setInt(1, resId);
		insertCopy.executeUpdate();
		// Get the ID just generated
		return getLatestUniqueId("copies", "copiesId");
	}

	/**
	 * Gets the latest auto-increment field generated by the database. This is
	 * extremely vulnerable to SQL injection, but since it doesn't use user
	 * input, that hopefully doesn't matter.
	 * 
	 * @param tableName   the name of the table to get the id from.
	 * @param idFieldName the name of the field containing the id.
	 * @return the biggest value in that field, or 0 if it's empty.
	 * @throws SQLException if SQL errors.
	 */
	public static int getLatestUniqueId(String tableName, String idFieldName)
		throws SQLException {
		/*
		 * Get the max id field value from the table - this should be the most
		 * recently inserted one
		 */
		PreparedStatement getUniqueId = Datastore.db.prepareStatement("SELECT "
			+ "IFNULL(MAX(" + idFieldName + "),0) AS id FROM " + tableName);
		ResultSet rs = getUniqueId.executeQuery();
		// Get the first row
		rs.next();
		int id = rs.getInt("id");
		rs.close();
		return id;
	}

	/**
	 * Deletes the given copy from the database.
	 * 
	 * @param copyRef The copy to be deleted.
	 * @throws SQLException if SQL errors.
	 */
	public static void deleteCopy(Copy copyRef) throws SQLException {
		// Delete the copy from the database
		PreparedStatement deleteCopy = Datastore.db.prepareStatement(
			"DELETE " + "FROM copies WHERE copies.copiesId = ?");
		deleteCopy.setInt(1, copyRef.getUniqueId());
		// Delete it
		deleteCopy.executeUpdate();
	}

	/**
	 * Updates the atrributes of the user to the ones given.
	 * 
	 * @param userId       the id of the user being edited.
	 * @param username     the new username of the user.
	 * @param firstName    the new firstname of the user.
	 * @param surname      the new surname of the user.
	 * @param mobileNumber the new mobileNumber of the user.
	 * @param address      the new address of the user.
	 * @throws SQLException if SQL errors.
	 */
	public static void editUser(int userId, String username, String firstName,
		String surname, String mobileNumber, String address)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateUser = db.prepareStatement(
			"UPDATE users SET username = ?, firstName = ?, surname = ?,"
				+ "mobileNumber = ?, address = ? WHERE usersId = ?;");
		// Set parameters
		updateUser.setString(1, username);
		updateUser.setString(2, firstName);
		updateUser.setString(3, surname);
		updateUser.setString(4, mobileNumber);
		updateUser.setString(5, address);
		updateUser.setInt(6, userId);
		// Update user
		updateUser.executeUpdate();
	}

	/**
	 * Adds a new transaction payment to the database.
	 * 
	 * @param customerId      the customer making the payment.
	 * @param paymentAmount   the amount paid.
	 * @param transactionDate the date the transaction took place.
	 * @return the unique ID of the generated record.
	 * @throws SQLException if SQL errors.
	 */
	public static int insertTransactionPayment(int customerId,
		int paymentAmount, Date transactionDate) throws SQLException {
		// Prepare statement
		PreparedStatement insertTP = db.prepareStatement(
			"INSERT INTO transactionPayments (transactionDate,custRef,amount)"
				+ "VALUES (?, ?, ?);");
		// Set parameters
		insertTP.setTimestamp(1,
			new java.sql.Timestamp(transactionDate.getTime()));
		insertTP.setInt(2, customerId);
		insertTP.setInt(3, paymentAmount);
		// Insert
		insertTP.executeUpdate();
		// Get auto-generated id
		return Datastore.getLatestUniqueId("transactionPayments",
			"transactionPaymentsId");
	}

	/**
	 * Updates a customer's fine value.
	 * 
	 * @param customerId the id of the customer being updated.
	 * @param balance    the customer's new balance.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateCustomerFine(int customerId, int balance)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateFine = db.prepareStatement(
			"UPDATE customers SET balance = ? WHERE usersId = ?;");
		// Set parameters
		updateFine.setInt(1, balance);
		updateFine.setInt(2, customerId);
		// Update
		updateFine.executeUpdate();
	}

	/**
	 * Inserts the given transaction fine into the database.
	 * 
	 * @param customerId     the customer being fined.
	 * @param fineAmount     the amount of the fine.
	 * @param borrowedCopyId the id of the copy borrowed.
	 * @param daysOverdue    the days the copy was overdue.
	 * @param now            the date when the transaction took place.
	 * @return the id of the transaction fine.
	 * @throws SQLException if SQL errors.
	 */
	public static int insertTransactionFine(int customerId, int fineAmount,
		int borrowedCopyId, int daysOverdue, Date now) throws SQLException {
		// Prepare statement
		PreparedStatement insertTF = db.prepareStatement(
			"INSERT INTO transactionFines (transactionDate, amount, copyRef,"
				+ " daysOverdue, custRef) VALUES (?, ?, ?, ?, ?);");
		// Set parameters
		insertTF.setTimestamp(1, new java.sql.Timestamp(now.getTime()));
		insertTF.setInt(2, fineAmount);
		insertTF.setInt(3, borrowedCopyId);
		insertTF.setInt(4, daysOverdue);
		insertTF.setInt(5, customerId);
		// Execute update
		insertTF.executeUpdate();
		return Datastore.getLatestUniqueId("transactionFines",
			"transactionFinesId");
	}

	/**
	 * This methods inserts a review and rating into the database as a record.
	 * 
	 * @author Sushil Kumar
	 * @param ratingNumber  The rating given by customer as floating point
	 *                      number.
	 * @param reviewString  The review in text written by the user.
	 * @param resourceRefID Unique id of the customer currently logged in.
	 * @param customerRefID Unique id of the resource for which rating/review
	 *                      is given.
	 * @throws SQLException if SQL errors
	 */
	public static void insertReviewAndRating(int resourceRefID,
		int customerRefID, float ratingNumber, String reviewString)
		throws SQLException {

		PreparedStatement insertRAR = db.prepareStatement(
			"INSERT INTO Ratings (resourceRef, customerRef, rating, review)"
				+ " VALUES (?, ?, ?, ?);");
		insertRAR.setInt(1, resourceRefID);
		insertRAR.setInt(2, customerRefID);
		insertRAR.setFloat(3, ratingNumber);
		insertRAR.setString(4, reviewString);

		insertRAR.executeUpdate();
	}

	/**
	 * Updates the copy specified to be loaned by the given user.
	 * 
	 * @param copyId       the id of the copy being loaned.
	 * @param userId       the id of the user loaning.
	 * @param loanDuration the duration of the loan.
	 * @param loanDate     the date the copy was loaned out.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateCopyLoan(int copyId, int userId, int loanDuration,
		java.util.Date loanDate) throws SQLException {
		// Prepare statement
		PreparedStatement updateLoan = db.prepareStatement(
			"UPDATE copies SET reservedBy = NULL, loanDuration = ?, "
				+ "custRef = ?, loanDate = ? WHERE copiesId = ?;");
		// Set parameters
		updateLoan.setInt(1, loanDuration);
		updateLoan.setInt(2, userId);
		updateLoan.setTimestamp(3, new Timestamp(loanDate.getTime()));
		updateLoan.setInt(4, copyId);
		// Update
		updateLoan.executeUpdate();
	}

	/**
	 * Clears all loan data from the copy in the database.
	 * 
	 * @param copyId the id of the copy to update.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateCopyRemoveLoan(int copyId) throws SQLException {
		// Prepare statement
		PreparedStatement removeLoan = db.prepareStatement(
			"UPDATE copies SET loanDuration = NULL, dueDate = NULL, "
				+ "custRef = NULL, loanDate = NULL WHERE copiesId = ?;");
		// Set parameters
		removeLoan.setInt(1, copyId);
		// remove loan
		removeLoan.executeUpdate();
	}

	/**
	 * Deletes the given resource request.
	 * 
	 * @param resourceId the id of the resource in the request.
	 * @param userId     the id of the user in the request.
	 * @throws SQLException if SQL errors.
	 */
	public static void deleteResourceRequest(int resourceId, int userId)
		throws SQLException {
		// Prepare statement
		PreparedStatement deleteRR = db.prepareStatement(
			"DELETE FROM resourceRequests WHERE resourceRef = ? AND"
				+ " customerRef = ?;");
		// Set parameters
		deleteRR.setInt(1, resourceId);
		deleteRR.setInt(2, userId);
		// delete record
		deleteRR.executeUpdate();
	}

	/**
	 * Updates the copy's reserved status.
	 * 
	 * @param copyId the id of the copy to update.
	 * @param userId the id of the user reserving the copy.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateCopyReserve(int copyId, Integer userId)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateReserve = db.prepareStatement(
			"UPDATE copies SET reservedBy = ? WHERE copiesId = ?;");
		// Set variables
		// Use setObject so it can be null
		updateReserve.setObject(1, userId, java.sql.Types.INTEGER);
		updateReserve.setInt(2, copyId);
		// Update
		updateReserve.executeUpdate();
	}

	/**
	 * Sets the reserved attribute of the specified copy to null. Currently
	 * unnecessary, as the loan method does this already.
	 * 
	 * @param copyId the id of the copy to update.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateCopyRemoveReserve(int copyId)
		throws SQLException {
		Datastore.updateCopyReserve(copyId, null);
	}

	public static void updateCopyDueDate(int copyId, Date dueDate)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateDueDate = db.prepareStatement(
			"UPDATE copies SET dueDate = ? WHERE copies.copiesId = ?;");
		// set variables
		// convert date to SQL date
		updateDueDate.setTimestamp(1,
			new java.sql.Timestamp(dueDate.getTime()));
		updateDueDate.setInt(2, copyId);
		// run update
		updateDueDate.executeUpdate();
	}

	/**
	 * Updates the return date of the given transaction loan.
	 * 
	 * @param transactionLoansId the id of the loan to update.
	 * @param returnDate         the date the loan was returned.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateTransactionLoanSetReturnDate(
		int transactionLoansId, Date returnDate) throws SQLException {
		// Prepare statement
		PreparedStatement updateTLRetDate =
			db.prepareStatement("UPDATE transactionLoans SET returnDate = ?"
				+ " WHERE transactionLoansId = ?");
		// Set variables
		// Convert to sql date
		updateTLRetDate.setTimestamp(1,
			new java.sql.Timestamp(returnDate.getTime()));
		updateTLRetDate.setInt(2, transactionLoansId);
		// Run update
		updateTLRetDate.executeUpdate();
	}

	/**
	 * Inserts a transaction loan into the database.
	 * 
	 * @param copyId    the id of the copy the loan belongs to.
	 * @param usersId   the id of the user loaning the item.
	 * @param transDate the date the transaction took place.
	 * @return the id of the new transaction loan record.
	 * @throws SQLException if SQL errors.
	 */
	public static int insertTransactionLoan(int copyId, int usersId,
		Date transDate) throws SQLException {
		// Prepare statement
		PreparedStatement insertTL =
			db.prepareStatement("INSERT INTO transactionLoans "
				+ "(transactionDate, copyRef, custRef) VALUES (?, ?, ?);");
		// Convert util date to sql date
		insertTL.setTimestamp(1, new java.sql.Timestamp(transDate.getTime()));
		insertTL.setInt(2, copyId);
		insertTL.setInt(3, usersId);
		// run insert
		insertTL.executeUpdate();
		// Return generated Unique Id
		return Datastore.getLatestUniqueId("transactionLoans",
			"transactionLoansId");

	}

	/**
	 * Updates data relating to a resource.
	 * 
	 * @param resourcesId the id of the resource to update.
	 * @param title       the new title of the resource.
	 * @param year        the new year of the resource.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateResource(int resourcesId, String title,
		String year) throws SQLException {
		// Prepare statement
		PreparedStatement updateRes = db.prepareStatement("UPDATE resources"
			+ " SET title = ?, year = ? WHERE resourcesId = ?;");
		updateRes.setString(1, title);
		updateRes.setString(2, year);
		updateRes.setInt(3, resourcesId);
		// Execute it
		updateRes.executeUpdate();
	}

	/**
	 * Updates a user's profile image.
	 * 
	 * @param usersId the user's id.
	 * @param path    the path to the image.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateUserProfileImage(int usersId, String path)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateUserImage = db.prepareStatement(
			"UPDATE users SET profileImagePath = ? WHERE usersId = ?");
		updateUserImage.setString(1, path);
		updateUserImage.setInt(2, usersId);
		// Execute it
		updateUserImage.executeUpdate();
	}

	/**
	 * Updates a book to have the given attributes.
	 * 
	 * @param resourcesId the ID of the book to update.
	 * @param author      the author of book.
	 * @param publisher   the publisher of book.
	 * @param genre       the genre of book.
	 * @param isbn        the isbn of book.
	 * @param language    the language of book.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateBook(int resourcesId, String author,
		String publisher, String genre, String isbn, String language)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateBook = db.prepareStatement(
			"UPDATE Books set author = ?, publisher = ?, genre = ?, isbn = ?,"
				+ " language = ? WHERE resourcesId = ?;");
		updateBook.setString(1, author);
		updateBook.setString(2, publisher);
		updateBook.setString(3, genre);
		updateBook.setString(4, isbn);
		updateBook.setString(5, language);
		updateBook.setInt(6, resourcesId);
		// Update
		updateBook.executeUpdate();
	}

	/**
	 * Updates a DVD to have the given attributes.
	 * 
	 * @param resourcesId  the ID of the DVD you want to update.
	 * @param director     the director of dvd.
	 * @param runtime      the runtime of dvd.
	 * @param language     the language of dvd.
	 * @param subtitleList the subtitle of dvd.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateDVD(int resourcesId, String director,
		String runtime, String language, ArrayList<String> subtitleList)
		throws SQLException {
		// Prepare statement
		PreparedStatement updateDVD = db.prepareStatement(
			"UPDATE DVDs SET director = ?, runtime = ?, language = ? WHERE"
				+ " resourcesId = ?;");
		updateDVD.setString(1, director);
		updateDVD.setString(2, runtime);
		updateDVD.setString(3, language);
		updateDVD.setInt(4, resourcesId);
		// Update
		updateDVD.executeUpdate();
		// Update subtitles
		updateDVDSubtitles(resourcesId, subtitleList);
	}

	/**
	 * Removes all subtitles for the given resource and then adds them back in.
	 * 
	 * @param resourcesId  the ID of the DVD.
	 * @param subtitleList the list of subtitles to update to.
	 * @throws SQLException if SQL errors.
	 */
	private static void updateDVDSubtitles(int resourcesId,
		ArrayList<String> subtitleList) throws SQLException {
		// Clear subs
		PreparedStatement deleteDVDSubs = db.prepareStatement(
			"DELETE FROM" + " DVDSubtitles WHERE resourcesId = ?;");
		// set parameters
		deleteDVDSubs.setInt(1, resourcesId);
		// delete
		deleteDVDSubs.executeUpdate();
		// Add them back in
		Datastore.insertDVDSubtitles(resourcesId, subtitleList);
	}

	/**
	 * Updates a laptop to have the given attributes.
	 * 
	 * @param resourcesId  the ID of the laptop to update.
	 * @param manufacturer the manfacturer of laptop and computer.
	 * @param model        the model of laptop and computer.
	 * @param OS           the OS of laptop and computer.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateLaptopComputer(int resourcesId,
		String manufacturer, String model, String OS) throws SQLException {
		// Prepare statement
		PreparedStatement updateLC =
			db.prepareStatement("UPDATE LaptopComputers SET"
				+ " manufacturer = ?, model = ?, OS = ? WHERE resourcesId = ?");
		updateLC.setString(1, manufacturer);
		updateLC.setString(2, model);
		updateLC.setString(3, OS);
		updateLC.setInt(4, resourcesId);
		updateLC.executeUpdate();
	}

	/**
	 * Updates the review and rating in the database.
	 * 
	 * @param ratingNumber  The rating given by customer as floating point
	 *                      number.
	 * @param reviewString  The review in text written by the user.
	 * @param resourceRefID Unique id of the customer currently logged in.
	 * @param customerRefID Unique id of the resource for which rating/review
	 *                      is given.
	 * @throws SQLException if SQL errors.
	 */
	public static void updateReviewAndRating(float ratingNumber,
		String reviewString, int resourceRefID, int customerRefID)
		throws SQLException {
		PreparedStatement updateRAR = db.prepareStatement(
			"UPDATE ratings SET" + " rating = ?, review = ? WHERE"
				+ " resourceRef = ? AND customerRef = ?");
		updateRAR.setFloat(1, ratingNumber);
		updateRAR.setString(2, reviewString);
		updateRAR.setInt(3, resourceRefID);
		updateRAR.setInt(4, customerRefID);
		updateRAR.executeUpdate();
	}

	/**
	 * Creates a new event booking
	 * 
	 * @param eventID the ID of the event
	 * @param userID  the username of the user booking onto the event
	 * @author Mike
	 * @throws SQLException if SQL errors.
	 */
	public static void createBooking(int eventID, int userID)
		throws SQLException, IndexOutOfBoundsException {
		Event e = events.get(eventID);
		Customer currentCustomer = (Customer) currentUser;
		if (e.getAttendees() == e.getMaxAttendees()) {
			throw new IndexOutOfBoundsException("Event at max attendance");
		}
		PreparedStatement createBooking = db.prepareStatement(
			"INSERT INTO eventbookings (eventsId, customerRef)"
				+ " VALUES (?, ?);");
		createBooking.setInt(1, eventID);
		createBooking.setInt(2, userID);
		createBooking.executeUpdate();
		e.addAttendee();
		currentCustomer.getUpcommingEvents().add(e);
	}

	/**
	 * Removes a single booking from the bookings table
	 * 
	 * @param eventID the unique ID of the event
	 * @param userID  the unique ID of the user that has the booking
	 * @author Mike
	 * @throws SQLException if SQL errors.
	 */
	public static void removeBooking(int eventID, int userID)
		throws SQLException {
		Event e = events.get(eventID);
		Customer currentCustomer = (Customer) currentUser;
		PreparedStatement removeBooking =
			db.prepareStatement("DELETE FROM eventbookings"
				+ " WHERE eventsId = ? AND customerRef = ?");
		removeBooking.setInt(1, eventID);
		removeBooking.setInt(2, userID);
		removeBooking.executeUpdate();
		e.removeAttendee();
		currentCustomer.getUpcommingEvents().remove(e);
	}

	/**
	 * Appends % wildcards onto each side of a string.
	 * 
	 * @param in the string to add the wildcards to.
	 * @return the input with wildcards on each side.
	 */
	private static String wildcards(String in) {
		return "%" + in + "%";
	}

	/**
	 * Search the users table based on given parameters.
	 * 
	 * @param username     The username of the user of the system.
	 * @param firstName    The first name of the user of the system.
	 * @param surname      The surname of the user of the system.
	 * @param mobileNumber The mobile phone number of the user of the system.
	 * @param isLibrarian  if the user being searched for is a librarian or
	 *                     not.
	 * @return an arraylist of users matching criteria.
	 * @throws SQLException if SQL errors.
	 */
	public static ArrayList<User> searchUsers(String username,
		String firstName, String surname, String mobileNumber,
		boolean isLibrarian) throws SQLException {
		// Check librarian or not
		String tableName;
		if (isLibrarian) {
			tableName = "librarians";
		} else {
			tableName = "customers";
		}
		// Search users
		PreparedStatement searchUsers =
			db.prepareStatement("SELECT u.username "
				+ "FROM users u WHERE u.usersId IN (SELECT usersId FROM "
				+ tableName + ") AND u.username LIKE ? AND u.firstName LIKE ?"
				+ " AND u.surname LIKE ? AND u.mobileNumber LIKE ?");
		searchUsers.setString(1, wildcards(username));
		searchUsers.setString(2, wildcards(firstName));
		searchUsers.setString(3, wildcards(surname));
		searchUsers.setString(4, wildcards(mobileNumber));
		// Run query
		ResultSet rs = searchUsers.executeQuery();
		ArrayList<User> results = new ArrayList<User>();
		while (rs.next()) {
			results.add(Datastore.users.get(rs.getString("username")));
		}
		return results;

	}

	/**
	 * Searches for a dvd based on given criteria.
	 * 
	 * @param title            the title of dvd.
	 * @param year             the year of dvd.
	 * @param director         the director of dvd.
	 * @param runtime          the runtime of dvd.
	 * @param language         the language of dvd.
	 * @param subtitleLanguage the langauge of the subtitles of the dvd.
	 * @return an arraylist of DVDs which match the search criteria.
	 * @throws SQLException if SQL errors.
	 */
	public static ArrayList<DVD> searchDVDs(String title, String year,
		String director, String runtime, String language,
		String subtitleLanguage) throws SQLException {
		// Compare DVD information
		PreparedStatement searchDVDs = db.prepareStatement("SELECT DISTINCT"
			+ " r.resourcesId FROM resources r JOIN dvds on"
			+ " r.resourcesId = dvds.resourcesId LEFT JOIN DVDSubtitles subs"
			+ " ON subs.resourcesId = dvds.resourcesId WHERE"
			+ " r.title LIKE ? AND r.year LIKE ? AND dvds.director LIKE"
			+ " ? AND dvds.runtime LIKE ? AND dvds.language LIKE ? AND"
			+ " (subs.subtitleLanguage LIKE ? OR "
			+ "subs.subtitleLanguage IS NULL)");
		// Query with wildcards appended
		searchDVDs.setString(1, wildcards(title));
		searchDVDs.setString(2, wildcards(year));
		searchDVDs.setString(3, wildcards(director));
		searchDVDs.setString(4, wildcards(runtime));
		searchDVDs.setString(5, wildcards(language));
		searchDVDs.setString(6, wildcards(subtitleLanguage));
		// Run query
		ResultSet rs = searchDVDs.executeQuery();
		ArrayList<DVD> results = new ArrayList<DVD>();
		while (rs.next()) {
			results
				.add((DVD) Datastore.resources.get(rs.getInt("resourcesId")));
		}
		return results;
	}

	/**
	 * Searches for a book based on given criteria.
	 * 
	 * @param title     the title of book.
	 * @param year      the year of book.
	 * @param author    the author of book.
	 * @param publisher the publisher of book.
	 * @param genre     the genre of book.
	 * @param isbn      the isbn of book.
	 * @param language  the language of book.
	 * @return An arraylist of books found by the criteria given.
	 * @throws SQLException if SQL errors.
	 */
	public static ArrayList<Book> searchBooks(String title, String year,
		String author, String publisher, String genre, String isbn,
		String language) throws SQLException {
		// Compare Book information
		PreparedStatement searchBooks = db.prepareStatement(
			"SELECT" + " r.resourcesId FROM resources r JOIN books b on"
				+ " r.resourcesId = b.resourcesId WHERE"
				+ " r.title LIKE ? AND r.year LIKE ? AND b.author LIKE"
				+ " ? AND b.publisher LIKE ? AND b.genre LIKE ? AND "
				+ "b.isbn LIKE ? AND b.language LIKE ?");
		// Query with wildcards appended
		searchBooks.setString(1, wildcards(title));
		searchBooks.setString(2, wildcards(year));
		searchBooks.setString(3, wildcards(author));
		searchBooks.setString(4, wildcards(publisher));
		searchBooks.setString(5, wildcards(genre));
		searchBooks.setString(6, wildcards(isbn));
		searchBooks.setString(7, wildcards(language));
		// Run query
		ResultSet rs = searchBooks.executeQuery();
		ArrayList<Book> results = new ArrayList<Book>();
		while (rs.next()) {
			results
				.add((Book) Datastore.resources.get(rs.getInt("resourcesId")));
		}
		return results;
	}

	/**
	 * Searches for laptop computers.
	 * 
	 * @param title        the title of laptop and computer.
	 * @param year         the year of laptop and computer.
	 * @param manufacturer the manfacturer of laptop and computer.
	 * @param model        the model of laptop and computer.
	 * @param OS           the OS of laptop and computer.
	 * @return the resources found in the search.
	 * @throws SQLException if SQL errors.
	 */
	public static ArrayList<LaptopComputer> searchLaptopComputer(String title,
		String year, String manufacturer, String model, String OS)
		throws SQLException {
		// Compare LaptopComputer information
		PreparedStatement searchLaptopComputers = db.prepareStatement("SELECT"
			+ " r.resourcesId FROM resources r JOIN laptopComputers lc on"
			+ " r.resourcesId = lc.resourcesId WHERE"
			+ " r.title LIKE ? AND r.year LIKE ? AND lc.manufacturer LIKE"
			+ " ? AND lc.model LIKE ? AND lc.OS LIKE ?");
		// Query with wildcards appended
		searchLaptopComputers.setString(1, wildcards(title));
		searchLaptopComputers.setString(2, wildcards(year));
		searchLaptopComputers.setString(3, wildcards(manufacturer));
		searchLaptopComputers.setString(4, wildcards(model));
		searchLaptopComputers.setString(5, wildcards(OS));
		// Run query
		ResultSet rs = searchLaptopComputers.executeQuery();
		ArrayList<LaptopComputer> results = new ArrayList<LaptopComputer>();
		while (rs.next()) {
			results.add((LaptopComputer) Datastore.resources
				.get(rs.getInt("resourcesId")));
		}
		return results;
	}

	/**
	 * Searches for VideoGames
	 * 
	 * @param title              Title of the VideoGame.
	 * @param year               Year the VideoGame was published.
	 * @param publisher          Publisher of the VideoGame.
	 * @param genre              Genre of the VideoGame.
	 * @param certificateRating  Certificate rating of the VideoGame.
	 * @param multiplayerSupport Whether the VideoGame has multiplayer support
	 *                           (1 if so).
	 * @return the collection of video games which match the given criteria.
	 * @throws SQLException if SQL errors.
	 */
	public static ArrayList<VideoGame> searchVideoGame(String title,
		String year, String publisher, String genre, String certificateRating,
		int multiplayerSupport) throws SQLException {
		// Compare VideoGame information
		PreparedStatement searchVideoGames = db.prepareStatement(
			"SELECT" + " r.resourcesId FROM resources r JOIN videogames vg on"
				+ " r.resourcesId = vg.resourcesId WHERE"
				+ " r.title LIKE ? AND r.year LIKE ? AND vg.Publisher LIKE"
				+ " ? AND vg.Genre LIKE ? AND vg.CertificateRating LIKE ?"
				+ " AND ? IN (-1,vg.MultiplayerSupport)");
		// Query with wildcards appended
		searchVideoGames.setString(1, wildcards(title));
		searchVideoGames.setString(2, wildcards(year));
		searchVideoGames.setString(3, wildcards(publisher));
		searchVideoGames.setString(4, wildcards(genre));
		searchVideoGames.setString(5, wildcards(certificateRating));
		if (multiplayerSupport == 1) {
			searchVideoGames.setInt(6, 1);
		} else if (multiplayerSupport == 0) {
			searchVideoGames.setInt(6, 0);
		} else if (multiplayerSupport == -1) {
			searchVideoGames.setInt(6, -1);
		}

		// Run query
		ResultSet rs = searchVideoGames.executeQuery();
		ArrayList<VideoGame> results = new ArrayList<VideoGame>();
		while (rs.next()) {
			results.add(
				(VideoGame) Datastore.resources.get(rs.getInt("resourcesId")));
		}
		return results;
	}

	/**
	 * @return the currently logged in user.
	 */
	public static User getCurrentUser() {
		return Datastore.currentUser;
	}

	/**
	 * @return the database connection.
	 */
	public static Connection getDb() {
		return db;
	}

	/**
	 * @return the users hashmap.
	 */
	public static HashMap<String, User> getUsers() {
		return users;
	}

	/**
	 * @return the resources hashmap.
	 */
	public static HashMap<Integer, Resource> getResources() {
		return resources;
	}

	/**
	 * @return the events
	 */
	public static HashMap<Integer, Event> getEvents() {
		return events;
	}

	/**
	 * Logs the current user out.
	 */
	public static void logOut() {
		Datastore.currentUser = null;
	}

	/**
	 * Gets the number of borrows a customer has made in monthly buckets.
	 * 
	 * @param cust       the customer who has borrowed items.
	 * @param startRange the start of the daterange to search.
	 * @param endRange   the end of the daterange to search.
	 * @return an ObservableList of string dates (in yyyy-MM format) and
	 *         Numbers of borrows made on those dates.
	 * @throws SQLException if SQL errors.
	 */
	public static ObservableList<Data<String, Number>> getMonthlyBorrows(
		Customer cust, LocalDate startRange, LocalDate endRange)
		throws SQLException {
		// Set up output observable list
		ObservableList<Data<String, Number>> out =
			FXCollections.observableArrayList();
		// Set up DateTimeFormatter to output to
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM");

		// Get monthly borrow count
		PreparedStatement countMonthlyBorrows =
			db.prepareStatement("SELECT COUNT(*) AS count, "
				+ "/*First of the month is the date minus"
				+ " (the number of days into the month, minus 1)*/"
				+ " CAST(SUBDATE(transactionDate, INTERVAL ("
				+ "DAY(transactionDate) - 1) DAY) AS DATE)"
				+ " AS FirstOfMonth"
				+ " FROM transactionloans WHERE custRef = ? AND"
				+ " CAST(SUBDATE(transactionDate, INTERVAL ("
				+ "DAY(transactionDate) - 1) DAY) AS DATE) BETWEEN ? AND ?"
				+ " GROUP BY  custRef, FirstOfMonth"
				+ " ORDER BY FirstOfMonth");
		// Query with wildcards appended
		countMonthlyBorrows.setInt(1, cust.getUniqueId());
		countMonthlyBorrows.setDate(2, java.sql.Date.valueOf(startRange));
		countMonthlyBorrows.setDate(3, java.sql.Date.valueOf(endRange));
		ResultSet rs = countMonthlyBorrows.executeQuery();
		// Loop between dates to create 0 values

		/*
		 * Initialise the previous day value to the start of the month after or
		 * equal to the start of the range.
		 */
		LocalDate nextMonth;
		if (startRange.with(TemporalAdjusters.firstDayOfMonth())
			.isEqual(startRange)) {
			nextMonth = startRange;
		} else {
			nextMonth =
				startRange.with(TemporalAdjusters.firstDayOfNextMonth());
		}

		while (rs.next()) {
			LocalDate month = rs.getDate("FirstOfMonth").toLocalDate();

			// For every month between the next month to add until the new
			// month
			while (month.isAfter(nextMonth)) {
				// Convert month to a string
				String nextMonthAsString = nextMonth.format(format);
				// Add the 0 count month to the collection
				out.add(new Data<String, Number>(nextMonthAsString, 0));
				// Increment the next month value
				nextMonth = nextMonth.plusMonths(1);
			}
			// Add the current month to the collection
			int count = rs.getInt("count");
			// Convert month to a string
			String monthAsString = month.format(format);
			out.add(new Data<String, Number>(monthAsString, count));
			// Set the next month as this month plus 1 month
			nextMonth = month.plusMonths(1);
		}

		// While we still aren't at the end of the date range
		while (endRange.isAfter(nextMonth) || endRange.isEqual(nextMonth)) {
			// Convert month to a string
			String nextMonthAsString = nextMonth.format(format);
			// Add the 0 count month to the collection
			out.add(new Data<String, Number>(nextMonthAsString, 0));
			// Increment the next month value
			nextMonth = nextMonth.plusMonths(1);
		}
		return out;
	}

	/**
	 * Gets the number of borrows a customer has made in weekly buckets.
	 * 
	 * @param cust       the customer who has borrowed items.
	 * @param startRange the start of the daterange to search.
	 * @param endRange   the end of the daterange to search.
	 * @return an ObservableList of string dates (in yyyy-ww format) and
	 *         Numbers of borrows made on those dates.
	 * @throws SQLException if SQL errors.
	 */
	public static ObservableList<Data<String, Number>> getWeeklyBorrows(
		Customer cust, LocalDate startRange, LocalDate endRange)
		throws SQLException {
		// Set up output observable list
		ObservableList<Data<String, Number>> out =
			FXCollections.observableArrayList();
		// Set up DateTimeFormatter to output to
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-ww");

		// Get weekly borrow count
		PreparedStatement countWeeklyBorrows =
			db.prepareStatement("SELECT COUNT(*) AS count,"
				+ " /*Monday's date is today minus the number of days since"
				+ " monday*/"
				+ " CAST(SUBDATE(transactionDate, INTERVAL WEEKDAY("
				+ "transactionDate) DAY) AS DATE) AS MondayDate"
				+ " FROM transactionloans"
				+ " WHERE custRef = ? AND SUBDATE(transactionDate,"
				+ " INTERVAL WEEKDAY(transactionDate) DAY) BETWEEN ? AND ?"
				+ " GROUP BY  custRef, MondayDate" + " ORDER BY MondayDate");
		// Query with wildcards appended
		countWeeklyBorrows.setInt(1, cust.getUniqueId());
		countWeeklyBorrows.setDate(2, java.sql.Date.valueOf(startRange));
		countWeeklyBorrows.setDate(3, java.sql.Date.valueOf(endRange));
		ResultSet rs = countWeeklyBorrows.executeQuery();
		// Loop between dates to create 0 values

		/*
		 * Initialise the previous day value to the first Monday after or equal
		 * to the start of the range.
		 */
		LocalDate nextWeek =
			startRange.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

		while (rs.next()) {
			LocalDate week = rs.getDate("MondayDate").toLocalDate();

			// For every day between the next day to add until the new day
			while (week.isAfter(nextWeek)) {
				// Convert day to a string
				String nextWeekAsString = nextWeek.format(format);
				// Add the 0 count day to the collection
				out.add(new Data<String, Number>(nextWeekAsString, 0));
				// Increment the next day value
				nextWeek = nextWeek.plusWeeks(1);
			}
			// Add the current week to the collection
			int count = rs.getInt("count");
			// Convert day to a string
			String weekAsString = week.format(format);
			out.add(new Data<String, Number>(weekAsString, count));
			// Set the next week as this day plus 1 week
			nextWeek = week.plusWeeks(1);
		}

		// While we still aren't at the end of the date range
		while (endRange.isAfter(nextWeek)) {
			// Convert day to a string
			String nextWeekAsString = nextWeek.format(format);
			// Add the 0 count day to the collection
			out.add(new Data<String, Number>(nextWeekAsString, 0));
			// Increment the next day value
			nextWeek = nextWeek.plusWeeks(1);
		}
		return out;
	}

	/**
	 * Gets the number of borrows the given customer has made for each day in
	 * the given range.
	 * 
	 * @param cust       the customer to get the borrows for.
	 * @param startRange the start of the range to get borrows for.
	 * @param endRange   the end of the range to get borrows for.
	 * @return an ObservableList of string dates (in yyyy-MM-dd format) and
	 *         Numbers of borrows made on those dates.
	 * @throws SQLException if SQL errors.
	 */
	public static ObservableList<Data<String, Number>> getDailyBorrows(
		Customer cust, LocalDate startRange, LocalDate endRange)
		throws SQLException {
		// Set up output observable list
		ObservableList<Data<String, Number>> out =
			FXCollections.observableArrayList();
		// Set up DateTimeFormatter to output to
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// Get daily borrow count
		PreparedStatement countDailyBorrows =
			db.prepareStatement("SELECT COUNT(*) AS count, "
				+ "CAST(transactionloans.transactionDate AS DATE) AS day"
				+ " FROM transactionloans" + " WHERE custRef = ? AND "
				+ " CAST(transactionloans.transactionDate AS DATE)"
				+ " BETWEEN ? AND ?"
				+ " GROUP BY CAST(transactionloans.transactionDate AS DATE),"
				+ " custRef"
				+ " ORDER BY CAST(transactionloans.transactionDate AS DATE);");
		// Query with wildcards appended
		countDailyBorrows.setInt(1, cust.getUniqueId());
		countDailyBorrows.setDate(2, java.sql.Date.valueOf(startRange));
		countDailyBorrows.setDate(3, java.sql.Date.valueOf(endRange));
		ResultSet rs = countDailyBorrows.executeQuery();
		// Loop between dates to create 0 values

		// Initialise the previous day value to the start of the range.
		LocalDate nextDay = startRange.plusDays(0);

		// While there're still dates from SQL
		while (rs.next()) {
			LocalDate day = rs.getDate("day").toLocalDate();

			// For every day between the next day to add until the new day
			while (day.isAfter(nextDay)) {
				// Convert day to a string
				String nextDayAsString = nextDay.format(format);
				// Add the 0 count day to the collection
				out.add(new Data<String, Number>(nextDayAsString, 0));
				// Increment the next day value
				nextDay = nextDay.plusDays(1);
			}
			// Add the current day to the collection
			int count = rs.getInt("count");
			// Convert day to a string
			String dayAsString = day.format(format);
			out.add(new Data<String, Number>(dayAsString, count));
			// Set the next day as this day plus 1 day
			nextDay = day.plusDays(1);
		}

		// While we still aren't at the end of the date range
		while (endRange.isAfter(nextDay)) {
			// Convert day to a string
			String nextDayAsString = nextDay.format(format);
			// Add the 0 count day to the collection
			out.add(new Data<String, Number>(nextDayAsString, 0));
			// Increment the next day value
			nextDay = nextDay.plusDays(1);
		}

		return out;
	}

	/**
	 * Gets the most borrowed resources on the system in the given date range.
	 * 
	 * @param startRange   the start of the date range to search.
	 * @param endRange     the end of the date range to search.
	 * @param resourceType the type of the resource - any of "Books", "DVDs",
	 *                     "LaptopComputers", "VideoGames", or "Any" (which
	 *                     will show all resources). These names are maintained
	 *                     in the resourceTypesLookup view (apart from 'Any',
	 *                     which is in the query below), and their
	 *                     corresponding values are maintained in the
	 *                     resourceTypes view. Non-formatted versions of these
	 *                     views (i.e. easier to read) are in the DbCreate.sql
	 *                     file.
	 * @param limit        the maximum number of resources to get data about.
	 *                     If fewer resources of the selected type have
	 *                     borrows, that number will be shown instead.
	 * @return an ObservableList of resource titles and counts of how many
	 *         times they've been borrowed in the given time period.
	 * @throws SQLException if SQL errors.
	 */
	public static ObservableList<Data<String, Number>> getPopularResources(
		LocalDate startRange, LocalDate endRange, String resourceType,
		int limit) throws SQLException {
		// Set up output observable list
		ObservableList<Data<String, Number>> out =
			FXCollections.observableArrayList();
		PreparedStatement getPopularResources =
			db.prepareStatement("SELECT CONCAT(resources.title,"
				+ " ' (', CAST(resources.year AS CHAR), ')') AS title,"
				+ " COUNT(*) AS BorrowCount"
				+ " FROM resourceTypesNamed JOIN resources ON"
				+ " resources.resourcesId = resourceTypesNamed.resourcesId"
				+ " JOIN copies ON copies.resourceRef = resources.resourcesId"
				+ " JOIN transactionloans ON"
				+ " transactionloans.copyRef = copies.copiesId"
				+ " WHERE transactionloans.transactionDate BETWEEN ? AND ?"
				+ " AND ? IN ('Any',resourceTypesNamed.ResourceTypeString)"
				+ " GROUP BY resources.title" + " ORDER BY BorrowCount DESC"
				+ " LIMIT ?");
		// Query with wildcards appended
		getPopularResources.setDate(1, java.sql.Date.valueOf(startRange));
		// Increase endRange by 1 day, so as to be inclusive of the endRange
		getPopularResources.setDate(2,
			java.sql.Date.valueOf(endRange.plusDays(1)));
		getPopularResources.setString(3, resourceType);
		getPopularResources.setInt(4, limit);
		// Execute
		ResultSet rs = getPopularResources.executeQuery();

		while (rs.next()) {
			String title = rs.getString("title");
			int count = rs.getInt("BorrowCount");
			out.add(new Data<String, Number>(title, count));
		}
		return out;
	}

	/**
	 * Gets the users who have been fined the most.
	 * 
	 * @param limit the maximum number of users to display.
	 * @return an ObservableList of usernames and counts of how many times
	 *         they've been fined.
	 * @throws SQLException if SQL errors.
	 */
	public static ObservableList<Data<String, Number>>
		getMostFinedUsers(int limit) throws SQLException {
		// Set up output observable list
		ObservableList<Data<String, Number>> out =
			FXCollections.observableArrayList();
		PreparedStatement getMostFinedUsers =
			db.prepareStatement("SELECT COUNT(*) AS count, users.userName"
				+ " FROM users" + " JOIN transactionfines ON"
				+ " transactionfines.custRef = users.usersId"
				+ " GROUP BY users.userName" + " ORDER BY count DESC"
				+ " LIMIT ?");
		// Query with wildcards appended
		getMostFinedUsers.setInt(1, limit);
		// Execute
		ResultSet rs = getMostFinedUsers.executeQuery();

		while (rs.next()) {
			String userName = rs.getString("userName");
			int count = rs.getInt("count");
			out.add(new Data<String, Number>(userName, count));
		}
		return out;
	}

	/**
	 * Gets users who have the largest outstanding fine at this time.
	 * 
	 * @param limit the maximum number of users to display.
	 * @return an ObservableList of usernames and their balances.
	 * @throws SQLException if SQL errors.
	 */
	public static ObservableList<Data<String, Number>>
		getBiggestBalances(int limit) throws SQLException {
		// Set up output observable list
		ObservableList<Data<String, Number>> out =
			FXCollections.observableArrayList();
		PreparedStatement getBiggestBalances =
			db.prepareStatement("SELECT customers.balance, users.userName"
				+ " FROM users JOIN customers ON"
				+ " customers.usersId = users.usersId"
				+ " WHERE customers.balance <> 0" + " ORDER BY balance DESC"
				+ " LIMIT ?");
		// Query with wildcards appended
		getBiggestBalances.setInt(1, limit);
		// Execute
		ResultSet rs = getBiggestBalances.executeQuery();

		while (rs.next()) {
			String userName = rs.getString("userName");
			int balance = rs.getInt("balance");
			out.add(new Data<String, Number>(userName, balance));
		}
		return out;
	}

}
