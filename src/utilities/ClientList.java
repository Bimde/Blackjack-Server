package utilities;

import connection.Client;

public class ClientList {
	private ClientNode head, tail;
	private int playerNo;

	/**
	 * Creates a new linked list containing Client objects Starts the client
	 * numbers from 1
	 */
	public ClientList() {
		this.playerNo = 1;
	}

	/**
	 * Add a new Client to the list
	 * 
	 * @param temp
	 */
	public void add(Client temp) {
		ClientNode add = new ClientNode(this.playerNo, temp);
		if (this.head == null)
			this.head = this.tail = add;
		else {
			this.tail.setNext(add);
			this.tail = add;
		}
		this.playerNo++;
	}

	/**
	 * Counts the number of Client objects in the list
	 * 
	 * @return The number of clients in the list
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
	 * 
	 * @param playerNo
	 *            The number associated with the desired client
	 * @return Client associated with specified player number, or -1 or not
	 *         found
	 */
	public Client get(int playerNo) {
		ClientNode temp = this.head;
		while (temp != null) {
			if (temp.getPlayerNo() == playerNo)
				return temp.getClient();
			temp = temp.getNext();
		}
		return null;
	}
}
