package connection;

import java.net.Socket;

public class Spectator extends Client {

	public Spectator(Socket client, Server server, int playerNo) {
		super(client, server, playerNo);
	}

}
