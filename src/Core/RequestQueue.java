package Core;
/**
 * A class that implements a queue.
 * 
 * @author Sushil Kumar
 * @version 1.0
 */
public class RequestQueue<T> {
	private RequestQueueElement<T> head;
	private RequestQueueElement<T> tail;
	private int length;

	/**
	 * Constructs an empty Queue.
	 */
	public RequestQueue() {
		head = null;
		tail = null;
	}

	/**
	 * Returns element at the top of the queue
	 * 
	 * @return head The head of the queue
	 */
	public RequestQueueElement<T> getHead() {
		return head;
	}

	/**
	 * Returns element at the bottom of the queue
	 * 
	 * @return tail The tail of the queue
	 */
	public RequestQueueElement<T> getTail() {
		return tail;
	}

	/**
	 * Returns the queue's current length.
	 * 
	 * @return the queue's current length.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Returns true if the queue is empty
	 * 
	 * @return True if queue is empty otherwise false
	 */
	public boolean isEmpty() {
		return ((head == null) && (tail == null));
	}

	/**
	 * Returns the element at the head of the queue
	 * 
	 * @return Element at front of the queue
	 */
	public T peek() {
		return head.getElement();
	}

	/**
	 * Puts an element at the back of the queue.
	 * 
	 * @param element The element to add to the queue
	 */
	public void enqueue(T element) {
		RequestQueueElement<T> newElement =
			new RequestQueueElement<T>(element, null);
		// If the queue isn't empty
		if (!(this.tail == null)) {
			this.tail.setNext(newElement);
		}
		// If the queue is empty
		else {
			this.head = newElement;
		}
		this.tail = newElement;
		length++;
	}

	/**
	 * Removes an element from the front of the queue.
	 */
	public void dequeue() {
		head = head.getNext();
		if (head == null) {
			tail = null;
		}
		if (!(length <= 0)) {
			length--;
		}
	}
}
