package Core;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * A class that represents a DVD.
 * 
 * @author Ali Alowais and Tyunay Kamber
 * @version 1.0
 */

public class DVD extends Resource {

	private static final int FINE_PER_DAY = 2;
	private static final int MAX_FINE = 25;

	private String director;
	private int runtime;
	private String language;
	private ArrayList<String> subtitleList;

	/**
	 * Constructor for a DVD.
	 * 
	 * @param uniqueId     the unique id of each dvd.
	 * @param title        the title of dvd.
	 * @param year         the year of dvd.
	 * @param director     the director of dvd.
	 * @param runtime      the runtime of dvd.
	 * @param language     the language of dvd.
	 * @param subtitleList the subtitle of dvd.
	 * @param additionDate the time the resource is created on.
	 */
	public DVD(int uniqueId, String title, int year, String director,
		int runtime, String language, ArrayList<String> subtitleList,
		LocalDateTime additionDate) {
		super(uniqueId, title, year, additionDate);
		this.director = director;
		this.runtime = runtime;
		this.language = language;
		this.subtitleList = subtitleList;
	}

	/**
	 * Updates the attributes of this DVD.
	 * 
	 * @param title        the title of dvd.
	 * @param year         the year of dvd.
	 * @param director     the director of dvd.
	 * @param runtime      the runtime of dvd.
	 * @param language     the language of dvd.
	 * @param subtitleList the subtitle of dvd.
	 * @throws SQLException if SQL errors.
	 */
	public void editDVD(String title, String year, String director,
		String runtime, String language, ArrayList<String> subtitleList)
		throws SQLException {
		// Update the parent data
		this.editResource(title, year);
		// Update DVD data
		this.director = director;
		this.runtime = Integer.parseInt(runtime);
		this.language = language;
		this.subtitleList = subtitleList;
		// Update db
		Datastore.updateDVD(this.getUniqueID(), director, runtime, language,
			subtitleList);

	}

	/**
	 * return the director of DVD.
	 * 
	 * @return director.
	 */
	public String getDirector() {
		return director;
	}

	/**
	 * return the runtime of DVD.
	 * 
	 * @return runtime.
	 */
	public int getRuntime() {
		return runtime;
	}

	/**
	 * return the language.
	 * 
	 * @return language.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * return the ArrayList of subtitleList.
	 * 
	 * @return subtitleList.
	 */
	public ArrayList<String> getSubtitleList() {
		return subtitleList;
	}

	@Override
	public String toString() {
		return "DVD [director=" + director + ", runtime=" + runtime
			+ ", language=" + language + ", subtitleLanguages=" + subtitleList
			+ "]";
	}

	@Override
	/**
	 * Calculates the amount of fine due if this resource is overdue.
	 * 
	 * @param daysOverdue the number of days the copy is overdue.
	 */
	public int calculateFine(int daysOverdue) {
		return super.calculateFine(daysOverdue, DVD.FINE_PER_DAY,
			DVD.MAX_FINE);
	}

	/**
	 * Returns the list of subtitle langauges as a comma seperated string.
	 * 
	 * @return list of subtitle langauges as a comma seperated string.
	 */
	public String getSubtitleListString() {
		return String.join(", ", this.subtitleList);
	}

	@Override
	/**
	 * The type of the resource.
	 * 
	 * @return "DVD"
	 */
	public String getType() {
		return "DVD";
	}

	public int getResourceCountValue() {
		return 1;
	}
}