package Core;

import java.lang.RuntimeException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * A class represents a resource.
 * 
 * @author Ali Alowais and kamber
 * @version 1.0
 */

public abstract class Resource {
	// variable initialisation.
	private final int UNIQUE_ID;
	private String title;
	private int year;
	private ArrayList<Copy> copies;
	private RequestQueue<Customer> requests;
	private ArrayList<ReviewAndRating> reviewsAndRatings =
		new ArrayList<ReviewAndRating>();

	private LocalDateTime additionDate = null;

	/**
	 * Constructor for a resource.
	 *
	 * @param uniqueID     unique id for each resources.
	 * @param title        the title for every resources.
	 * @param year         the year of all resources.
	 * @param additionDate the time the resource is created on.
	 */
	public Resource(int uniqueID, String title, int year,
		LocalDateTime additionDate) {
		this.setAdditionDate(additionDate);

		this.UNIQUE_ID = uniqueID;
		this.title = title;
		this.year = year;
		copies = new ArrayList<Copy>();
		requests = new RequestQueue<Customer>();
	}

	// Method which returns 3 for laptop and 1 for all other resources
	public abstract int getResourceCountValue();

	/**
	 * Checks whether this resource has any requests.
	 * 
	 * @return whether this resource has any requests.
	 */
	public boolean isRequested() {
		return requests.isEmpty();
	}

	/**
	 * Calculates the fine amount based on the number of days the item was
	 * overdue, the finePerDay amount for the resource type, and the maximum
	 * fine that can be accrued on that resource type.
	 * 
	 * @param daysOverdue The number of days the copy is overdue.
	 * @param finePerDay  The amount the fine increases per day overdue.
	 * @param maxFine     The maximum amount the fine can increase to.
	 * @return The amount of fine owed for this copy.
	 */
	protected static int calculateFine(int daysOverdue, int finePerDay,
		int maxFine) {
		int fineOwed = finePerDay * daysOverdue;
		if (fineOwed >= maxFine) {
			fineOwed = maxFine;
		}
		return fineOwed;
	}

	/**
	 * Calculates the fine for a copy of a specific resource type. Due to
	 * limitations of Java, this method can't be static.
	 * 
	 * @param daysOverdue the number of days the copy is overdue.
	 * @return The amount of fine owed for this copy.
	 */
	public abstract int calculateFine(int daysOverdue);

	/**
	 * Returns the next customer in the request queue. Also removes the
	 * customer from the queue.
	 * 
	 * @throws RuntimeException if there are no requests in the queue.
	 * @return customer The customer who has requested a resource.
	 * @throws SQLException if SQL errors.
	 */
	public Customer nextRequest() throws RuntimeException, SQLException {
		if (requests.isEmpty()) {
			throw new RuntimeException("There are no more requests");
		}
		Customer customer = requests.peek();
		requests.dequeue();
		// remove request from database
		Datastore.deleteResourceRequest(this.UNIQUE_ID,
			customer.getUniqueId());
		return customer;
	}

	/**
	 * Returns the unique id.
	 * 
	 * @return uniqueId.
	 */
	public int getUniqueID() {
		return UNIQUE_ID;
	}

	/**
	 * return the title.
	 * 
	 * @return title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * return the year.
	 * 
	 * @return year.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * return the path to the resource's thumbnail image.
	 * 
	 * @return thumbnailImage path.
	 */
	public String getThumbnailImagePath() {
		return Resource.generateThumbnailImagePath(this.UNIQUE_ID);
	}

	/**
	 * Gets the thumbnail image path of a resource with a given ID.
	 * 
	 * @param resourcesId the id of the resource to generate the path for.
	 * @return The path to the thumbnail of the resource.
	 */
	public static String generateThumbnailImagePath(int resourcesId) {
		return "images/resource_thumbs/" + resourcesId + ".jpg";
	}

	/**
	 * Returns the arraylist of copies.
	 * 
	 * @return copies.
	 */
	public ArrayList<Copy> getCopies() {
		return copies;
	}

	/**
	 * imports the given arraylist of copies without additional checks.
	 * 
	 * @param copies the collection of copies to import.
	 */
	public void importCopies(ArrayList<Copy> copies) {
		this.copies = copies;
	}

	/**
	 * Returns the requestQueue of requests for this resource.
	 * 
	 * @return the requestQueue of requests for this resource.
	 */
	public RequestQueue<Customer> getRequests() {
		return requests;
	}

	/**
	 * Imports a queue of requests from the database without any additional
	 * checks.
	 * 
	 * @param requests the collection of requests to set.
	 */
	public void importRequests(RequestQueue<Customer> requests) {
		this.requests = requests;
	}

	/**
	 * The customer requests the resource.
	 * 
	 * @param requestee The customer requesting the resource.
	 * @return null if the resource is requested; the reserved copy if a copy
	 *         is available to reserve.
	 * @throws IllegalArgumentException if the customer already has this
	 *                                  resource or a copy of this resource
	 *                                  reserved.
	 * @throws SQLException             if SQL errors.
	 */
	public Copy addRequest(Customer requestee)
		throws IllegalArgumentException, SQLException {
		// Check if customer can request or reserve a copy of this resource
		if (requestee.isResourceRequested(this)
			|| requestee.isResourceReserved(this)) {
			throw new IllegalArgumentException(
				"You've already requested this resource!");
		} else {
			// First check if any copies are available; if so, reserve
			Copy reserveCopy = this.reserve(requestee);
			// Otherwise, Request
			if (reserveCopy == null) {
				this.request(requestee);
			}
			// Return a copy if reserved, null otherwise
			return reserveCopy;
		}
	}

	/**
	 * Reserves a copy of this resource for a customer if a copy is available
	 * to be reserved. If no copies are available to be reserved, returns null.
	 * 
	 * @param requestee the customer reserving a copy of the resource.
	 * @return the copy reserved, or null if no copies are available.
	 * @throws SQLException if SQL errors.
	 */
	private Copy reserve(Customer requestee) throws SQLException {
		// Loop through copies, check whether they're available
		for (Copy searchCopy : copies) {
			if (searchCopy.isAvailable()) {
				// Reserve available copy
				searchCopy.reserve(requestee);
				requestee.reserve(searchCopy);
				// Return the reserved copy
				return searchCopy;
			}
		}
		// If no copies were available return null
		return null;
	}

	/**
	 * Requests this resource. When a customer requests a resource,
	 * 'addRequest' should be used - this method simply performs the request
	 * side of that function.
	 * 
	 * @param requestee The customer requesting the resource.
	 * @throws SQLException if the commit fails.
	 */
	private void request(Customer requestee) throws SQLException {
		// Request
		requestee.request(this);
		this.requests.enqueue(requestee);
		// Update database
		Datastore.insertResourceRequest(this, requestee);

		/*
		 * Update copies' due date. Loop through all copies until one without a
		 * duedate is found; set duedate of that copy. If no copies already
		 * have their duedate set, do nothing.
		 */
		int i = copies.size();
		boolean found = false;
		Copy searchCopy = null;
		while (i > 0 && !found) {
			i--;
			searchCopy = copies.get(i);
			if (searchCopy.getDueDate() == null) {
				searchCopy.setDueDate();
				found = true;
			}
		}
	}

	/**
	 * @return The number of requests for the resource.
	 */
	public int getNumRequests() {
		return requests.getLength();
	}

	/**
	 * @return Whether there are more requests for the resource than the number
	 *         of its copies.
	 */
	public boolean moreRequestsThanCopies() {
		return (this.getNumRequests() >= copies.size());
	}

	/**
	 * Returns a string representation of the resource.
	 */
	@Override
	public String toString() {
		return "Resource [uniqueId = " + UNIQUE_ID + ", title = " + title
			+ ", year = " + year + ", image = " + this.getThumbnailImagePath()
			+ "]";
	}

	/**
	 * Removes the given copy from the system, if it's a copy of this resource.
	 * 
	 * @param copyRef the copy in question.
	 * @return whether the copy was part of this resource (and so, removed).
	 * @throws SQLException if SQL errors.
	 */
	public boolean removeCopy(Copy copyRef) throws SQLException {
		// If copy is for this resource, remove it
		if (copies.remove(copyRef)) {
			// and delete the copy from the database
			Datastore.deleteCopy(copyRef);
			return true;
		}
		// Otherwise return false
		else {
			return false;
		}
	}

	/**
	 * Creates the specified number of copies of this resource and adds them to
	 * the database and this resource's collection of copies.
	 * 
	 * @param numCopies the number of copies to add.
	 * @throws SQLException if SQL errors.
	 */
	public void addCopies(int numCopies) throws SQLException {
		// Add the number of copies specified
		for (int x = 0; x < numCopies; x++) {
			// Add the copy to the database and get its id
			int copyId = Datastore.insertCopy(this.UNIQUE_ID);
			// Add the copy to the collection of copies
			copies.add(new Copy(copyId, this));
		}
	}

	/**
	 * Updates the editable parameters of this resource and writes them to the
	 * database.
	 * 
	 * @param title title of the resource.
	 * @param year  year the resource was released.
	 * @throws SQLException if SQL errors.
	 */
	public void editResource(String title, String year) throws SQLException {
		this.title = title;
		this.year = Integer.parseInt(year);
		// Update database
		Datastore.updateResource(this.UNIQUE_ID, title, year);

	}

	/**
	 * This method adds a edited review and rating object to the arraylist of
	 * reviewsAndRating possessed by both the selected resource and current
	 * customer
	 * 
	 * @author Sushil Kumar
	 * @param selectedResource The resource selected to rate or review by the
	 *                         customer.
	 * @param currentCustomer  The customer who gave the rating or review.
	 * @param ratingNumber     The rating given by the customer.
	 * @param reviewString     The review given by the customer.
	 * @throws SQLException if SQL errors
	 */
	public void editReviewAndRating(Resource selectedResource,
		Customer currentCustomer, float ratingNumber, String reviewString)
		throws SQLException {
		int customerRefID = currentCustomer.getUniqueId();
		int resourceRefID = selectedResource.getUniqueID();
		Datastore.updateReviewAndRating(ratingNumber, reviewString,
			resourceRefID, customerRefID);
		ReviewAndRating editedReviewAndRating = new ReviewAndRating(
			reviewString, ratingNumber, currentCustomer, selectedResource);

		int resourceIndex = 0;
		// int customerIndex = 0;
		for (ReviewAndRating elem : selectedResource.getReviewsAndRatings()) {
			if (elem.getCustomer() == currentCustomer
				&& elem.getResource() == selectedResource) {
				resourceIndex =
					selectedResource.getReviewsAndRatings().indexOf(elem);
			}
		}
		/*
		 * for (ReviewAndRating elem : currentCustomer.getReviewsAndRatings())
		 * { if (elem.getCustomer() == currentCustomer && elem.getResource() ==
		 * selectedResource) { customerIndex =
		 * currentCustomer.getReviewsAndRatings(). indexOf(elem); } }
		 */

		selectedResource.getReviewsAndRatings().remove(resourceIndex);
		// currentCustomer.getReviewsAndRatings().remove(customerIndex);
		selectedResource.getReviewsAndRatings().add(editedReviewAndRating);
		// currentCustomer.getReviewsAndRatings().add(editedReviewAndRating);
	}

	/**
	 * @return The type of the resource
	 */
	public abstract String getType();

	/**
	 * @author Sushil Kumar
	 * @return the reviewAndRatings An arraylist of reviews and ratings for
	 *         this resource made by various users
	 */
	public ArrayList<ReviewAndRating> getReviewsAndRatings() {
		return reviewsAndRatings;
	}

	/**
	 * This method sets the arraylist of reviewAndRating
	 * 
	 * @author Sushil Kumar
	 * @param reviewsAndRatings the collection of reviews and ratings of this
	 *                          resource.
	 */
	public void
		setReviewsAndRatings(ArrayList<ReviewAndRating> reviewsAndRatings) {
		this.reviewsAndRatings = reviewsAndRatings;
	}

	/**
	 * This method adds a reviewAndRating object to the arraylist of
	 * reviewsAndRating possessed by both the selected resource and current
	 * customer
	 * 
	 * @author Sushil Kumar
	 * @param selectedResource The resource selected to rate or review by the
	 *                         customer.
	 * @param currentCustomer  The customer who gave the rating or review.
	 * @param ratingNumber     The rating given by the customer.
	 * @param reviewString     The review given by the customer.
	 * @throws SQLException if SQL errors
	 */
	public void addReviewAndRating(Resource selectedResource,
		Customer currentCustomer, float ratingNumber, String reviewString)
		throws SQLException {

		int resourceRefID = selectedResource.getUniqueID();
		int customerRefID = currentCustomer.getUniqueId();
		Datastore.insertReviewAndRating(resourceRefID, customerRefID,
			ratingNumber, reviewString);
		ReviewAndRating reviewAndRating = new ReviewAndRating(reviewString,
			ratingNumber, currentCustomer, selectedResource);
		selectedResource.getReviewsAndRatings().add(reviewAndRating);
		currentCustomer.getReviewsAndRatings().add(reviewAndRating);
	}

	/**
	 * @return the additionDate
	 */
	public LocalDateTime getAdditionDate() {
		return additionDate;
	}

	/**
	 * @param additionDate the additionDate to set
	 */
	public void setAdditionDate(LocalDateTime additionDate) {
		this.additionDate = additionDate;
	}
}
