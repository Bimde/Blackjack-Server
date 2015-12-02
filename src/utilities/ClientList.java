package utilities;

import connection.Client;

public class ClientList {
	private ClientNode head, tail;

	public void add(int playerNo, Client temp) {
		ClientNode add = new ClientNode(playerNo, temp);
		if (this.head == null)
			this.head = this.tail = add;
		else {
			this.tail.setNext(add);
			this.tail = add;
		}
	}

	public int size() {
		ClientNode temp = this.head;
		int size = 0;
		while (temp != null) {
			size++;
			temp = temp.getNext();
		}
		return size;
	}
}
