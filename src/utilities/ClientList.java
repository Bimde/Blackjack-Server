package utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import connection.Client;

/**
 * Linked list containing Client objects to store and retrieve players based on
 * their 'playerNo' parameter
 *
 */
public class ClientList implements Iterable<Client> {

	/**
	 * Links to first and last nodes in list.
	 */
	private ClientNode head, tail;

	/**
	 * Constructs new ClientList object containing Client objects.
	 */
	public ClientList() {
	}

	/**
	 * Adds a specified client to the list.
	 * 
	 * @param temp
	 *            the client to add to the list.
	 */
	public void add(Client temp) {
		ClientNode add = new ClientNode(temp);
		if (this.head == null)
			this.head = this.tail = add;
		else {
			this.tail.setNext(add);
			add.setPrevious(this.tail);
			this.tail = add;
		}
	}

	/**
	 * Counts the number of Client objects in the list.
	 * 
	 * @return the number of Clients in the list.
	 */
	public int size() {
		ClientNode temp = this.head;
		int size = 0;
		while (temp != null) {
			size++;
			temp = temp.getNext();
		}
		return size;
	}

	/**
	 * Gets a Client object from the list given its player number.
	 * 
	 * @param playerNo
	 *            the number associated with the desired client
	 * @return the client associated with specified player number. If the player
	 *         is not found, returns null.
	 */
	public Client get(int playerNo) {
		ClientNode temp = this.getNode(playerNo);
		if (temp != null)
			return temp.getClient();
		return null;
	}

	/**
	 * Gets a Client object from the list given its player number.
	 * 
	 * @param playerNo
	 *            the number associated with the desired client.
	 * @return the client associated with specified player number. If the player
	 *         is not found, returns null.
	 */
	private ClientNode getNode(int playerNo) {
		ClientNode temp = this.head;
		while (temp != null) {
			if ((temp.getClient().getPlayer().getPlayerNo() == playerNo))
				return temp;
			temp = temp.getNext();
		}
		return null;
	}

	/**
	 * Removes specified Client object from list.
	 * 
	 * @param client
	 *            the Client object to remove.
	 */
	public void remove(Client client) {
		ClientNode temp = this.head;
		while (temp != null) {
			if (temp.getClient() == client)
				this.remove(temp);
			temp = temp.getNext();
		}
	}

	/**
	 * Removes Client object associated with specified playerNo from list.
	 * 
	 * @param client
	 *            the player number to remove.
	 */
	public void remove(int playerNo) {
		this.remove(this.getNode(playerNo));
	}

	/**
	 * *** For internal use only, as there is no external access to nodes ***
	 * Removes node from the list.
	 * 
	 * @param client
	 *            the Client to remove.
	 */
	private void remove(ClientNode client) {

		// Removes and updates all links to node
		if (client != null) {
			if (this.head == client)
				this.head = client.getNext();
			if (client == this.tail)
				this.tail = client.getPrevious();
			if (client.getPrevious() != null)
				client.getPrevious().setNext(client.getNext());
			if (client.getNext() != null)
				client.getNext().setPrevious(client.getPrevious());
		}
	}

	/**
	 * Creates a new iterator using the Client objects contained in the list's
	 * nodes. <br>
	 * Allows for use with a for-each loop.
	 */
	@Override
	public Iterator<Client> iterator() {
		return new Iterator<Client>() {

			/**
			 * Sets the first element of the iterator to the head of the list
			 */
			private ClientNode current = ClientList.this.head;

			@Override
			public boolean hasNext() {
				return this.current != null;
			}

			@Override
			public Client next() {
				if (this.hasNext()) {
					Client temp = this.current.getClient();
					this.current = this.current.getNext();
					return temp;
				}
				throw new NoSuchElementException("No more elements in the list");
			}
		};
	}

	/**
	 * Returns a string representation of the list using the following format:
	 * '[client1{@link #Client.toString() .toString()}, client2
	 * {@link #Client.toString() .toString()}, ..., clientn
	 * {@link #Client.toString() .toString()}]'
	 */
	@Override
	public String toString() {
		String rep = "[";
		ClientNode temp = this.head;
		while (temp != null) {
			rep += temp.getClient().toString() + ", ";
			temp = temp.getNext();
		}

		// Returns '[]' for an empty list or removes the last ',' character and
		// trailing space and adds the closing ']' otherwise
		return rep.length() <= 1 ? "[]" : rep.substring(0, rep.length() - 2)
				+ "]";
	}
}