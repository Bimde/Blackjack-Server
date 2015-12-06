package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
	private Server server;
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private String name;
	private boolean connected;
	
	
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
			this.server.queueMessage("! " + this.player.getPlayerNo());
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

	/**
	 *  Constructor for the client
	 */
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
			this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
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
						this.message("% LATE");
					} else if (server.isFull()) {
						this.message("% FULL");
					} else {
						this.userType = 'P';
						this.message("% ACCEPTED");
						this.server.newPlayer(this);
					}
				} else if (message.equalsIgnoreCase("SPECTATE")) {
					this.userType = 'S';
					this.message("% ACCEPTED");
				}
			} catch (IOException e) {
				this.disconnect();
			}
		}

		while (this.userType == 'U' && this.connected && !this.isReady) {
			try {
				String message = this.input.readLine();

				if (message.equalsIgnoreCase("READY")) {
					this.server.ready(this.player.getPlayerNo());
					this.isReady = true;
				} else {
					this.message("% FORMATERROR");
				}
			} catch (IOException e) {
				this.disconnect();
			}

		}

		// Game
		while (this.userType == 'U' && this.connected) {
			// Get the player's bet
			// TODO Add a 60s timer
			try {
				int currentBet = Integer.parseInt(this.input.readLine());
				if (currentBet >= 10 && currentBet <= this.getCoins()) {
					this.setBet(currentBet);
					this.server.queueMessage("$ " + this.getPlayerNo() + " bets " + currentBet);
				} else {
					this.message("% FORMATERROR");
				}
			} catch (IOException e) {
				this.server.queueMessage("! " + this.getPlayerNo());
				this.disconnect();
			}
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

	public BufferedReader getIn() {
		return this.input;
	}

	public PrintWriter getOut() {
		return this.output;
	}

	protected Socket getSocket() {
		return socket;
	}


	public String getName() {
		return name;
	}

	public int getPlayerNo() {
		if (this.player == null)
			return -1;
		return this.player.getPlayerNo();
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

	public Player getPlayer() {
		return this.player;
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

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isReady() {
		return this.isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
}