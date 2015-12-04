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
	private String message;

	/**
	 * Player to send the message to
	 */
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}
	
	
	
}
