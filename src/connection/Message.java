package connection;

/**
 * Object to hold the destination and content of message.
 */
class Message {
	public static final int ALL_CLIENTS = -1;
	private String message;
	private int playerNo;

	/**
	 * Constructs a new Message object.
	 * 
	 * @param playerNo
	 *            the player number of the receiver of the message.
	 * @param message
	 *            the message to be sent.
	 */
	public Message(int playerNo, String message) {
		this.playerNo = playerNo;
		this.message = message;
	}

	/**
	 * Get the message to be sent.
	 * 
	 * @return the message to be sent.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message to be sent.
	 * 
	 * @param message
	 *            the new message to be sent.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get the destination of the message (player number).
	 * 
	 * @return the player number of the receiver of the message.
	 */
	public int getPlayerNo() {
		return playerNo;
	}

	/**
	 * Set the destination of the message (player number).
	 * 
	 * @param playerNo
	 *            the new player number of the receiver of the message.
	 */
	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}
}