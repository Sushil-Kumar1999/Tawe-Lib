package Core;

import java.util.Calendar;
import java.util.Date;

/**
 * This class represents a Librarian and his knowledge/behaviour
 * 
 * @author Sushil Kumar
 * @version 1.0
 */
public class Librarian extends User {
	private int staffNumber;
	private Calendar employmentDate;

	/**
	 * Creates a new Librarian
	 * 
	 * @param uniqueId         the unique ID of this user.
	 * @param username         The username of the user
	 * @param firstName        The first name of the user
	 * @param surname          The surname of the user
	 * @param mobileNumber     The mobile phone number of the user
	 * @param address          The address of the user of the system
	 * @param profileImagePath The path to the profile image the user
	 * @param staffNumber      the staff number of this librarian.
	 * @param year             The year the librarian got employed
	 * @param month            The month the librarian got employed
	 * @param day              The day the librarian got employed
	 */
	public Librarian(int uniqueId, String username, String firstName,
		String surname, String mobileNumber, String address,
		String profileImagePath, int staffNumber, int year, int month,
		int day) {
		super(uniqueId, username, firstName, surname, mobileNumber, address,
			profileImagePath);
		this.staffNumber = staffNumber;
		setEmploymentDate(year, month, day);

	}

	/**
	 * Creates a new Librarian, as passed from the database.
	 * 
	 * @param uniqueId         the unique ID of this user.
	 * @param username         The username of the user
	 * @param firstName        The first name of the user
	 * @param surname          The surname of the user
	 * @param mobileNumber     The mobile phone number of the user
	 * @param address          The address of the user of the system
	 * @param profileImagePath The path to the profile image the user
	 * @param staffNumber      the staff number of this librarian.
	 * @param employmentDate   The date the librarian got employed
	 */
	public Librarian(int uniqueId, String username, String firstName,
		String surname, String mobileNumber, String address,
		String profileImagePath, int staffNumber, Date employmentDate) {
		super(uniqueId, username, firstName, surname, mobileNumber, address,
			profileImagePath);
		this.staffNumber = staffNumber;
		this.employmentDate = Calendar.getInstance();
		this.employmentDate.setTime(employmentDate);
	}

	/**
	 * @return the librarian's staff number.
	 */
	public int getStaffNumber() {
		return this.staffNumber;
	}

	/**
	 * @return The day of the month the librarian was employed on.
	 */
	public int getDay() {
		return employmentDate.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @return the calendar showing when the librarian was employed.
	 */
	public Calendar getEmploymentDate() {
		return this.employmentDate;
	}

	/**
	 * @return The month of the year the librarian was employed in.
	 */
	public int getMonth() {
		return employmentDate.get(Calendar.MONTH);
	}

	/**
	 * @return The year the librarian was employed in.
	 */
	public int getYear() {
		return employmentDate.get(Calendar.YEAR);
	}

	/**
	 * Sets the employment date of the librarian
	 * 
	 * @param year  The year the librarian got employed
	 * @param month The month the librarian got employed
	 * @param day   The day the librarian got employed
	 */
	private void setEmploymentDate(int year, int month, int day) {
		employmentDate = Calendar.getInstance();
		employmentDate.set(Calendar.YEAR, year);
		employmentDate.set(Calendar.MONTH, month);
		employmentDate.set(Calendar.DAY_OF_MONTH, day);
	}

	@Override
	/**
	 * Checks whether a user is a librarian
	 * 
	 * @return always true.
	 */
	public boolean isLibrarian() {
		return true;
	}
}
