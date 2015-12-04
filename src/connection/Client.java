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

	// Whether or not the player is ready to start the game
	private boolean isReady = false;

	// Potential Player or Spectator object
	private Player player;

	// 'U' for unassigned, 'P' for player and 'S' for spectator
	private char userType;


	/**
	 * Disconnect/timeout the player
	 */
	public void disconnect() {

		if (userType == 'P') {
			System.out.println(player.getPlayerNo() + " has disconnected");
			server.broadcast("! " + player.getPlayerNo());
		}
		else
		{
			System.out.println("Client has disconnected");
		}

		connected = false;
		try {
			input.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		output.close();
		server.disconnectClient(this);

	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

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

		// Set the user's account type, either enter the user into the game or
		// assign as a spectator

		while (this.userType == 'U' && connected) {

			try {
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
				} else if (message.equalsIgnoreCase("SPECTATE")) {
					this.userType = 'S';
					this.output.println("% ACCEPTED");
					this.output.flush();
				}
			} catch (IOException e) {
				disconnect();
			}

		}

		while (connected && !isReady) {
			try {
				String message = input.readLine();

				if (message.equalsIgnoreCase("READY")) {
					server.ready(player.getPlayerNo());
					isReady = true;
				}
			} catch (IOException e) {
				disconnect();
			}

		}

		while (connected) {
			// Do clienty stuff / player stuff
		}
	}

	/**
	 * Send a message to the client
	 * 
	 * @param message
	 *            the message to send
	 */
	public void message(String message) {
		output.println(message);
		output.flush();
	}

	protected Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
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

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

}