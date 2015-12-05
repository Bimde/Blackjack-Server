package utilities;

import connection.Client;

public class ClientNode {

	private Client client;
	private ClientNode next, previous;

	public ClientNode(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return this.client;
	}

	public void setNext(ClientNode next) {
		this.next = next;
	}

	public ClientNode getNext() {
		return this.next;
	}

	public void setPrevious(ClientNode previous) {
		this.previous = previous;
	}

	public ClientNode getPrevious() {
		return this.previous;
	}

}
