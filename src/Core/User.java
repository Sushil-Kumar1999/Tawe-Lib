package Core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Holds information about a specific user of the library system.
 * 
 * @author Ben Kennard
 */
public abstract class User {
	// Variable initialisation
	private final int UNIQUE_ID;
	private String username;
	private String firstName;
	private String surname;
	private String mobileNumber;
	private String address;
	private String profileImagePath;
	private static HashMap<String, String> defaultAvatars =
		new HashMap<String, String>();

	/**
	 * Instantiates information about this user.
	 * 
	 * @param username     The username of the user of the system.
	 * @param firstName    The first name of the user of the system.
	 * @param surname      The surname of the user of the system.
	 * @param mobileNumber The mobile phone number of the user of the system.
	 * @param address      The address of the user of the system.
	 * @param imagePath    The path to the avatar the user of the system has
	 *                     chosen.
	 * @param uniqueId     The user's unique ID.
	 */
	protected User(int uniqueId, String username, String firstName,
		String surname, String mobileNumber, String address,
		String imagePath) {
		this.UNIQUE_ID = uniqueId;
		this.username = username;
		this.firstName = firstName;
		this.surname = surname;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.profileImagePath = imagePath;
	}

	/**
	 * @return True if the user is a librarian; False if they are not.
	 */
	public abstract boolean isLibrarian();

	/**
	 * Adds an avatar to the collection of default avatars, allowing the avatar
	 * to be chosen.
	 * 
	 * @param avatarName The name of the avatar being added.
	 * @param avatarPath The filepath of the avatar being added.
	 */
	public static void addDefaultAvatar(String avatarName, String avatarPath) {
		defaultAvatars.put(avatarName, avatarPath);
	}

	/**
	 * Gets the path of the avatar requested.
	 * 
	 * @param avatarName The name of the avatar to get the path of.
	 * @return The path to the avatar.
	 */
	public static String getDefaultAvatar(String avatarName) {
		return defaultAvatars.get(avatarName);
	}

	/**
	 * Returns an set of the names of all the default avatars.
	 * 
	 * @return an set of the names of all the default avatars.
	 */
	public static Set<String> getAvatarNames() {
		return defaultAvatars.keySet();
	}

	/**
	 * Get the user's username.
	 * 
	 * @return the user's username.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the first name of the user.
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @return the surname of the user.
	 */
	public String getSurname() {
		return this.surname;
	}

	/**
	 * @return the full name of the user.
	 */
	public String getName() {
		return this.firstName + " " + this.surname;
	}

	/**
	 * @return the mobile phone number of the user.
	 */
	public String getMobileNumber() {
		return this.mobileNumber;
	}

	/**
	 * @return the user's address.
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @return the path to the user's profile image.
	 */
	public String getProfileImagePath() {
		return "file:" + this.profileImagePath;
	}

	/**
	 * @param avatarName the name of the avatar to set the user's profile image
	 *                   to.
	 * @throws SQLException if SQL errors.
	 */
	public void setProfileImageToDefault(String avatarName)
		throws SQLException {
		setProfileImagePath(defaultAvatars.get(avatarName));
	}

	/**
	 * Generates the path for a custom profile image, sets the user's profile
	 * image path to use that path, and returns that path.
	 * 
	 * @param fileType the file extension being used.
	 * @return the path to the user's custom profile image.
	 * @throws SQLException if SQL errors.
	 */
	public String generateAndSetCustomProfileImage(String fileType)
		throws SQLException {
		// Generate the path
		String imagePath =
			"images/custom_avatars/" + this.UNIQUE_ID + "." + fileType;
		// Set the path
		setProfileImagePath(imagePath);
		// Return it
		return imagePath;
	}

	/**
	 * Sets the user's profile image path and updates the database.
	 * 
	 * @param path The path to set.
	 * @throws SQLException if SQL errors.
	 */
	private void setProfileImagePath(String path) throws SQLException {
		this.profileImagePath = path;
		Datastore.updateUserProfileImage(this.UNIQUE_ID, path);
	}

	/**
	 * Gets this user's unique ID.
	 * 
	 * @return this user's unique ID.
	 */
	public int getUniqueId() {
		return UNIQUE_ID;
	}

	/**
	 * Updates the modifiable parameters of the user.
	 * 
	 * @param username     the user's username.
	 * @param firstName    the user's first name.
	 * @param surname      the user's surname.
	 * @param mobileNumber the user's mobile number.
	 * @param address      the user's address.
	 * @throws SQLException             if SQL errors.
	 * @throws IllegalArgumentException if the username entered is already in
	 *                                  use
	 */
	public void editUser(String username, String firstName, String surname,
		String mobileNumber, String address)
		throws SQLException, IllegalArgumentException {
		// Check username is valid
		if (!username.equals(this.username)) {
			if (Datastore.getUsers().containsKey(username)) {
				throw new IllegalArgumentException(
					"That username is already in use!");
			} else {
				// Remove from the users hashmap
				Datastore.getUsers().remove(this.username);
				// Update username
				this.username = username;
				// Put back into the users hashmap
				Datastore.getUsers().put(this.username, this);
			}
		}
		// Change this object
		this.firstName = firstName;
		this.surname = surname;
		this.mobileNumber = mobileNumber;
		this.address = address;
		// Change in the database
		Datastore.editUser(this.UNIQUE_ID, this.username, this.firstName,
			this.surname, this.mobileNumber, this.address);
	}
}
