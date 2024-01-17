package Core;

import java.sql.SQLException;
import java.util.Date;

import JavaFX.UserUI;

/**
 * This class models loaning of a copy to a customer
 * 
 * @author Mike
 */
public class TransactionLoan extends Transaction {
	private Customer custRef;
	private Date returnDate;

	/**
	 * Creates a new TransactionLoan object.
	 * 
	 * @param uniqueId the loan's unique ID.
	 * @param custRef  A reference to the customer that loaned the copy.
	 * @param loanDate The date the transaction took place on.
	 */
	public TransactionLoan(int uniqueId, Customer custRef, Date loanDate) {
		super(uniqueId, loanDate);
		this.custRef = custRef;
	}

	/**
	 * Loads a TransactionLoan from the database.
	 * 
	 * @param uniqueId   the loan's unique ID.
	 * @param custRef    the customer who loaned the copy.
	 * @param loanDate   the date the copy was loaned out.
	 * @param returnDate the date the copy was returned, or null if it's still
	 *                   loaned out.
	 */
	public TransactionLoan(int uniqueId, Customer custRef, Date loanDate,
		Date returnDate) {
		super(uniqueId, loanDate);
		this.custRef = custRef;
		this.returnDate = returnDate;
	}

	/**
	 * Gets the customer who loaned/returned the copy that the transaction
	 * refers to.
	 * 
	 * @return The Customer object that loaned/returned the copy that the
	 *         transaction refers to.
	 */
	public Customer getCustRef() {
		return custRef;
	}

	/**
	 * @return Gets the date the copy was returned.
	 */
	public Date getReturnDate() {
		return returnDate;
	}

	/**
	 * @param returnDate The date the copy was returned.
	 * @throws SQLException if SQL errors.
	 */
	public void setReturnDate(Date returnDate) throws SQLException {
		this.returnDate = returnDate;
		// Update the database
		Datastore.updateTransactionLoanSetReturnDate(this.getUniqueId(),
			returnDate);
	}

	/**
	 * Gives a string representation of a loan.
	 * 
	 * @return String containing a description of a loan.
	 */
	public String toString() {
		String returnString = "";
		returnString += "User: " + custRef.getUsername() + " Date Loaned: "
			+ UserUI.ddmmyy.format(this.getDate());
		if (this.getReturnDate() != null) {
			returnString += ", Date Returned: "
				+ UserUI.ddmmyy.format(this.getReturnDate());
		} else {
			returnString += ", Date Returned: NOT RETURNED";
		}

		return returnString;
	}

}
