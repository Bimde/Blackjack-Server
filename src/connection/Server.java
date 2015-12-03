package connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.Timer;

import gameplay.Dealer;
import utilities.ClientList;
import utilities.Validator;

public class Server implements ActionListener {
	private ClientList clients;
	private int playersReady;
	private boolean gameStarted;
	private ServerSocket socket;
	private Dealer dealer;
	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000, MESSAGE_DELAY = 500;
	private Timer timer;

	public Server(int port) {

		// Sets up client list to hold each client
		// Sets up the socket and the number of ready players to zero
		this.clients = new ClientList();
		ServerSocket socket = null;
		this.playersReady = 0;
		this.timer = new Timer(MESSAGE_DELAY, this);

		// Try to start the server
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Error starting server.");
			e.printStackTrace();
		}

		// Try to connect to a client while the server has been started
		while (true) {
			System.err.println("Waiting for client to connect...");
			try {
				Socket client = socket.accept();
				Client temp = new Client(client, this, this.clients.size() + 1);
				new Thread(temp).start();
				this.clients.add(temp);
			} catch (Exception e) {
				System.err.println("Error connecting to client " + this.clients.size());
				e.printStackTrace();
			}
			System.err.println("Client " + this.clients.size() + " connected.");
		}
	}

	/**
	 * Allows individual client threads to add their associated client as a
	 * player
	 * 
	 * @param playerNo
	 *            The assigned number of the client
	 * @param name
	 *            The name of the client
	 */
	protected synchronized void newPlayer(int playerNo, String name) {
		synchronized (this.clients) {
			for (int i = 0; i < clients.size(); i++) {
				if (i + 1 != playerNo) {
					this.clients.get(i).broadcast(playerNo + " " + name);
				}
			}
		}
	}

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
	public synchronized void broadcast(String message) {
		synchronized (this.clients) {
			for (int i = 0; i < clients.size(); i++) {
				this.clients.get(i).broadcast(message);
			}
		}
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
	 * Determines if the game has started or not.
	 * 
	 * @return Whether or not the game has started.
	 */
	public boolean gameStarted() {
		return this.gameStarted;
	}

	public static void main(String[] args) {
		String portStr;
		int port = -1;
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
	}
}