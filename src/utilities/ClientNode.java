package utilities;

import connection.Client;

/**
 * Node used for the 'ClientList' which stores 'Client' object
 */
class ClientNode {

	/**
	 * Contained Client object
	 */
	private Client client;

	/**
	 * Nodes used to traverse through list
	 */
	private ClientNode next, previous;

	/**
	 * Constructs a new ClientNode object containing the specified client
	 * object.
	 * 
	 * @param client
	 *            the client to put in the node.
	 */
	public ClientNode(Client client) {
		this.client = client;
	}

	/**
	 * Getter for the contained Client object.
	 * 
	 * @return the contained Client object.
	 */
	public Client getClient() {
		return this.client;
	}

	/**
	 * Setter for the following node.
	 * 
	 * @param next
	 *            the new following node.
	 */
	public void setNext(ClientNode next) {
		this.next = next;
	}

	/**
	 * Getter for the following node.
	 * 
	 * @return the following node.
	 */
	public ClientNode getNext() {
		return this.next;
	}

	/**
	 * Setter for the preceding node.
	 * 
	 * @param previous
	 *            the new preceding node.
	 */
	public void setPrevious(ClientNode previous) {
		this.previous = previous;
	}

	/**
	 * Getter for the preceding node.
	 * 
	 * @return the preceding node.
	 */
	public ClientNode getPrevious() {
		return this.previous;
	}
}