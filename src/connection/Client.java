package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private String name;
	private boolean connected;

	private Server server;

	// Potential Player or Spectator object
	private Player player;
	private Spectator spectator;

	// 'U' for unassigned, 'P' for player and 'S' for spectator
	private char userType;

	public Client(Socket client, Server server) {
		this.socket = client;
		this.connected = true;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			this.output = new PrintWriter(this.socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error getting client's output stream");
			e.printStackTrace();
			this.connected = false;
		}
		try {
			this.input = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Error getting client's input stream");
			e.printStackTrace();
			this.connected = false;
		}
		try {
			this.name = input.readLine();
		} catch (IOException e) {
			System.out.println("Error getting client's name");
			e.printStackTrace();
			this.connected = false;
		}

		// The user is unassigned at first
		this.userType = 'U';

		try {
			while (this.userType == 'U') {
				String message = input.readLine();

				if (message.equalsIgnoreCase("PLAY")) {
					if (server.gameStarted()) {
						this.userType = 'S';
						this.output.println("% LATE");
						this.output.flush();
					} else if (server.isFull()) {
						this.output.println("% FULL");
						this.output.flush();
					} else {
						this.userType = 'P';
						this.output.println("% ACCEPTED");
						this.output.flush();
						int playerNumber = server.returnAndUsePlayerNumber();

						player = new Player(server, playerNumber);
						server.broadcast("@ " + playerNumber + " " + name);
					}
				} else {
					this.userType = 'S';
					this.output.println("% ACCEPTED");
					this.output.flush();
				}
			}
		} catch (IOException e) {
			System.out.println("Error getting the client's game mode");
			e.printStackTrace();
			this.userType = 0;
		}

		try {
			if (input.readLine().equals("READY")) {
				server.ready(player.getPlayerNo());
			}
		} catch (IOException e) {
			System.out
					.println("Error getting the \"ready\" status of the player");
			e.printStackTrace();
		}
	}

	public void broadcast(String message) {
		this.output.println(message);
		this.output.flush();
	}

	protected Socket getSocket() {
		return this.socket;
	}

	public String getName() {
		return this.name;
	}

	public BufferedReader getIn() {
		return this.input;
	}

	public PrintWriter getOut() {
		return this.output;
	}

	public int getBet() {

		return player.getCurrentBet();
	}

	public int getCoins() {

		return player.getCoins();
	}

	public void setBet(int betAmount) {
		player.setCurrentBet(betAmount);

	}

	public void setCoins(int noOfCoins) {
		player.setCoins(noOfCoins);

	}
}