package connection;


public class Message {

	public static final int ALL_PLAYERS = -1;
	public String data;
	public int playerNo;

	public Message(int player, String message) {
		this.playerNo = player;
		this.data = message;
	}
}
