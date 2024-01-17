package Core;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Represents a customer of the library.
 * 
 * @author Benjamin Kennard
 * @version 1.0
 */
public class Customer extends User {

	public static final int RESOURCE_CAP = 5;
	private int balance = 0;
	private ArrayList<Copy> currentLoans = new ArrayList<Copy>();
	private ArrayList<Transaction> fineHistory = new ArrayList<Transaction>();
	private ArrayList<Resource> currentRequests = new ArrayList<Resource>();
	private ArrayList<Copy> currentReserves = new ArrayList<Copy>();
	private ArrayList<ReviewAndRating> reviewsAndRatings =
		new ArrayList<ReviewAndRating>();
	private ArrayList<Event> upcommingEvents = new ArrayList<Event>();
	private ArrayList<Event> attendedEvents = new ArrayList<Event>();

	private LocalDateTime lastLogin;

	/**
	 * Constructor for a new Customer.
	 * 
	 * @param uniqueId     the customer's unique ID.
	 * @param username     The customer's username.
	 * @param firstName    The customer's first name.
	 * @param surname      The customer's surname.
	 * @param mobileNumber The customer's mobile phone number.
	 * @param address      The customer's address.
	 * @param imagePath    The path to the avatar the customer has chosen.
	 */
	public Customer(int uniqueId, String username, String firstName,
		String surname, String mobileNumber, String address,
		String imagePath) {
		super(uniqueId, username, firstName, surname, mobileNumber, address,
			imagePath);
		lastLogin = LocalDateTime.MAX;
	}

	/**
	 * Constructor for a customer being loaded from the database.
	 * 
	 * @param uniqueId       the customer's unique ID.
	 * @param username       The customer's username.
	 * @param firstName      The customer's first name.
	 * @param surname        The customer's surname.
	 * @param mobileNumber   The customer's mobile phone number.
	 * @param address        The customer's address.
	 * @param imagePath      The path to the avatar the customer has chosen.
	 * @param balance        The customer's balance.
	 * @param paymentHistory The customer's history of payments. (Fines are
	 *                       loaded later.)
	 * @param attendedEvents the collection of events the user has attended.
	 * @param upcomingEvents the collection of events the user has subscribed
	 *                       to.
	 * @param lastLogin      the last time the user logged in.
	 */
	public Customer(int uniqueId, String username, String firstName,
		String surname, String mobileNumber, String address, String imagePath,
		int balance, ArrayList<Transaction> paymentHistory,
		ArrayList<Event> attendedEvents, ArrayList<Event> upcomingEvents,
		LocalDateTime lastLogin) {
		this(uniqueId, username, firstName, surname, mobileNumber, address,
			imagePath);
		this.balance = balance;
		this.fineHistory = paymentHistory;
		this.attendedEvents = attendedEvents;
		this.upcommingEvents = upcomingEvents;

		this.lastLogin = lastLogin;
	}

	public void updateLastSeen() throws SQLException {
		lastLogin = LocalDateTime.now();
		Datastore.updateLastSeen(lastLogin, super.getUniqueId());
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	@Override
	/**
	 * @return always false, as Customers are not librarians.
	 */
	public boolean isLibrarian() {
		return false;
	}

	/**
	 * Pays a customer's fine by the amount input.
	 * 
	 * @param paymentAmount the amount paid by the customer.
	 * @throws IllegalArgumentException if the balance
	 * @throws SQLException             if SQL errors.
	 */
	public void payFine(int paymentAmount)
		throws IllegalArgumentException, SQLException {
		if (balance < paymentAmount) {
			throw new IllegalArgumentException("Payment amount ("
				+ paymentAmount + ") greater than current balance ("
				+ this.balance + ")!");
		} else {
			// update balance
			this.balance -= paymentAmount;
			Datastore.updateCustomerFine(this.getUniqueId(), this.balance);
			Date now = new Date();
			// Add transaction payment
			int transPaymentId = Datastore.insertTransactionPayment(
				this.getUniqueId(), paymentAmount, now);
			fineHistory.add(
				new TransactionPayment(transPaymentId, paymentAmount, now));
			// Update database
		}
	}

	/**
	 * @return the customer's current fine amount.
	 */
	public int getFine() {
		return this.balance;
	}

	/**
	 * @return the history of the customer's fines.
	 */
	public ArrayList<Transaction> getFineTransactions() {
		return this.fineHistory;
	}

	/**
	 * @return the customer's loaned copies.
	 */
	public ArrayList<Copy> getCurrentLoans() {
		return currentLoans;
	}

	/**
	 * @return the customer's requested resources.
	 */
	public ArrayList<Resource> getCurrentRequests() {
		return currentRequests;
	}

	/**
	 * @return The customer's balance.
	 */
	public int getBalance() {
		return balance;
	}

	/**
	 * @return the customer's reserved copies.
	 */
	public ArrayList<Copy> getCurrentReserves() {
		return currentReserves;
	}

	/**
	 * @return the upcommingEvents
	 */
	public ArrayList<Event> getUpcommingEvents() {
		return upcommingEvents;
	}

	/**
	 * @param upcommingEvents the upcommingEvents to set
	 */
	public void setUpcommingEvents(ArrayList<Event> upcommingEvents) {
		this.upcommingEvents = upcommingEvents;
	}

	/**
	 * @return the attendedEvents
	 */
	public ArrayList<Event> getAttendedEvents() {
		return attendedEvents;
	}

	/**
	 * Returns a borrowed copy for this Customer and fines them if its late.
	 * This function should be called by the Copy's returnCopy function.
	 * 
	 * @param borrowedCopy the copy the customer is returning.
	 * @throws SQLException if SQL errors.
	 */
	public void returnBorrowed(Copy borrowedCopy) throws SQLException {
		// Current datetime
		Date now = new Date();
		// Get number of days since duedate
		int daysOverdue = borrowedCopy.getDaysOverdue();
		// If a fine should be applied
		if (daysOverdue > 0) {
			// Get resource reference
			Resource res = borrowedCopy.getResourceRef();
			// Get amount of fine to be added
			int fineAmount = res.calculateFine(daysOverdue);
			// Add fine amount
			balance += fineAmount;
			// Adds transaction and balance change to the database.
			Datastore.updateCustomerFine(this.getUniqueId(), this.balance);
			int fineId = Datastore.insertTransactionFine(this.getUniqueId(),
				fineAmount, borrowedCopy.getUniqueId(), daysOverdue, now);
			// Add transaction
			fineHistory.add(new TransactionFine(fineId, fineAmount,
				borrowedCopy, daysOverdue, now));
		}
		// Remove copy from loan list
		currentLoans.remove(borrowedCopy);
	}

	/**
	 * Adds a Resource to the user's requested resources.
	 * 
	 * @param requestedResource The resource the user is requesting.
	 */
	public void request(Resource requestedResource) {
		currentRequests.add(requestedResource);
	}

	/**
	 * Adds a resource to the user's reserved resources. Used when a requested
	 * resource is available already.
	 * 
	 * @param availableCopy an available copy of the requested resource.
	 */
	public void reserve(Copy availableCopy) {
		currentReserves.add(availableCopy);
	}

	/**
	 * Updates a requested resource to a reserved copy.
	 * 
	 * @param availableCopy The copy available to fulfill the request.
	 * @throws IllegalArgumentException Thrown if the resource isn't requested.
	 * @throws SQLException             if SQL errors.
	 */
	public void updateRequestedToReserved(Copy availableCopy)
		throws IllegalArgumentException, SQLException {
		Resource res = availableCopy.getResourceRef();
		/*
		 * Error checking - this function should never be called if the
		 * customer doesn't have the resource requested.
		 */
		if (!currentRequests.contains(res)) {
			throw new IllegalArgumentException(
				"This resource is not currently requested by this user!");
		} else {
			/*
			 * Remove resource from requests. This has already been done in the
			 * database as part of res.getNextRequest().
			 */
			currentRequests.remove(res);
			// add to reserves
			currentReserves.add(availableCopy);
			// Update on the copy's side. This also updates the db.
			availableCopy.reserve(this);
		}
	}

	/**
	 * Borrows the referenced copy. If the copy has already been reserved, the
	 * reservation is removed.
	 * 
	 * @param availableCopy the copy to borrow.
	 */
	public void borrow(Copy availableCopy) {
		/*
		 * If the copy is reserved by the user, remove the reservation This
		 * change has already been made in the database.
		 */

		if (currentReserves.contains(availableCopy)) {
			currentReserves.remove(availableCopy);
		}
		/*
		 * Add the copy to the user's borrowed list. This change has already
		 * been made in the database.
		 */
		currentLoans.add(availableCopy);
	}

	/**
	 * Imports the entire fine history without any additional checks.
	 * 
	 * @param fineHistory the transaction collection to import.
	 */
	public void importTransactions(ArrayList<Transaction> fineHistory) {
		this.fineHistory = fineHistory;
	}

	/**
	 * Imports a transaction without any additional checks.
	 * 
	 * @param transIn the transaction to import.
	 */
	public void importTransaction(Transaction transIn) {
		this.fineHistory.add(transIn);
	}

	/**
	 * Sorts the fine history for this customer.
	 */
	public void sortTransactionHistory() {
		Collections.sort(this.fineHistory);
	}

	/**
	 * Imports a request, without any additional checks or changes.
	 * 
	 * @param request the request to import.
	 */
	public void importRequest(Resource request) {
		currentRequests.add(request);
	}

	/**
	 * Imports a reserve, without any additional checks or changes.
	 * 
	 * @param reserve the reserve to import.
	 */
	public void importReserve(Copy reserve) {
		currentReserves.add(reserve);
	}

	/**
	 * Imports a borrow, without any additional checks or changes.
	 * 
	 * @param loan the loan to import.
	 */
	public void importLoan(Copy loan) {
		currentLoans.add(loan);
	}

	/**
	 * Checks whether the customer has requested this resource.
	 * 
	 * @param res The resource to check requested status of.
	 * @return True if resource is requested, false if not.
	 */
	public boolean isResourceRequested(Resource res) {
		return currentRequests.contains(res);
	}

	/**
	 * Checks whether a copy of a given resource is reserved by this customer.
	 * 
	 * @param res The resource to check reservation for.
	 * @return True if a copy of that resource is reserved, false otherwise.
	 */
	public boolean isResourceReserved(Resource res) {
		for (Copy c : currentReserves) {
			if (c.getResourceRef() == res) {
				// If resource matches return true
				return true;
			}
		}
		// If no copy's resources match, return false
		return false;
	}

	/**
	 * Checks if the customer should be allowed to borrow items.
	 * 
	 * @return whether the user should be allowed to borrow items.
	 */
	public boolean canBorrow() {
		// If the user has a fine, they cannot borrow
		if (balance > 0) {
			return false;
		} else {
			/*
			 * If any of the user's loaned copies are overdue, they cannot
			 * borrow
			 */
			for (Copy c : currentLoans) {
				if (c.isOverdue()) {
					return false;
				}
			}
			// Otherwise, the user can borrow
			return true;
		}
	}

	/**
	 * This method returns the sum of resource count values of all resources
	 * borrowed by the customer
	 * 
	 * @return sum The sum of resource count values
	 * @author Sushil Kumar
	 */
	public int getSumOfBorrowedResourceCountValues() {
		int sum = 0;
		for (Copy loanedCopy : currentLoans) {
			sum += loanedCopy.getResourceRef().getResourceCountValue();
		}
		return sum;
	}

	/**
	 * Returns the list of review and rating made by the customer
	 * 
	 * @author Sushil Kumar
	 * @return the reviewAndRatings The review and rating given by the user
	 */
	public ArrayList<ReviewAndRating> getReviewsAndRatings() {
		return reviewsAndRatings;
	}
}
