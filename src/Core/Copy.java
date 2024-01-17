package Core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a copy of a resource
 * 
 * @author Mike, Billy
 */
public class Copy {
	private final int UNIQUE_ID;
	private Resource resourceRef;
	private int loanDuration;
	private Date dueDate = null;
	private Customer custRef = null;
	private Customer reservedBy = null;
	private Date loanDate = null;
	private ArrayList<TransactionLoan> transactionHistory =
		new ArrayList<TransactionLoan>();

	/**
	 * Creates a new Copy object.
	 * 
	 * @param uniqueId    the copy's unique ID.
	 * @param resourceRef The resource that the Copy object is a copy of.
	 */
	public Copy(int uniqueId, Resource resourceRef) {
		this.UNIQUE_ID = uniqueId;
		this.resourceRef = resourceRef;
	}

	/**
	 * Loads a Copy from the database.
	 * 
	 * @param uniqueId           The uniqueId of the copy.
	 * @param resourceRef        The resource the copy is a copy of.
	 * @param loanDuration       The duration of the loan, if present.
	 * @param dueDate            The date the copy is due to be returned, if
	 *                           present.
	 * @param custRef            The customer who has the copy on loan, if the
	 *                           copy's loaned.
	 * @param reservedBy         The customer who has the copy reserved, if
	 *                           present.
	 * @param loanDate           the date the copy was loaned out on.
	 * @param transactionHistory The information about historical loans of this
	 *                           copy.
	 */
	public Copy(int uniqueId, Resource resourceRef, int loanDuration,
		Date dueDate, Customer custRef, Customer reservedBy, Date loanDate,
		ArrayList<TransactionLoan> transactionHistory) {
		this.UNIQUE_ID = uniqueId;
		this.resourceRef = resourceRef;
		this.loanDuration = loanDuration;
		this.dueDate = dueDate;
		this.custRef = custRef;
		this.reservedBy = reservedBy;
		this.transactionHistory = transactionHistory;
		this.loanDate = loanDate;
	}

	/**
	 * Gets the unique ID of this copy.
	 * 
	 * @return the unique ID of this copy.
	 */
	public int getUniqueId() {
		return this.UNIQUE_ID;
	}

	/**
	 * @return The resource that the Copy object is a copy of.
	 */
	public Resource getResourceRef() {
		return resourceRef;
	}

	/**
	 * @return The due date of the copy.
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * @return If a copy is overdue, returns the number of days it is overdue.
	 */
	public int getDaysOverdue() {
		// If the duedate is unset, return 0
		if (dueDate == null) {
			return 0;
		} else {
			// Get the current time minus the duedate
			return (int) TimeUnit.DAYS.convert(
				new Date().getTime() - this.dueDate.getTime(),
				TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * @return The number of days the copy has been loaned for as an int.
	 */
	public int getLoanDuration() {
		return loanDuration;
	}

	/**
	 * @return The customer currently loaning the copy as a Customer object.
	 */
	public Customer getCustRef() {
		return custRef;
	}

	/**
	 * @return The customer currently reserving the copy as a Customer object.
	 */
	public Customer getReservedBy() {
		return reservedBy;
	}

	/**
	 * @return The date the copy was loaned as a Date type.
	 */
	public Date getLoanDate() {
		return loanDate;
	}

	/**
	 * @return The transaction history of an object as an array list of
	 *         TransactionLoan.
	 */
	public ArrayList<TransactionLoan> getTransactionHistory() {
		return this.transactionHistory;
	}

	/**
	 * @param loanDuration The number of days the copy has been loaned for.
	 */
	public void setLoanDuration(int loanDuration) {
		this.loanDuration = loanDuration;
	}

	/**
	 * @param custRef The Customer that has loaned the Copy.
	 */
	public void setCustRef(Customer custRef) {
		this.custRef = custRef;
	}

	/**
	 * @param reservedBy The Customer that has reserved the Copy.
	 */
	public void setReservedBy(Customer reservedBy) {
		this.reservedBy = reservedBy;
	}

	/**
	 * @param loanDate The date the Copy was loaned.
	 */
	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	/**
	 * Sets the due date of the Copy.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	public void setDueDate() throws SQLException {
		final int MILLISECONDS_IN_A_DAY = 86400000;
		// The minimum due date is the date the copy was loaned + its loan
		// duration.
		Date minDueDate =
			new Date(TimeUnit.MILLISECONDS.convert(loanDuration, TimeUnit.DAYS)
				+ loanDate.getTime());
		// Now is the current date.
		Date now = new Date();
		// If the minimum date has passed, set it to the day after the current
		// date.
		if (now.after(minDueDate)) {
			dueDate = new Date(now.getTime() + MILLISECONDS_IN_A_DAY);
		}
		// If not, set it to the day after the minimum due date.
		else {
			dueDate = new Date(minDueDate.getTime() + MILLISECONDS_IN_A_DAY);
		}
		// Update database
		Datastore.updateCopyDueDate(this.UNIQUE_ID, dueDate);
	}

	/**
	 * Checks whether the copy is overdue.
	 * 
	 * @return Whether the copy is overdue or not.
	 */
	public boolean isOverdue() {
		// If duedate is unset
		if (dueDate == null) {
			return false;
		} else {
			// check if now is later than the due date.
			return (new Date().getTime() > this.dueDate.getTime());
		}
	}

	/**
	 * Checks Whether the copy is available to loan.
	 * 
	 * @return Whether the copy is available to loan.
	 */
	public boolean isAvailable() {
		return (custRef == null && reservedBy == null);
	}

	/**
	 * Reserves the copy for a customer if the copy is available. Additionally,
	 * updates the database with this information.
	 * 
	 * @param reserver The Customer reserving the copy.
	 * @throws SQLException if SQL errors.
	 */
	public void reserve(Customer reserver) throws SQLException {
		reservedBy = reserver;
		Datastore.updateCopyReserve(this.UNIQUE_ID, reserver.getUniqueId());
	}

	/**
	 * The method that is called when a copy is borrowed.
	 * 
	 * @param newCustRef   The customer borrowing the copy.
	 * @param loanDuration the duration for the copy to be loaned out for.
	 * @throws IllegalArgumentException if the customer cannot borrow this
	 *                                  copy.
	 * @throws SQLException             if SQL errors.
	 */
	public void borrow(Customer newCustRef, int loanDuration)
		throws IllegalArgumentException, SQLException {
		// If the copy is being loaned, it cannot be borrowed.
		if (custRef != null) {
			throw new IllegalArgumentException("Copy is already being loaned");
		}
		// Check if the customer can borrow items.
		if (!(newCustRef.canBorrow())) {
			throw new IllegalArgumentException("Customer cannot borrow items");
		}
		// Check if the customer has reached or exceeded the resource cap
		if ((newCustRef.getSumOfBorrowedResourceCountValues()
			+ this.resourceRef
				.getResourceCountValue()) > Customer.RESOURCE_CAP) {
			throw new IllegalArgumentException(
				"Customer has exceeded the" + " resource cap");
		}
		/*
		 * Check to see if the copy is reserved, and if it is, check to see if
		 * the customer reserving it is not the customer trying to borrow it.
		 */
		if (reservedBy != null && reservedBy != newCustRef) {
			throw new IllegalArgumentException("Copy is currently reserved");
		}

		this.custRef = newCustRef;
		this.loanDuration = loanDuration;
		this.reservedBy = null;
		// Generate date the copy was loaned
		Date loanDate = new Date();
		this.loanDate = loanDate;
		custRef.borrow(this);
		// Update the database
		Datastore.updateCopyLoan(this.UNIQUE_ID, custRef.getUniqueId(),
			this.loanDuration, loanDate);
		/*
		 * If there are more requests for the resource than the number of
		 * copies of the resource then its due date must be set right away.
		 */
		if (resourceRef.moreRequestsThanCopies()) {
			this.setDueDate();
		}
		Date now = new Date();
		int transLoanId = Datastore.insertTransactionLoan(this.UNIQUE_ID,
			custRef.getUniqueId(), now);
		// Create a new transactionHistory object recording the copy being
		// loaned.
		transactionHistory.add(new TransactionLoan(transLoanId, custRef, now));
	}

	/**
	 * The method that is called when a copy is returned.
	 * 
	 * @throws SQLException if SQL errors.
	 */
	public void returnCopy() throws SQLException {
		// Return this copy
		custRef.returnBorrowed(this);
		custRef = null;
		loanDuration = 0;
		loanDate = null;
		dueDate = null;
		Datastore.updateCopyRemoveLoan(this.UNIQUE_ID);
		/*
		 * If the resource that the copy refers to has requests, take the first
		 * request and then reserve the copy for that customer.
		 */
		if (resourceRef.getNumRequests() != 0) {
			reservedBy = resourceRef.nextRequest();
			reservedBy.updateRequestedToReserved(this);
		}
		/*
		 * Set the return date of the most recent transactionLoan object to the
		 * current date.
		 */
		transactionHistory.get(transactionHistory.size() - 1)
			.setReturnDate(new Date());
	}
}
