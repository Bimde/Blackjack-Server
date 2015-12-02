package utilities;

import connection.Client;

public class ClientNode {

	private int playerNo;
	private Client client;
	private ClientNode next;

	public ClientNode(int playerNo, Client client) {
		this.client = client;
		this.playerNo = playerNo;
	}

	public int getPlayerNo() {
		return this.playerNo;
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
