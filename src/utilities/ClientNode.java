package utilities;

import connection.Client;

public class ClientNode {

	private Client client;
	private ClientNode next;

	public ClientNode(Client client) {
		this.client = client;
	}

	public void setNext(ClientNode next) {
		this.next = next;
	}

	public Client getClient() {
		return this.client;
	}

	public ClientNode getNext() {
		return this.next;
	}

}
