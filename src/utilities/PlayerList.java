package utilities;

import connection.Player;

/**
 * Linked list containing Client objects to store and retrieve players based on
 * their 'playerNo' parameter
 *
 */
public class PlayerList {
	private PlayerNode head, tail;

	/**
	 * Creates a new linked list containing Client objects
	 */
	public PlayerList() {
	}

	/**
	 * Adds specified client to list
	 * 
	 * @param temp
	 *            Client to add to list
	 */
	public void add(Player temp) {
		PlayerNode add = new PlayerNode(this.findEmptyPlayerNo(), temp);
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
		PlayerNode temp = this.head;
		int lastNo = 0;
		while (temp != null) {
			int nextNo = temp.getPlayerNo();

			// If the difference between the player numbers of clients that are
			// next to each other is greater than 1, then there is a
			// gap in the player numbers
			if (nextNo > lastNo + 1)
				return lastNo + 1;
			lastNo = nextNo;
			temp = temp.getNext();
		}

		// If no gaps are found, returns one more than the last client in the
		// list's player number
		return lastNo + 1;
	}

	/**
	 * Counts number of Client objects in list
	 * 
	 * @return Number of clients in list
	 */
	public int size() {
		PlayerNode temp = this.head;
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
	public Player get(int playerNo) {
		PlayerNode temp = this.head;
		while (temp != null) {
			if (temp.getPlayerNo() == playerNo)
				return temp.getPlayer();
			temp = temp.getNext();
		}
		return null;
	}
}
