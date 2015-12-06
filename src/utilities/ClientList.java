package utilities;

import java.util.Iterator;

import connection.Client;

/**
 * Linked list containing Client objects to store and retrieve players based on
 * their 'playerNo' parameter
 *
 */
public class ClientList implements Iterable<Client> {
	private ClientNode head, tail;

	/**
	 * Constructs a new ClientList object containing Client objects.
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
		ClientNode temp = this.head;
		while (temp != null) {

			if ((temp.getClient().getPlayer().getPlayerNo() == playerNo))
				return temp.getClient();
			temp = temp.getNext();
		}
		return null;
	}

	/**
	 * Removes a Client object from the list.
	 * 
	 * @param client
	 *            the client to remove.
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
	 * Removes a Client object from the list.
	 * 
	 * @param client
	 *            the client to remove.
	 */
	private void remove(ClientNode client) {
		if (client != null) {
			if (this.head == client) {
				this.head = client.getNext();
				if (this.head != null)
					this.head.setPrevious(null);
			} else {
				client.getPrevious().setNext(client.getNext());
				client.getNext().setPrevious(client.getPrevious());
			}
		}
	}

	/**
	 * Allows for use with a for-each loop
	 */
	@Override
	public Iterator<Client> iterator() {
		return new Iterator<Client>() {
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
				return null;
			}
		};
	}
}