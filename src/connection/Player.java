package connection;

import java.net.Socket;

public class Player extends Client {

	public Player(Socket client, Server server, int playerNo) {
		super(client, server, playerNo);
	}

}
