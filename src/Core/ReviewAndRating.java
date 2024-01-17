package Core;

/**
 * This class represents a review and rating of a resource given by a user
 * 
 * @author Sushil Kumar
 * @version 1.0
 */
public class ReviewAndRating {
	private String reviewString;
	private float ratingNumber;
	private Customer customer;
	private Resource resource;

	/**
	 * Initialises the review and rating.
	 * 
	 * @param reviewString The review in text written by the user.
	 * @param ratingNumber The rating given by user as floating point number.
	 * @param customer     The customer currently logged in.
	 * @param resource     The resource for which rating/review is given.
	 */
	public ReviewAndRating(String reviewString, float ratingNumber,
		Customer customer, Resource resource) {
		this.reviewString = reviewString;
		this.ratingNumber = ratingNumber;
		this.customer = customer;
		this.resource = resource;
	}

	/**
	 * This method returns the review in string format.
	 * 
	 * @return reviewString The review in text written by the user.
	 */
	public String getReviewString() {
		return this.reviewString;
	}

	/**
	 * This method returns the reference to the customer who gave the
	 * rating/review.
	 * 
	 * @return customer The customer currently logged in.
	 */
	public User getCustomer() {
		return this.customer;
	}

	/**
	 * This method returns the reference to the resource to which the
	 * rating/review is given to.
	 * 
	 * @return resource The resource for which rating/review is given.
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * This method returns the rating.
	 * 
	 * @return ratingNumber The rating given by customer as floating point
	 *         number.
	 */
	public float getRatingNumber() {
		return ratingNumber;
	}
}
