package connection;

public class Message {

	public static final int ALL_PLAYERS = -1;
	public String message;
	public int player;

	public Message(int player, String message) {
		this.player = player;
		this.message = message;
	}
}
