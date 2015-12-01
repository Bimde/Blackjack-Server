import java.net.Socket;

public class Client implements Runnable {

	private Server server;
	private Socket client;

	public Client(Socket client, Server server) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {
	}

}
