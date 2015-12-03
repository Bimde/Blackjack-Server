package utilities;

import connection.Client;

public class ClientList {
	private ClientNode head, tail;

	/**
	 * Creates a new linked list containing Client objects
	 */
	public ClientList() {
	}

	/**
	 * Adds specified client to list
	 * 
	 * @param temp
	 *            Client to add to list
	 */
	public void add(Client temp) {
		ClientNode add = new ClientNode(this.findEmptyPlayerNo(), temp);
		if (this.head == null)
			this.head = this.tail = add;
		else {
			this.tail.setNext(add);
			this.tail = add;
		}
	}

	/**
	 * Finds the next available player number
	 * 
	 * @return Next available player number
	 */
	private int findEmptyPlayerNo() {
		ClientNode temp = this.head;
		int lastNo = 0;
		while (temp != null) {
			int nextNo = temp.getPlayerNo();
			if (nextNo > lastNo + 1)
				return lastNo + 1;
			lastNo = nextNo;
			temp = temp.getNext();
		}
		return lastNo + 1;
	}

	/**
	 * Counts number of Client objects in list
	 * 
	 * @return Number of clients in list
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
	 * @return Client associated with specified player number, or null or not
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
