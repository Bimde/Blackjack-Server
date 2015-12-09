package connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.Timer;

import gameplay.Dealer;
import utilities.ClientList;

public class Server implements ActionListener {
	private ArrayList<Client> allClients;
	private ClientList players;
	private int playersReady;
	private boolean gameStarted;
	private Dealer dealer;

	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000, MESSAGE_DELAY = 500, MIN_BET = 10;
	public static final boolean DEBUG = false;

	// Array indicating which player numbers have been taken (index 0 is dealer)
	public boolean[] playerNumbers = { true, false, false, false, false, false, false };

	private Timer messageTimer;
	private ArrayDeque<Message> messages;
	private CentralServer centralServer;
	private boolean sendMessages;

	public ArrayList<Client> getAllClients() {
		return allClients;
	}

	public void setAllClients(ArrayList<Client> allClients) {
		this.allClients = allClients;
	}

	public void addClient(Client newClient) {
		this.allClients.add(newClient);
	}

	/**
	 * Constructor for a new Server object.
	 * 
	 * @param port
	 *            the port to start the server on.
	 */
	public Server(CentralServer centralServer) {
		// Sets up client list to hold each client
		// Sets up the socket and the number of ready players to zero
		this.allClients = new ArrayList<Client>();
		this.players = new ClientList();
		this.centralServer = centralServer;
		this.playersReady = 0;
		this.messageTimer = new Timer(MESSAGE_DELAY, this);
		this.messages = new ArrayDeque<Message>();
		this.sendMessages = true;
		this.messageTimer.start();
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
						this.println("Cancelled timer");
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
		this.println("---" + this.players);
		this.queueMessage("! " + source.getPlayer().getPlayerNo());
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
		this.println("---" + this.players);
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
		synchronized (this.messages) {
			if (this.sendMessages)
				this.messages.add(message);
		}
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
		synchronized (this.messages) {
			if (this.messages.size() == 0) {
				if (!this.sendMessages)
					this.messageTimer.stop();
				return;
			}
			Message msg = this.messages.remove();
			this.println("Our Message: " + msg.getMessage());

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

	public void endGame() {
		this.gameStarted = false;
		this.sendMessages = false;
		this.centralServer.removeServer(this);
	}

	/**
	 * Centralized place to print messages to allow for debugging messages to be
	 * disabled on release and enabled when needed
	 * 
	 * @param message
	 *            String to print out to standard out
	 */
	public void println(String message) {
		if (DEBUG)
			System.out.println(message);
	}
}