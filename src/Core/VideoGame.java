package Core;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * This class represents a video game.
 * 
 * @author Billy Roberts.
 * @version 1.0
 */
public class VideoGame extends Resource {

	private static final int FINE_PER_DAY = 5;
	private static final int MAX_FINE = 50;

	private String publisher;
	private String genre;
	private String certificateRating;
	private boolean multiplayerSupport;

	/**
	 * Creates a new VideoGame Object.
	 * 
	 * @param uniqueId           The Unique ID that is used to identify a
	 *                           VideoGame.
	 * @param title              The title of the VideoGame.
	 * @param year               The year that the VideoGame was released.
	 * @param publisher          The publisher of the VideoGame.
	 * @param genre              The genre of the VideoGame.
	 * @param certificateRating  The certificate rating of the VideoGame.
	 * @param multiplayerSupport Whether the VideoGame has multiplayer support
	 *                           (True if it does).
	 * @param additionDate       the date the videoGame was created on.
	 */
	public VideoGame(int uniqueId, String title, int year, String publisher,
		String genre, String certificateRating, boolean multiplayerSupport,
		LocalDateTime additionDate) {
		super(uniqueId, title, year, additionDate);
		this.publisher = publisher;
		this.genre = genre;
		this.certificateRating = certificateRating;
		this.multiplayerSupport = multiplayerSupport;
	}

	/**
	 * Used to edit the values of a specific VideoGame.
	 * 
	 * @param title              Updated Title of the VideoGame.
	 * @param year               Updated Year of the VideoGame.
	 * @param publisher          Updated Publisher of the VideoGame.
	 * @param genre              Updated Genre of the VideoGame.
	 * @param certificateRating  Updated Certificate Rating of the VideoGame.
	 * @param multiplayerSupport Updated True or False value concerning whether
	 *                           the VideoGame has multiplayer support (True if
	 *                           it does).
	 * @throws SQLException If SQL errors.
	 */
	public void editVideoGame(String title, String year, String publisher,
		String genre, String certificateRating, boolean multiplayerSupport)
		throws SQLException {
		// Update the parent data.
		this.editResource(title, year);
		// Update the data of the current object.
		this.publisher = publisher;
		this.genre = genre;
		this.certificateRating = certificateRating;
		this.multiplayerSupport = multiplayerSupport;
		// Update the database with the new values. TODO Add updateVideoGame
		// method to Datastore.
		// Datastore.updateVideoGame(this.getUniqueID(), publisher, genre,
		// certificateRating, multiplayerSupport);
	}

	/**
	 * Returns the publisher of the VideoGame.
	 * 
	 * @return Publisher of the VideoGame.
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * Returns the genre of the VideoGame.
	 * 
	 * @return Genre of the VideoGame.
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Returns the certificate rating of the VideoGame
	 * 
	 * @return Certificate rating of the VideoGame.
	 */
	public String getCertificateRating() {
		return certificateRating;
	}

	/**
	 * Returns true or false depending on whether the VideoGame has multiplayer
	 * support.
	 * 
	 * @return True if the VideoGame has muliplayer support, false otherwise.
	 */
	public boolean getMultiplayerSupport() {
		return multiplayerSupport;
	}

	/**
	 * Returns the type of the Resource.
	 * 
	 * @return "VideoGame"
	 */
	public String getType() {
		return "VideoGame";
	}

	/**
	 * Calculates the fine due if the VideoGame is overdue.
	 * 
	 * @param daysOverdue The number of days the copy is overdue.
	 * @return The fine that is due.
	 */
	public int calculateFine(int daysOverdue) {
		return super.calculateFine(daysOverdue, VideoGame.FINE_PER_DAY,
			VideoGame.MAX_FINE);
	}

	public int getResourceCountValue() {
		return 1;
	}
}
