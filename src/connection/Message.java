package connection;

/**
 * Object to hold the destination and content of message
 *
 */
class Message {

	public static final int ALL_CLIENTS = -1;
	private String message;
	private int playerNo;

	/**
	 * 
	 * @param playerNo
	 *            Destination of message
	 * @param message
	 *            Message to send
	 */
	public Message(int playerNo, String message) {
		this.playerNo = playerNo;
		this.message = message;
	}

	/**
	 * Get the message to be sent
	 * 
	 * @return The message to be sent
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message to be sent
	 * 
	 * @param message
	 *            The message to be sent
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get the destination of the message (player number)
	 * 
	 * @return The number of the player receiving the message
	 */
	public int getPlayerNo() {
		return playerNo;
	}

	/**
	 * Set the destination of the message (player number)
	 * 
	 * @param playerNo
	 *            The destination of the message in the form of player number
	 */
	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

}
