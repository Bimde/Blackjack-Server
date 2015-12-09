package connection;

/**
 * Object to hold the destination and content of message.
 */
public class Message {
	public static final int ALL_CLIENTS = -1;
	private String message;
	private int playerNo;
	private int ignoredPlayer;

	/**
	 * Constructs a new Message object
	 * 
	 * @param playerNo
	 *            Player number of the receiver of the message, set to
	 *            Message.ALL_CLIENTS if all clients should receive message
	 * @param message
	 *            Message to be sent
	 */
	public Message(int playerNo, String message) {
		this.playerNo = playerNo;
		this.message = message;
		this.ignoredPlayer = -2;
	}

	/**
	 * Constructs a new Message object with a 'ALL_CLIENTS' flag and
	 * instructions to ignore a specific player
	 * 
	 * @param playerNo
	 *            Player number of the receiver of the message, set to
	 *            Message.ALL_CLIENTS if all clients should receive message
	 * @param ignoredPlayer
	 *            the player number associated with the player which to ignore
	 *            <br>
	 *            This is should only be used if the playerNo is set to
	 *            'Message.ALL_CLIENTS'
	 * @param message
	 *            the message to be sent
	 */
	public Message(int playerNo, int ignoredPlayer, String message) {
		this.playerNo = playerNo;
		this.message = message;
		this.ignoredPlayer = ignoredPlayer;
	}

	/**
	 * Get message to be sent
	 * 
	 * @return Message to be sent
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Getter for ignored player
	 * 
	 * @return Player number associated with player which to not send this
	 *         message to
	 */
	public int getIgnoredPlayer() {
		return this.ignoredPlayer;
	}

	/**
	 * Get the destination of the message (player number)
	 * 
	 * @return the player number of the receiver of the message
	 */
	public int getPlayerNo() {
		return playerNo;
	}
}