package connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.Timer;

import gameplay.Dealer;
import utilities.ClientList;
import utilities.Validator;

public class Server implements ActionListener {
	private ArrayList<Client> allClients;
	private ClientList players;
	private int playersReady;
	private boolean gameStarted;
	private ServerSocket socket;
	private Dealer dealer;
	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000, MESSAGE_DELAY = 500, MIN_BET = 10;

	// Array indicating which player numbers have been taken (index 0 is dealer)
	public boolean[] playerNumbers = { true, false, false, false, false, false, false };

	private Timer timer;
	private ArrayDeque<Message> messages;

	/**
	 * Constructor for a new Server object.
	 * 
	 * @param port
	 *            the port to start the server on.
	 */
	public Server(int port) {
		// Sets up client list to hold each client
		// Sets up the socket and the number of ready players to zero
		this.allClients = new ArrayList<Client>();
		this.players = new ClientList();
		this.socket = null;
		this.playersReady = 0;
		this.timer = new Timer(MESSAGE_DELAY, this);
		this.messages = new ArrayDeque<Message>();
		this.timer.start();

		// Try to start the server
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Error starting server.");
			e.printStackTrace();
		}

		// Try to connect to a client while the server has been started
		while (true) {
			System.err.println("Waiting for client to connect...");
			try {
				Socket client = this.socket.accept();
				Client temp = new Client(client, this);
				new Thread(temp).start();
				this.allClients.add(temp);
			} catch (Exception e) {
				System.err.println("Error connecting to client " + this.allClients.size());
				e.printStackTrace();
			}
			System.err.println("Client " + this.allClients.size() + " connected.");
		}
	}

	/**
	 * Change a player number to unused as a player disconnects from the lobby.
	 * 
	 * @param playerNumber
	 *            the player number to clear.
	 */
	public void clearPlayerNumbers(int playerNumber) {
		this.playerNumbers[playerNumber] = false;
	}

	/**
	 * Return the first unused player number and -1 if everything is used.
	 * 
	 * @return the first unused player number.
	 */
	public int returnAndUsePlayerNumber() {
		for (int no = 1; no < this.playerNumbers.length; no++) {
			if (!this.playerNumbers[no]) {
				this.playerNumbers[no] = true;
				return no;
			}
		}
		return -1;
	}

	/**
	 * Waits for the player to be ready. Once all players are ready, start the
	 * game.
	 * 
	 * @param playerNo
	 *            the player to set to be "ready".
	 */
	public void ready(int playerNo) {
		this.playersReady++;
		this.queueMessage("% " + playerNo + " READY");
		if (this.playersReady != 0 && this.playersReady == this.players.size()) {
			// Do a 15 second timer to wait for more people to join

			if (this.players.size() < 6) {
				long startTime = System.nanoTime();
				while ((System.nanoTime() - startTime) / 1000000000 < 15) {
					if (this.playersReady == 0 || this.playersReady != this.players.size()) {
						System.out.println("Cancelled timer");
						return;
					}
				}
			}
			this.startGame();
		}
	}

	/**
	 * Disconnects a player from the server.
	 * 
	 * @param source
	 *            the client to disconnect.
	 */
	public void disconnectPlayer(Client source) {
		System.out.println("---" + this.players);
		source.setUserType('S');
		this.players.remove(source);
		this.playerNumbers[source.getPlayerNo()] = false;
		if (this.gameStarted) {
			if (this.players.size() == 0) {
				this.gameStarted = false;
			}
		} else {
			if (source.isReady()) {
				this.playersReady--;
			}
			if (this.playersReady != 0 && this.playersReady == this.players.size()) {
				this.startGame();
			}
		}
		System.out.println("---" + this.players);
	}

	/**
	 * Starts the game. Broadcasts a start game message and sets up the dealer
	 * for the game.
	 */
	private void startGame() {
		this.gameStarted = true;
		this.queueMessage("% START");
		this.dealer = new Dealer(this, this.players);

		// Start the dealer thread
		Thread dealerThread = new Thread(dealer);
		dealerThread.start();
	}

	/**
	 * Queues specified message to be sent.
	 * 
	 * @param message
	 *            the message to send.
	 */
	public void queueMessage(Message message) {
		this.messages.add(message);
	}

	public boolean isQueueEmpty() {
		return this.messages.size() == 0;
	}

	/**
	 * Queue a message to broadcast.
	 * 
	 * @param message
	 *            the message to broadcast.
	 */
	public void queueMessage(String message) {
		this.queueMessage(new Message(Message.ALL_CLIENTS, message));
	}

	/**
	 * Indicates to the server that a client wants to become a player.
	 * 
	 * @param source
	 *            the thread which is communicating with a client who wants to
	 *            be a player.
	 */
	public void newPlayer(Client source) {
		this.players.add(source);
		this.queueMessage(new Message(Message.ALL_CLIENTS, source.getPlayerNo(),
				"@ " + source.getPlayerNo() + " " + source.getName()));
	}

	/**
	 * Determines whether or not the lobby is full.
	 * 
	 * @return whether or not the lobby is full.
	 */
	public synchronized boolean isFull() {
		return (this.players.size() == 6);
	}

	/**
	 * Disconnect a client from the server.
	 * 
	 * @param client
	 *            the client to disconnect.
	 */
	public synchronized void disconnectClient(Client client) {
		this.allClients.remove(client);
	}

	/**
	 * Determines if the game has started or not.
	 * 
	 * @return whether or not the game has started.
	 */
	public boolean gameStarted() {
		return this.gameStarted;
	}

	public static void main(String[] args) {
		String portStr;
		int port = 5000;
		Scanner keyboard = new Scanner(System.in);

		if (args.length > 0) {
			if (Validator.isValidPort(args[0])) {
				port = Integer.parseInt(args[0]);
			}
		} else {
			System.out.print("Please enter a port: ");
			portStr = keyboard.nextLine();
			if (Validator.isValidPort(portStr)) {
				port = Integer.parseInt(portStr);
			}
		}

		// While the port entered is invalid, continually ask for another port
		while (port == -1) {
			System.out.print("Please enter a valid port: ");
			portStr = keyboard.nextLine();
			if (Validator.isValidPort(portStr)) {
				port = Integer.parseInt(portStr);
			}
		}

		keyboard.close();

		new Server(port);
	}

	/**
	 * Gets the client list of current players
	 * 
	 * @return The list of current players
	 */
	protected ClientList getCurrentPlayers() {
		return this.players;
	}

	/**
	 * Gets called by 'actionPerformed' method to send the latest message to the
	 * specified clients
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (this.messages.size() == 0)
			return;
		Message msg = this.messages.remove();
		System.out.println("Our Message: " + msg.getMessage());

		// Messages are either to the entire server or to individual clients
		if (msg.getPlayerNo() == Message.ALL_CLIENTS) {
			// Send the messages at the same time
			synchronized (this.allClients) {
				for (Client client : this.allClients) {
					if (client.getPlayerNo() != msg.getIgnoredPlayer())
						client.sendMessage(msg.getMessage());
				}
			}
		} else {
			Client temp = this.players.get(msg.getPlayerNo());
			if (temp != null)
				temp.sendMessage(msg.getMessage());
		}
	}
}