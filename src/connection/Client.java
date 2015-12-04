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

		if (this.userType == 'P') {
			System.out.println(this.player.getPlayerNo() + " has disconnected");
			//CHANGE THIS
			this.server.broadcast("! " + this.player.getPlayerNo());
		} else {
			System.out.println("Client has disconnected");
		}

		this.connected = false;
		try {
			this.input.close();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.output.close();
		this.server.disconnectClient(this);

	}

	public Player getPlayer() {
		return this.player;
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

		while (this.userType == 'U' && this.connected) {

			try {
				String message = this.input.readLine();

				if (message.equalsIgnoreCase("PLAY")) {
					if (this.server.gameStarted()) {
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
						this.server.newPlayer(this);
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

		while (this.connected && !this.isReady) {
			try {
				String message = this.input.readLine();

				if (message.equalsIgnoreCase("READY")) {
					this.server.ready(this.player.getPlayerNo());
					this.isReady = true;
				}
			} catch (IOException e) {
				disconnect();
			}

		}

		// Game
		while (this.connected) 
		{
			
			
		}
	}

	/**
	 * Send a message to the client
	 * 
	 * @param message
	 *            the message to send
	 */
	public void message(String message) {
		this.output.println(message);
		this.output.flush();
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
		if (this.player == null)
			return -1;
		return this.player.getCurrentBet();
	}

	public int getCoins() {
		if (this.player == null)
			return -1;
		return this.player.getCoins();
	}

	public void setBet(int betAmount) {
		if (this.player == null)
			return;
		this.player.setCurrentBet(betAmount);

	}

	public void setCoins(int noOfCoins) {
		if (this.player == null)
			return;
		this.player.setCoins(noOfCoins);

	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

}