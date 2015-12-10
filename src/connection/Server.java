package connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.Timer;

import gameplay.Dealer;
import utilities.ClientList;

/**
 * Object for each individual blackjack server that is within a central server.
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 1, 2015
 */
public class Server implements ActionListener {
	private ArrayList<Client> allClients;
	private ClientList players;
	private int playersReady, currentTimer;
	private boolean gameStarted;
	private Dealer dealer;

	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000, MESSAGE_DELAY = 500,
			MIN_BET = 10, START_DELAY = 15;
	public static final boolean DEBUG = true;

	/**
	 * Array indicating which player numbers have been taken (index 0 is
	 * dealer).
	 */
	public boolean[] playerNumbers = { true, false, false, false, false, false,
			false };

	private Timer messageTimer;
	private ArrayDeque<Message> messages;
	private CentralServer centralServer;
	private boolean sendMessages;
	private boolean lobbyTimerActive;

	/**
	 * Constructor for a new Server object.
	 * 
	 * @param centralServer
	 *            the central server to put the new server on.
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
		this.currentTimer = 0;
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
	 * Return the first unused player number and -1 if everything is used. Also
	 * fills up the unused player number.
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

		// Tell all of the players in the lobby that somebody has become ready
		this.queueMessage("% " + playerNo + " READY");

		// Do a 15 second timer to wait for more people to join
		if (this.playersReady != 0 && this.playersReady == this.players.size()) {
			this.startReadyTimer(true);
		}
	}

	/**
	 * Once all the players in the lobby declare they are ready, set a timer to
	 * run before starting the game in order to give more people a chance to
	 * join.
	 */
	public void startReadyTimer(boolean playerJoined) {
		// Make sure that the server is not full
		// Otherwise, start the game right away
		this.currentTimer++;
		if (this.currentTimer > Integer.MAX_VALUE - 5)
			this.currentTimer = 0;

		if (playerJoined || !this.lobbyTimerActive) {
			if (this.players.size() < 6) {
				this.lobbyTimerActive = true;
				long startTime = System.nanoTime();

				// Create new thread to prevent interference with other
				// activities
				new Thread(new Updatable(this.currentTimer) {
					@Override
					public void run() {
						// Keep checking if the entire lobby is ready until the
						// number of seconds specified by the
						// 'Server#START_DELAY' constant is reached
						while ((System.nanoTime() - startTime) / 1000000000 < Server.START_DELAY) {
							if (Server.this.playersReady == 0
									|| Server.this.playersReady != Server.this.players
											.size()
									|| this.value != Server.this.currentTimer) {
								Server.this.println("Cancelled timer");
								Server.this.lobbyTimerActive = false;
								return;
							}
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						Server.this.startGame();
					}
				}).start();

			} else if (this.players.size() == this.playersReady) {
				this.startGame();
			}
		}
	}

	/**
	 * Disconnects a player from the server.
	 * 
	 * @param source
	 *            the client to disconnect.
	 */
	public void disconnectPlayer(Client source) {
		// Announce to all other players that a player has disconnected
		this.queueMessage("! " + source.getPlayer().getPlayerNo());

		// Remove the player and free up its space
		source.setUserType('S');
		this.players.remove(source);
		this.playerNumbers[source.getPlayerNo()] = false;

		if (this.gameStarted) {
			// If the player was the last player in the game, stop the game
			if (this.players.size() == 0) {
				this.gameStarted = false;
			}
		} else {
			// Decrease the number of players ready if they were ready
			if (source.isReady()) {
				this.playersReady--;
			}

			// Start the timer if everybody else was ready
			if (this.playersReady != 0
					&& this.playersReady == this.players.size()) {
				this.startReadyTimer(false);
			}
		}
	}

	/**
	 * Starts the game. Broadcasts a start game message and sets up the dealer
	 * for the game.
	 */
	private void startGame() {
		this.lobbyTimerActive = false;
		this.gameStarted = true;
		this.queueMessage("% START");
		this.dealer = new Dealer(this, this.players);

		// Start the dealer thread
		Thread dealerThread = new Thread(dealer);
		dealerThread.start();
	}

	/**
	 * Queues a message to send.
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

	/**
	 * Check whether or not there are still messages to be sent
	 * 
	 * @return whether or not the queue of messages is empty
	 */
	public boolean isMessageQueueEmpty() {
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

		// Send a message to all clients that a new player has joined
		this.queueMessage(new Message(Message.ALL_CLIENTS,
				source.getPlayerNo(), "@ " + source.getPlayerNo() + " "
						+ source.getName()));
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
	 * Gets the client list of current players.
	 * 
	 * @return the list of current players.
	 */
	protected ClientList getCurrentPlayers() {
		return this.players;
	}

	/**
	 * Constantly sends out messages from the server's message queue at a
	 * specified delay (specified by Server.MESSAGE_DELAY).
	 */
	public void actionPerformed(ActionEvent arg0) {
		synchronized (this.messages) {
			if (this.messages.size() == 0) {
				if (!this.sendMessages) {
					this.messageTimer.stop();
				}
				return;
			}
			Message msg = this.messages.remove();
			this.println("Our Message: " + msg.getMessage());

			// Messages are either to the entire server or to individual clients
			if (msg.getPlayerNo() == Message.ALL_CLIENTS) {
				// Send the message to every client at the same time
				synchronized (this.allClients) {
					for (Client client : this.allClients) {
						// Send the message only to clients who are not ignored
						if (client.getPlayerNo() != msg.getIgnoredPlayer()) {
							client.sendMessage(msg.getMessage());
						}
					}
				}
			} else {
				// Send the message to a specific client
				Client temp = this.players.get(msg.getPlayerNo());
				if (temp != null) {
					temp.sendMessage(msg.getMessage());
				}
			}
		}
	}

	/**
	 * Ends the current game of the server and then removes the server from the
	 * central server it is a part of.
	 */
	public void endGame() {
		this.gameStarted = false;
		this.sendMessages = false;
		this.centralServer.removeServer(this);
	}

	/**
	 * Add a new client to the server.
	 * 
	 * @param newClient
	 *            the new client to add.
	 */
	public void addClient(Client newClient) {
		this.allClients.add(newClient);
	}

	/**
	 * Centralized place to print messages to allow for debugging messages to be
	 * disabled on release and enabled when needed.
	 * 
	 * @param message
	 *            string to print out to standard out.
	 */
	public void println(String message) {
		if (DEBUG) {
			this.centralServer.println(message);
		}
	}

	public ArrayList<Client> getAllClients() {
		return allClients;
	}

	public void setAllClients(ArrayList<Client> allClients) {
		this.allClients = allClients;
	}
}