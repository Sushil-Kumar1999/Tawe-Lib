package Core;

import java.util.Date;

/**
 * A class that describes an event.
 * 
 * @author Mike
 */
public class Event {
	/**
	 * The date the event takes place.
	 */
	private Date date;
	/**
	 * The name of the event.
	 */
	private String name;
	/**
	 * The maximum number of people that can attend the event.
	 */
	private int maxAttendees;
	/**
	 * The number of people attending the event.
	 */
	private int attendees;
	/**
	 * A description of the event.
	 */
	private String description;
	/**
	 * A unique ID referring to the event.
	 */
	private int uniqueID;

	/**
	 * Creates a new event.
	 * 
	 * @param date         The date the event takes place.
	 * @param name         The name of the event.
	 * @param maxAttendees The maximum number of people that can attend the
	 *                     event.
	 * @param uniqueID     the unique ID of this event.
	 * @param description  A description of the event.
	 */
	public Event(int uniqueID, Date date, String name, int maxAttendees,
		String description) {
		this.uniqueID = uniqueID;
		this.date = date;
		this.name = name;
		this.maxAttendees = maxAttendees;
		this.description = description;
		attendees = 0;
	}

	/**
	 * Adds new attendee to the event.
	 * 
	 * @throws IndexOutOfBoundsException Thrown if event is at max attendance.
	 */
	public void addAttendee() {
		attendees++;
	}

	/**
	 * Removes an attendee from the event.
	 * 
	 * @throws IndexOutOfBoundsException Thrown if event has no attendees.
	 */
	public void removeAttendee() {
		attendees--;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the uniqueID
	 */
	public int getUniqueID() {
		return uniqueID;
	}

	/**
	 * @return the maxAttendees
	 */
	public int getMaxAttendees() {
		return maxAttendees;
	}

	/**
	 * @return the attendees
	 */
	public int getAttendees() {
		return attendees;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the number of places left on the event
	 */
	public int getPlacesLeft() {
		return maxAttendees - attendees;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the Event details in a string
	 */
	public String toString() {
		return "NAME: " + name + " DATE: " + date.toString() + " ATTENDEES: "
			+ attendees;
	}

}
