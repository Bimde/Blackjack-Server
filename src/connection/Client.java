package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import gameplay.Dealer;
import utilities.ClientList;
import utilities.Validator;

public class Client implements Runnable, Comparable<Client> {
	private Server server;
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private String name;
	private boolean connected;

	private Dealer dealer;

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}

	// Whether or not the player is ready to start the game
	private boolean isReady = false;

	// Potential Player or Spectator object
	private Player player;

	// 'U' for unassigned, 'P' for player and 'S' for spectator
	private char userType;

	/**
	 * Disconnect/timeout the client.
	 */
	public void disconnect() {
		if (this.userType == 'P') {
			System.out.println(this.player.getPlayerNo() + " has disconnected");
			this.server.queueMessage("! " + this.player.getPlayerNo());
		} else {
			System.out.println("Client has disconnected");
		}
		this.userType = 'U';

		this.connected = false;
		try {
			this.input.close();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.output.close();

		if (this.isPlayer()) {
			this.server.disconnectPlayer(this);
		}
		this.server.disconnectClient(this);
	}

	/**
	 * Constructor for a new Client object.
	 * 
	 * @param client
	 *            the socket of the client.
	 * @param server
	 *            the server to put the client on.
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
			while (this.name == null) {
				String newName = input.readLine();
				if (Validator.isValidName(newName)) {
					this.name = newName;
				} else {
					this.sendMessage("% FORMATERROR");
				}
			}
			System.out.println("New user registered as " + name);
		} catch (IOException e) {
			this.disconnect();
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
						this.sendMessage("% LATE");
					} else if (server.isFull()) {
						this.sendMessage("% FULL");
					} else {
						this.userType = 'P';
						this.sendMessage("% ACCEPTED");
						this.server.newPlayer(this);
						player = new Player(this.server, this.server.returnAndUsePlayerNumber());
					}
				} else if (message.equalsIgnoreCase("SPECTATE")) {
					this.userType = 'S';
					this.sendMessage("% ACCEPTED");
				}
			} catch (IOException e) {
				this.disconnect();
			}
		}

		if (this.isPlayer()) {
			this.sendStartMessage();
			// Check if the player is ready to start
			while (this.connected && !this.isReady) {
				try {
					String message = this.input.readLine();

					if (message.equalsIgnoreCase("READY")) {
						this.server.ready(this.player.getPlayerNo());
						this.isReady = true;
						System.out.println(name + " is ready");
					} else {
						this.sendMessage("% FORMATERROR");
					}
				} catch (IOException e) {
					this.disconnect();
				}

			}
		}

		// Game
		while (this.isPlayer() && this.connected) {
			try {
				String message = this.input.readLine();
				System.out.println(this.getPlayerNo() + " : " + this.name + "'S MESSAGGE: " + message);

				// If the player is betting then set the bet
				int betPlaced = 0;
				System.out.println(this.player.getCoins());

				if (this.dealer.bettingIsActive() && this.player.getCurrentBet() == 0 && message.matches("[0-9]+")
						&& (betPlaced = Integer.parseInt(message)) >= Server.MIN_BET
						&& betPlaced <= this.player.getCoins()) {
					this.server.queueMessage("$ " + this.getPlayerNo() + " bets " + betPlaced);
					this.player.setCurrentBet(betPlaced);
				} else
					if (this.dealer.getCurrentPlayerTurn() == this.getPlayerNo() && message.equalsIgnoreCase("hit")) {
					this.player.setCurrentMove('H');
				} else if (dealer.getCurrentPlayerTurn() == this.getPlayerNo() && message.equalsIgnoreCase("stand")) {
					this.player.setCurrentMove('S');
				} else if (this.dealer.getCurrentPlayerTurn() == this.getPlayerNo()
						&& message.equalsIgnoreCase("doubledown")) {
					this.player.setCurrentMove('D');
				} else {
					System.out.println("Bet Placed (not applicable if 0): " + betPlaced);
					this.sendMessage("% FORMATERROR");
				}

			} catch (Exception e) {
				this.server.queueMessage("! " + this.getPlayerNo());
				this.disconnect();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends a private message to the client.
	 * 
	 * @param message
	 *            the message to send.
	 */
	public void sendMessage(String message) {
		this.output.println(message);
		this.output.flush();
	}

	protected Socket getSocket() {
		return this.socket;
	}

	public String getName() {
		return this.name;
	}

	public int getPlayerNo() {
		if (this.player == null)
			return -1;
		return this.player.getPlayerNo();
	}

	public char getUserType() {
		return this.userType;
	}

	public boolean isPlayer() {
		return (this.userType == 'P');
	}

	public void setUserType(char userType) {
		this.userType = userType;
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

	private void sendStartMessage() {
		ClientList players = this.server.getCurrentPlayers();
		String message = "@ " + this.getPlayerNo() + " " + (players.size() - 1);
		for (Client client : players) {
			String name = client.getName();
			if (!name.equals(this.name))
				message += " " + client.getName() + " //";
		}
		this.sendMessage(message);
	}

	public boolean isReady() {
		return this.isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	@Override
	public int compareTo(Client object) {
		return this.getPlayerNo() - object.getPlayerNo();
	}

	@Override
	public String toString() {
		return this.name + " : " + this.player.getCoins();
	}
}