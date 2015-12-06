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
	 * Created a new node containing the specified client object
	 * 
	 * @param client
	 */
	public ClientNode(Client client) {
		this.client = client;
	}

	/**
	 * Getter for the contained Client object
	 * 
	 * @return Contained Client object
	 */
	public Client getClient() {
		return this.client;
	}

	/**
	 * Setter for following node
	 * 
	 * @param next
	 *            Node to set as following
	 */
	public void setNext(ClientNode next) {
		this.next = next;
	}

	/**
	 * Getter for following node
	 * 
	 * @return Following node
	 */
	public ClientNode getNext() {
		return this.next;
	}

	/**
	 * Setter for preceding node
	 * 
	 * @param previous
	 *            Node to set as preceding
	 */
	public void setPrevious(ClientNode previous) {
		this.previous = previous;
	}

	/**
	 * Getter for preceding node
	 * 
	 * @return Preceding node
	 */
	public ClientNode getPrevious() {
		return this.previous;
	}

}
