package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import gameplay.Dealer;
import utilities.ClientList;
import utilities.Validator;

/**
 * Object for every client connecting to a blackjack server.
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 1, 2015
 */
public class Client implements Runnable {
	private Server server;
	private CentralServer centralServer;
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private String name;
	private boolean connected;
	private Dealer dealer;

	/**
	 * Whether or not the player is ready to start the game.
	 */
	private boolean isReady = false;

	/**
	 * Potential Player object if the client is a player.
	 */
	private Player player;

	/**
	 * 'U' for unassigned, 'P' for player and 'S' for spectator.
	 */
	private char userType;

	/**
	 * Constructor for a new Client object.
	 * 
	 * @param client
	 *            socket of the connected client.
	 * @param centralServer
	 *            central server for routing players to specific game-server.
	 */
	public Client(Socket client, CentralServer centralServer) {
		this.socket = client;
		this.connected = true;
		this.centralServer = centralServer;
	}

	/**
	 * Remove client from all associated lists and close all linked to client
	 * (input/output streams + socket) by calling appropriate 'Server' methods.
	 */
	public void disconnect() {
		// Doesn't use Server.println() because the Server may have not been
		// determined
		if (this.userType == 'P') {
			if (Server.DEBUG) {
				System.out.println(this.player.getPlayerNo()
						+ " has disconnected");
			}
		} else {
			if (Server.DEBUG) {
				System.out.println("Client has disconnected");
			}
		}

		this.connected = false;

		// Try closing all input/output streams and socket
		try {
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch (IOException e) {
			System.err.println("Error closing the socket");
			e.printStackTrace();
		}

		// Do not need to disconnect from server if before being added to server
		if (this.server != null) {
			if (this.isPlayer()) {
				this.server.disconnectPlayer(this);
			}
			this.server.disconnectClient(this);
		}

		this.userType = 'U';
	}

	/**
	 * Starts the thread of a client, mostly used for input and output of
	 * messages.
	 */
	@Override
	public void run() {
		// Set up the output
		try {
			this.output = new PrintWriter(this.socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Error getting client's output stream");
			e.printStackTrace();
			this.connected = false;
		}

		// Set up the input
		try {
			this.input = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error getting client's input stream");
			e.printStackTrace();
			this.connected = false;
		}

		// Try to get the name of the client
		// Make sure the name is valid (1-16 alphanumeric + spaces)
		while (this.connected && this.name == null) {
			String newName = this.readLine();
			if (Validator.isValidName(newName)) {
				this.name = newName;
			} else {
				this.sendMessage("% FORMATERROR");
			}
		}

		// Not using Server.println() because server is not yet determined
		if (Server.DEBUG) {
			this.centralServer.println("New user registered as " + this.name);
		}

		// The user type is unassigned at first
		this.userType = 'U';

		// Set the user's account type, either enter the user into the game or
		// assign as a spectator
		while (this.userType == 'U' && this.connected) {
			String message = this.readLine();
			if (message.equalsIgnoreCase("PLAY")) {
				this.sendMessage("% ACCEPTED");
				this.centralServer.addToServer(this, true);
				this.player = new Player(this.server,
						this.server.returnAndUsePlayerNumber());
				this.server.newPlayer(this);
				this.userType = 'P';
			} else if (message.equalsIgnoreCase("SPECTATE")) {
				this.userType = 'S';
				this.centralServer.addToServer(this, false);
				this.sendMessage("% ACCEPTED");
			} else {
				this.sendMessage("% FORMATERROR");
			}
		}

		// Inform the user of all the players currently in the lobby
		this.sendStartMessage();

		if (this.isPlayer()) {
			// Check if the player is ready to start
			while (this.connected && !this.isReady) {
				String message = this.readLine();
				if (message.equalsIgnoreCase("READY")) {
					this.server.ready(this.player.getPlayerNo());
					this.isReady = true;
				} else {
					this.sendMessage("% FORMATERROR");
				}
			}
		}

		// Game loop
		while (this.isPlayer() && this.connected) {
			String message = this.readLine();
			this.server.println(this.getPlayerNo() + " : " + this.name
					+ "'S MESSAGE: " + message);

			int betPlaced = 0;

			// Set the bet if the player is betting
			// Otherwise if the player is hitting/standing/doubling down, set
			// their move to the respective move
			// If the message doesn't match, give them an error
			if (this.server.gameStarted()) {
				if (this.dealer.bettingIsActive()
						&& this.player.getCurrentBet() == 0
						&& message.matches("[0-9]{1,8}")
						&& (betPlaced = Integer.parseInt(message)) >= Server.MIN_BET
						&& betPlaced <= this.player.getCoins()) {
					this.server.queueMessage("$ " + this.getPlayerNo()
							+ " bets " + betPlaced);
					this.server.println("Bet Placed (not applicable if 0): "
							+ betPlaced);
					this.player.setCurrentBet(betPlaced);
				} else if (this.dealer.getCurrentPlayerTurn() == this
						.getPlayerNo() && message.equalsIgnoreCase("hit")) {
					this.player.setCurrentMove('H');
				} else if (dealer.getCurrentPlayerTurn() == this.getPlayerNo()
						&& message.equalsIgnoreCase("stand")) {
					this.player.setCurrentMove('S');
				} else if (this.dealer.getCurrentPlayerTurn() == this
						.getPlayerNo()
						&& message.equalsIgnoreCase("doubledown")
						&& this.player.getCoins() >= this.player
								.getCurrentBet() * 2) {
					this.player.setCurrentMove('D');
				} else {
					this.sendMessage("% FORMATERROR");
				}
			}
		}

		try {
			Thread.sleep(Server.MESSAGE_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	protected void setServer(Server server) {
		this.server = server;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}

	public String getName() {
		return this.name;
	}

	public int getPlayerNo() {
		if (this.player == null) {
			return -1;
		}
		return this.player.getPlayerNo();
	}

	public boolean isPlayer() {
		return (this.userType == 'P');
	}

	public void setUserType(char userType) {
		this.userType = userType;
	}

	public int getBet() {
		if (this.player == null) {
			return -1;
		}
		return this.player.getCurrentBet();
	}

	public int getCoins() {
		if (this.player == null) {
			return -1;
		}
		return this.player.getCoins();
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setBet(int betAmount) {
		if (this.player == null) {
			return;
		}
		this.player.setCurrentBet(betAmount);
	}

	public void setCoins(int noOfCoins) {
		if (this.player == null) {
			return;
		}
		this.player.setCoins(noOfCoins);
	}

	/**
	 * Sends a list of players that were already connected to the server as well
	 * as the current user's player number (-1 if spectator)
	 */
	private void sendStartMessage() {
		ClientList players = this.server.getCurrentPlayers();
		int noOfPlayersBefore = players.size();
		if (this.isPlayer()) {
			noOfPlayersBefore--;
		}

		// Tells the user his/her player number, the number of other players in
		// the lobby, as well as all their names separated by //
		String message = "@ " + this.getPlayerNo() + " " + noOfPlayersBefore;
		for (Client client : players) {
			if (client != this)
				message += " " + client.getName() + " //";
		}
		this.sendMessage(message);
	}

	/**
	 * Reads in a message from the client.
	 * 
	 * @return the message from the client.
	 */
	private String readLine() {
		String line = "";
		try {
			line = this.input.readLine();
		} catch (IOException e) {
			this.disconnect();
		}
		if (line == null) {
			this.disconnect();
		}
		return line;
	}

	/**
	 * Check if this client (if a player) is ready to start the game.
	 * 
	 * @return whether or not the player is ready.
	 */
	public boolean isReady() {
		return this.isReady;
	}

	/**
	 * Returns the player's name and their coins using the following format:
	 * 'player's name : player's current coins', 'ex. Bob : 150'
	 */
	@Override
	public String toString() {
		return this.name + " : " + this.player.getCoins();
	}
}