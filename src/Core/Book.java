package Core;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * A class that represents a book.
 * 
 * @author Ali Alowais and Tyunay Kamber
 * @version 1.0
 */
public class Book extends Resource {

	private static final int FINE_PER_DAY = 2;
	private static final int MAX_FINE = 25;
	// variable initialisation
	private String author;
	private String publisher;
	private String genre;
	private String isbn;
	private String language;

	/**
	 * Constructor for a book.
	 * 
	 * @param uniqueId  the unique id of each book.
	 * @param title     the title of book.
	 * @param year      the year of book.
	 * @param author    the author of book.
	 * @param publisher the publisher of book.
	 * @param genre     the genre of book.
	 * @param isbn      the isbn of book.
	 * @param language  the language of book.
	 * @param additionDate the time the resource is created on.
	 */
	public Book(int uniqueId, String title, int year, String author,
		String publisher, String genre, String isbn, String language,LocalDateTime additionDate) {
		super(uniqueId, title, year,additionDate);
		this.author = author;
		this.publisher = publisher;
		this.genre = genre;
		this.isbn = isbn;
		this.language = language;
	}

	/**
	 * return the name of author.
	 * 
	 * @return author.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * return the publisher.
	 * 
	 * @return publisher.
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * return the genre of a book.
	 * 
	 * @return genre.
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * return the isbn of a book.
	 * 
	 * @return isbn.
	 */
	public String getIsbn() {
		return isbn;
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
	 * Sets this book's attributes to the given ones.
	 * @param title     the title of book.
	 * @param year      the year of book.
	 * @param author    the author of book.
	 * @param publisher the publisher of book.
	 * @param genre     the genre of book.
	 * @param isbn      the isbn of book.
	 * @param language  the language of book.
	 * @throws SQLException if SQL errors.
	 */
	public void editBook(String title, String year, String author,
		String publisher, String genre, String isbn, String language)
		throws SQLException {
		// Update the parent data
		this.editResource(title, year);
		// Update this data
		this.author = author;
		this.publisher = publisher;
		this.genre = genre;
		this.isbn = isbn;
		this.language = language;
		// Update datastore
		Datastore.updateBook(this.getUniqueID(), author, publisher, genre, isbn,
			language);

	}

	/**
	 * Provides details about the book as a String.
	 */
	@Override
	public String toString() {
		return "Book [author=" + author + ", publisher=" + publisher
			+ ", genre=" + genre + ", isbn=" + isbn + ", language=" + language
			+ "]";
	}

	/**
	 * Calculates the amount of fine due for this resource being overdue by the
	 * given number of days.
	 */
	@Override
	public int calculateFine(int daysOverdue) {
		return super.calculateFine(daysOverdue, Book.FINE_PER_DAY,
			Book.MAX_FINE);
	}

	@Override
	/**
	 * The type of the resource.
	 * @return "Book"
	 */
	public String getType() {
		return "Book";
	}
	
	public int getResourceCountValue() {
		return 1;
	}
}