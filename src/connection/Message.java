package connection;

/**
 * Object to hold destination and content of message
 *
 */
class Message {

	/**
	 * Used to flag a message which is to be sent to all players
	 */
	public static final int ALL_CLIENTS = -1;

	/**
	 * Content of the message
	 */
	public String data;

	/**
	 * Player to send the message to
	 */
	public int playerNo;

	/**
	 * 
	 * @param playerNo
	 *            Destination of message
	 * @param message
	 *            Message to send
	 */
	public Message(int playerNo, String message) {
		this.playerNo = playerNo;
		this.data = message;
	}
}
