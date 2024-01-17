package Core;

import java.util.Date;

import JavaFX.UserUI;

/**
 * This class represents the fine accumulated by a customer
 * 
 * @author Mike
 */
public class TransactionFine extends Transaction {
	private double amount;
	private Copy copyRef;
	private int daysOverdue;

	/**
	 * Creates a new TransactionFine object.
	 * 
	 * @param uniqueId    the fine's unique ID.
	 * @param amount      The amount of the fine.
	 * @param copyRef     The Copy that the fine was against.
	 * @param daysOverdue The number of days the Copy was overdue.
	 * @param date        The date the fine was created.
	 */
	public TransactionFine(int uniqueId, double amount, Copy copyRef,
		int daysOverdue, Date date) {
		super(uniqueId, date);
		this.amount = amount;
		this.copyRef = copyRef;
		this.daysOverdue = daysOverdue;
	}

	/**
	 * Gets the amount of the fine.
	 * 
	 * @return The amount of the fine as a double.
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Gets the Copy that the fine was against.
	 * 
	 * @return The Copy that the fine was against.
	 */
	public Copy getCopyRef() {
		return copyRef;
	}

	/**
	 * Gets the number of days the Copy that the fine was against was overdue
	 * 
	 * @return The number of days as an int.
	 */
	public int getDaysOverdue() {
		return daysOverdue;
	}

	/**
	 * @return The TransactionFine as a String.
	 */
	@Override
	public String toString() {
		return "FINE: Date: " + UserUI.ddmmyy.format(super.getDate())
			+ " Amount: £" + amount + " Resource: "
			+ copyRef.getResourceRef().getTitle() + " Days Overdue: "
			+ daysOverdue;
	}
}
