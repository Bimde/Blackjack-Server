package connection;

/**
 * Object to hold the destination and content of message.
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 3, 2015
 */
public class Message {
	
	/**
	 * Constant variable to represent sending a message to every client (-1).
	 */
	public static final int ALL_CLIENTS = -1;
	
	private String message;
	private int playerNo;
	private int ignoredPlayer;

	/**
	 * Constructor for a new Message object.
	 * 
	 * @param playerNo
	 *            the player number of the receiver of the message. Set to
	 *            Message.ALL_CLIENTS if all clients should receive message.
	 * @param message
	 *            the message to be sent.
	 */
	public Message(int playerNo, String message) {
		this.playerNo = playerNo;
		this.message = message;
		this.ignoredPlayer = -2;
	}

	/**
	 * Constructor for a new Message object with a 'ALL_CLIENTS' flag and
	 * instructions to ignore a specific player.
	 * 
	 * @param playerNo
	 *            the player number of the receiver of the message. Set to
	 *            Message.ALL_CLIENTS if all clients should receive message.
	 * @param ignoredPlayer
	 *            the player number associated with the player which to ignore. <br>
	 *            This is should only be used if the playerNo is set to
	 *            'Message.ALL_CLIENTS'.
	 * @param message
	 *            the message to be sent.
	 */
	public Message(int playerNo, int ignoredPlayer, String message) {
		this.playerNo = playerNo;
		this.message = message;
		this.ignoredPlayer = ignoredPlayer;
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
	 * Getter for ignored player.
	 * 
	 * @return player number associated with player which to not send this
	 *         message to.
	 */
	public int getIgnoredPlayer() {
		return this.ignoredPlayer;
	}

	/**
	 * Get the destination of the message (player number).
	 * 
	 * @return the player number of the receiver of the message.
	 */
	public int getPlayerNo() {
		return playerNo;
	}
}