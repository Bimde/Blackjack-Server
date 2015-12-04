package connection;

import gameplay.Dealer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.Timer;

import utilities.ClientList;
import utilities.Validator;

public class Server implements ActionListener {
	private ArrayList<Client> clients;
	private ClientList players;
	private int playersReady;
	private boolean gameStarted;
	private ServerSocket socket;
	private Dealer dealer;
	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000, MESSAGE_DELAY = 500;

	// Array indicating which player numbers have been taken (index 0 is dealer)
	public boolean[] playerNumbers = { true, false, false, false, false, false,
			false };

	private Timer timer;
	private Queue<Message> messages;

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public Server(int port) {

		// Sets up client list to hold each client
		// Sets up the socket and the number of ready players to zero
		this.clients = new ArrayList<Client>();
		this.players = new ClientList();
		this.socket = null;
		this.playersReady = 0;
		this.timer = new Timer(MESSAGE_DELAY, this);

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
				this.clients.add(temp);
			} catch (Exception e) {
				System.err.println("Error connecting to client "
						+ this.clients.size());
				e.printStackTrace();
			}
			System.err.println("Client " + this.clients.size() + " connected.");
		}
	}

	/**
	 * Change a player number to unused as a player disconnects from the lobby
	 * 
	 * @param playerNumber
	 */
	public void clearPlayerNumber(int playerNumber) {
		this.playerNumbers[playerNumber] = false;
	}

	/**
	 * Return the first unused player number and -1 if everything is used
	 * 
	 * @return the first unused player number
	 */
	private int returnAndUsePlayerNumber() {
		for (int no = 1; no < this.playerNumbers.length; no++) {
			if (!this.playerNumbers[no]) {
				this.playerNumbers[no] = true;
				return no;
			}
		}
		return -1;
	}

	/**
	 * Waits for the player to be ready. Once the player is ready start the
	 * game.
	 * 
	 * @param playerNo
	 */
	protected synchronized void ready(int playerNo) {
		this.playersReady++;
		this.queueMessage(new Message(Message.ALL_CLIENTS, "% " + playerNo
				+ " READY"));
		if (this.playersReady == this.clients.size()) {

			// Do a 15 second timer (otherwise the player times out)
			this.gameStarted = true;
			this.startGame();
		}
	}

	public void playerDisconnected(Client source) {
		this.players.remove(source);
	}

	/**
	 * Starts the game. Broadcasts a start game message and sets up the dealer
	 * for the game.
	 */
	private void startGame() {
		this.queueMessage(new Message(Message.ALL_CLIENTS, "% START"));
		this.dealer = new Dealer(this, this.clients);
	}

	/**
	 * Places the message in the queue for message broadcasts
	 * 
	 * @param message
	 *            the message to send
	 */
	private void queueMessage(Message message) {
		synchronized (this.messages) {
			for (int no = 0; no < clients.size(); no++) {
				this.messages.add(message);
			}
		}
	}

	public void newPlayer(Client source) {
		source.setPlayer(new Player(this, this.returnAndUsePlayerNumber()));
		this.players.add(source);
	}

	/**
	 * Queue a message to broadcast
	 * 
	 * @param message
	 *            the message to queue
	 */
	public void broadcast(String message) {
		this.queueMessage(new Message(Message.ALL_CLIENTS, message));
	}

	/**
	 * Determines whether or not the lobby is full.
	 * 
	 * @return Whether or not the lobby is full.
	 */
	public synchronized boolean isFull() {
		return (this.clients.size() == 6);
	}

	/**
	 * Disconnect a client from the server
	 * 
	 * @param client
	 */
	public synchronized void disconnectClient(Client client) {
		if (client.isReady()) {
			this.playersReady--;
		}
		this.clients.remove(client);
	}

	/**
	 * Determines if the game has started or not.
	 * 
	 * @return Whether or not the game has started.
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

		while (port == -1) {
			System.out.print("Please enter a valid port: ");
			portStr = keyboard.nextLine();
			if (Validator.isValidPort(portStr)) {
				port = Integer.parseInt(portStr);
			}
		}

		new Server(port);
	}

	/**
	 * Gets called by 'actionPerformed' method to send the latest message to the
	 * specified clients
	 */
	public void actionPerformed(ActionEvent arg0) {
		synchronized (this.messages) {
			if (this.messages.size() == 0)
				return;
			Message msg = this.messages.remove();
			if (msg.getPlayerNo() == Message.ALL_CLIENTS)

				synchronized (this.clients) {

					for (int i = 0; i < this.clients.size(); i++) {
						this.clients.get(i).message(msg.getMessage());
					}
				}
			else {
				Client temp = this.clients.get(msg.getPlayerNo());
				if (temp != null)
					temp.message(msg.getMessage());
			}
		}
	}
}