package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
	private Server server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	private int playerNo, gameMode, coins;
	private boolean connected = true;

	public Client(Socket client, Server server, int playerNo) {
		this.socket = client;
		this.server = server;
		this.playerNo = playerNo;
		this.coins = Server.START_COINS;
	}

	@Override
	public void run() {
		try {
			this.out = new PrintWriter(this.socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error getting client's output stream");
			e.printStackTrace();
			this.connected = false;
		}
		try {
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Error getting client's input stream");
			e.printStackTrace();
			this.connected = false;
		}
		try {
			this.name = in.readLine();
		} catch (IOException e) {
			System.out.println("Error getting client's name");
			e.printStackTrace();
			this.connected = false;
		}

		this.gameMode = -1;
		try {
			while (this.gameMode == -1) {
				if (this.in.readLine().equals("PLAY")) {
					if (this.server.gameStarted()) {
						this.gameMode = 1;
						this.out.println("% LATE");
						this.out.flush();
					} else if (server.isFull()) {
						this.out.println("% FULL");
						this.out.flush();
					} else {
						this.gameMode = 0;
						this.out.println("% ACCEPTED");
						this.out.flush();
						this.server.newPlayer(this.playerNo, this.name);
					}
				} else {
					this.gameMode = 1;
					this.out.println("% ACCEPTED");
					this.out.flush();
				}
			}
		} catch (IOException e) {
			System.out.println("Error getting the client's game mode");
			e.printStackTrace();
			this.gameMode = 0;
		}

		try {
			if (in.readLine().equals("READY")) {
				server.ready(this.playerNo);
			}
		} catch (IOException e) {
			System.out.println("Error getting the \"ready\" status of the player");
			e.printStackTrace();
		}
	}

	public void broadcast(String message) {
		this.out.println(message);
		this.out.flush();
	}

	protected Socket getSocket() {
		return this.socket;
	}

	public String getName() {
		return this.name;
	}
}