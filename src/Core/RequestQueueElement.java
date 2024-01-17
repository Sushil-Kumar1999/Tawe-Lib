package Core;
/**
 * This class represents a link in the queue. The queue is composed of these
 * links.
 * 
 * @author Sushil Kumar
 * @version 1.0
 */

public class RequestQueueElement<T> {
	private T element;
	private RequestQueueElement<T> next;

	public RequestQueueElement(T e, RequestQueueElement<T> n) {
		this.element = e;
		this.next = n;
	}

	/**
	 * Method to get the element.
	 * 
	 * @return Element in the link
	 */
	public T getElement() {
		return element;
	}

	/**
	 * Method to set the element
	 * 
	 * @param element The element to be referenced in the link
	 */
	public void setElement(T element) {
		this.element = element;
	}

	/**
	 * Method to get the next queue element
	 * 
	 * @return The next element in queue
	 */
	public RequestQueueElement<T> getNext() {
		return next;
	}

	/**
	 * Method to set the next queue element
	 * 
	 * @param next Element to be set as next element in the queue
	 */
	public void setNext(RequestQueueElement<T> next) {
		this.next = next;
	}

}
