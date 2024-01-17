package Core;
import java.util.Date;

/**
 * This class represents a transaction (paying fine, accumulating fine 
 * or loaning a copy of a resource)
 * @author Mike 
 *
 */
public abstract class Transaction implements Comparable<Transaction> {
	private final Date DATE;
	private final int UNIQUE_ID;

	/**
	 * Creates a new Transaction object.
	 * 
	 * @param uniqueId the ID of this transaction.
	 * @param date     The date the transaction took place.
	 */
	protected Transaction(int uniqueId, Date date) {
		this.UNIQUE_ID = uniqueId;
		this.DATE = date;
	}

	/**
	 * Gets the date the transaction took place.
	 * 
	 * @return The date as a Date type.
	 */
	public Date getDate() {
		return DATE;
	}

	/**
	 * Allows comparison of two transactions based on their date.
	 */
	@Override
	public int compareTo(Transaction t) {
		return this.DATE.compareTo(t.DATE);
	}

	/**
	 * Gets this transaction's unique ID.
	 * 
	 * @return this transaction's unique ID.
	 */
	public int getUniqueId() {
		return this.UNIQUE_ID;
	}
}
