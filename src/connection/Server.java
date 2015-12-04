package connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.Timer;

import gameplay.Dealer;
import utilities.ClientList;
import utilities.Validator;

public class Server implements ActionListener {
	private ArrayList<Client> clients;
	private int playersReady;
	private boolean gameStarted;
	private ServerSocket socket;
	private Dealer dealer;
	private static final String START_MESSAGE = "START";
	private static final int START_COINS = 1000, MESSAGE_DELAY = 500;

	// Array indicating which player numbers have been taken (index 0 is dealer)
	public static boolean[] playerNumbers = { true, false, false, false, false,
			false, false };

	public String getStartMessage() {
		return START_MESSAGE;
	}

	public int getStartCoins() {
		return START_COINS;
	}

	public int getMessageDelay() {
		return MESSAGE_DELAY;
	}

	/**
	 * Change a player number to unused as a player disconnects from the lobby
	 * 
	 * @param playerNumber
	 */
	public void clearPlayerNumber(int playerNumber) {
		playerNumbers[playerNumber] = false;
	}

	/**
	 * Return the first unused player number and -1 if everything is used
	 * 
	 * @return the first unused player number
	 */
	public int returnAndUsePlayerNumber() {
		for (int no = 1; no < playerNumbers.length; no++) {
			if (!playerNumbers[no]) {
				return no;
			}
		}
		return -1;
	}

	private Timer timer;
	private ArrayList<Message> messages;

	public Server(int port) {

		// Sets up client list to hold each client
		// Sets up the socket and the number of ready players to zero
		this.clients = new ArrayList<Client>();
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
				Socket client = socket.accept();
				Client temp = new Client(client, this);
				new Thread(temp).start();
				clients.add(temp);
			} catch (Exception e) {
				System.err.println("Error connecting to client "
						+ clients.size());
				e.printStackTrace();
			}
			System.err.println("Client " + clients.size() + " connected.");
		}
	}

	// //////////////////////////////////////////////
	// We can just broadcast the new player client to the newly joined player
	// too
	// ///////////////////////////////////////////////

	// /**
	// * Allows individual client threads to add their associated client as a
	// * player
	// *
	// * @param playerNo
	// * The assigned number of the client
	// * @param name
	// * The name of the client
	// */
	// protected synchronized void newPlayer(int playerNo, String name) {
	// synchronized (this.clients) {
	// for (int i = 0; i < clients.size(); i++) {
	// if (i + 1 != playerNo) {
	// this.clients.get(i).message(playerNo + " " + name);
	// }
	// }
	// }
	// }

	/**
	 * Waits for the player to be ready. Once the player is ready start the
	 * game.
	 * 
	 * @param playerNo
	 */
	protected synchronized void ready(int playerNo) {
		this.playersReady++;
		broadcast("% " + playerNo + " READY");
		if (playersReady == this.clients.size()) {

			// Do a 15 second timer (otherwise the player times out)
			this.gameStarted = true;
			this.startGame();
		}
	}

	/**
	 * Starts the game. Broadcasts a start game message and sets up the dealer
	 * for the game.
	 */
	private void startGame() {
		this.broadcast(START_MESSAGE);
		this.dealer = new Dealer(this, this.clients);
	}

	/**
	 * Broadcasts a message to each client.
	 * 
	 * @param message
	 */
	public void broadcast(String message) {
		for (int no = 0; no < clients.size(); no++)
		{
			clients.get(no).message(message);
		}
		
	}

	/**
	 * Gets called by 'actionPerformed' method to send the latest message to the
	 * specified clients
	 */
	private void broadcastActionPerformed() {
		synchronized (this.messages) {
			if (this.messages.size() == 0)
				return;
			Message msg = this.messages.get(0);
			this.messages.remove(0);
			if (msg.playerNo == Message.ALL_CLIENTS)
				this.broadcastToAll(msg.data);
			else {
				Client temp = this.clients.get(msg.playerNo);
				if (temp != null)
					temp.message(msg.data);
			}
		}
	}

	private void broadcastToAll(String message) {
		synchronized (this.clients) {
			for (int i = 0; i < clients.size(); i++) {
				this.clients.get(i).message(message);
			}
		}
	}

	/**
	 * Determines whether or not the lobby is full.
	 * 
	 * @return Whether or not the lobby is full.
	 */
	public synchronized boolean isFull() {
		return (clients.size() == 6);
	}

	/**
	 *  Disconnect a client from the server
	 * @param client
	 */
	public synchronized void disconnectClient(Client client) {
		if (client.isReady())
		{
			playersReady--;
		}
		clients.remove(client);
	}

	/**
	 * Determines if the game has started or not.
	 * 
	 * @return Whether or not the game has started.
	 */
	public boolean gameStarted() {
		return gameStarted;
	}

	public static void main(String[] args) {
		String portStr;
		int port = 5000;
		Scanner keyboard = new Scanner(System.in);

		//
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.broadcastActionPerformed();
	}
}