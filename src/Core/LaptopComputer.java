package Core;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * A class that represents LaptopComputer.
 * 
 * @author Ali Alowais and Tyunay Kamber
 * @version 1.0
 */
public class LaptopComputer extends Resource {

	private static final int FINE_PER_DAY = 10;
	private static final int MAX_FINE = 100;

	private String manufacturer;
	private String model;
	private String OS;

	/**
	 * Constructor for a LaptopComputer.
	 *
	 * @param uniqueId     the unique id of each laptop and computer.
	 * @param title        the title of laptop and computer.
	 * @param year         the year of laptop and computer.
	 * @param manufacturer the manfacturer of laptop and computer.
	 * @param model        the model of laptop and computer.
	 * @param OS           the OS of laptop and computer.
	 * @param additionDate the time the resource is created on.
	 */
	public LaptopComputer(int uniqueId, String title, int year,
		String manufacturer, String model, String OS,LocalDateTime additionDate) {
		super(uniqueId, title, year,additionDate);
		this.manufacturer = manufacturer;
		this.model = model;
		this.OS = OS;
	}

	/**
	 * return the manufacturer of LaptopComputer.
	 *
	 * @return manufacturer.
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * return the model of LaptopComputer.
	 *
	 * @return model.
	 */
	public String getModel() {
		return model;
	}

	/**
	 * return the OS of laptop and computer.
	 *
	 * @return installedOS.
	 */
	public String getOS() {
		return OS;
	}

	/**
	 * Gives a string representation of this resource.
	 * 
	 * @return a string representation of this resource.
	 */
	@Override
	public String toString() {
		return "Laptop [manufacturer=" + manufacturer + ", model=" + model
			+ ", installedOS=" + OS + "]";
	}
	
	/**
	 * Calculates the amount of fine that should be paid given the number of
	 * days the copy is overdue.
	 * 
	 * @param daysOverdue the number of days the copy is overdue.
	 * @return the fine amount in GBP.
	 */
	@Override
	public int calculateFine(int daysOverdue) {
		return super.calculateFine(daysOverdue, LaptopComputer.FINE_PER_DAY,
			LaptopComputer.MAX_FINE);
	}

	/**
	 * Sets the laptopComputer's attributes to the ones given.
	 * 
	 * @param title        the title of laptop and computer.
	 * @param year         the year of laptop and computer.
	 * @param manufacturer the manfacturer of laptop and computer.
	 * @param model        the model of laptop and computer.
	 * @param OS           the OS of laptop and computer.
	 * @throws SQLException if SQL errors.
	 */
	public void editLaptopComputer(String title, String year,
		String manufacturer, String model, String OS) throws SQLException {
		// Update the parent data
		this.editResource(title, year);
		// Update this data
		this.manufacturer = manufacturer;
		this.model = model;
		this.OS = OS;
		// Update the database
		Datastore.updateLaptopComputer(this.getUniqueID(), manufacturer, model,
			OS);

	}

	@Override
	/**
	 * The type of the resource.
	 * 
	 * @return "Laptop"
	 */
	public String getType() {
		return "Laptop";
	}
	
	public int getResourceCountValue() {
		return 3;
	}
}
