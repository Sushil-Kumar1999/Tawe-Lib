package Core;

import java.util.Date;

import JavaFX.UserUI;

/**
 * This class represents a payment made by a customer towards a fine
 * 
 * @author Mike
 */
public class TransactionPayment extends Transaction {
	private double amount;

	/**
	 * Creates a new TransactionPayment object.
	 * 
	 * @param uniqueId the ID of this transaction.
	 * @param amount   The amount that was paid towards a fine.
	 * @param date     The date the payment was made.
	 */
	public TransactionPayment(int uniqueId, double amount, Date date) {
		super(uniqueId, date);
		this.amount = amount;
	}

	/**
	 * Gets the amount of the payment.
	 * 
	 * @return The amount of the payment as a double.
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @return The TransactionPayment object as a string.
	 */
	@Override
	public String toString() {
		return "PAYMENT: Date: " + UserUI.ddmmyy.format(super.getDate())
			+ " Amount: £" + amount;

	}
}
