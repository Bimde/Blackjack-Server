package connection;

import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import utilities.Validator;

/**
 * Central server that hosts multiple servers for blackjack games. Automatically
 * allocates new players to different servers.
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 8, 2015
 */
public class CentralServer {
	private ServerSocket socket;
	private ArrayList<Server> listOfGameServers;

	// GUI components for displaying server events
	private JTextArea textArea;
	private JFrame frame;
	private JScrollPane srollPane;

	/**
	 * Keeps tracks of the number of clients who joined the server.
	 */
	private int noOfClients;

	public static void main(String[] args) {
		new CentralServer();
	}

	/**
	 * Constructor for a new CentralServer object. This central server will hold
	 * all servers/games started on the same IP address/port.
	 */
	public CentralServer() {
		String portStr;
		int port = -1;
		Scanner keyboard = new Scanner(System.in);

		// Keep trying to connect to a new port while the port is invalid
		while (port == -1) {
			// Create a GUI for debugging
			this.frame = new JFrame("Blackjack Server");
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.textArea = new JTextArea();
			this.srollPane = new JScrollPane(this.textArea);

			// Enables auto-scrolling when new messages are added
			((DefaultCaret) this.textArea.getCaret())
					.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			this.textArea.setEditable(false);
			this.srollPane.setPreferredSize(new Dimension(300, 400));
			this.frame.add(this.srollPane);
			this.frame.setVisible(true);
			this.frame.pack();

			// While the port entered is invalid, continually ask for another
			// port
			while (port == -1) {
				portStr = null;
				while (portStr == null)
					portStr = JOptionPane
							.showInputDialog("Enter a port (1-5 digits): ");
				if (Validator.isValidPort(portStr)) {
					port = Integer.parseInt(portStr);
				}
			}

			// Create the socket based on the port entered
			try {
				this.socket = new ServerSocket(port);
			} catch (IOException e) {
				System.err
						.println("Error creating a new server socket on port "
								+ port);
				e.printStackTrace();
				port = -1;
			}
			this.listOfGameServers = new ArrayList<Server>();
			this.listOfGameServers.add(new Server(this));
		}

		keyboard.close();

		// Accept and connect new clients who join the server
		this.noOfClients = 0;
		while (true) {
			this.println("Waiting for client to connect...");
			try {
				Socket client = this.socket.accept();
				Client temp = new Client(client, this);
				new Thread(temp).start();
				this.noOfClients++;
				this.println("Client #" + this.noOfClients + " has connected");
			} catch (Exception e) {
				this.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Place a player/spectator in the next available game room (player in the
	 * first non-full && non-started room and spectator in the first non-started
	 * room). Synchronized to prevent players from creating two new servers at
	 * the same time if a available server doesn't exist.
	 * 
	 * @param client
	 *            the client to add to the server.
	 * @param isPlayer
	 *            whether or not it is a player.
	 */
	synchronized void addToServer(Client client, boolean isPlayer) {
		Server availableServer = null;
		int serverUsed = 0;
		boolean serverFound = false;

		// Uses a regular for loop instead of for-each loop to prevent changes
		// in list of servers during search from causing iterator to throw
		// ConcurrentModificationException
		for (int serverNo = 0; !serverFound
				&& serverNo < this.listOfGameServers.size(); serverNo++) {
			Server currentServer = this.listOfGameServers.get(serverNo);
			if (!currentServer.gameStarted()
					&& (!isPlayer || !currentServer.isFull())) {
				availableServer = currentServer;
				serverUsed = serverNo;
				serverFound = true;
			}
		}

		// Create a new game room if there are no available rooms for the client
		if (!serverFound) {
			availableServer = new Server(this);
			serverUsed = this.listOfGameServers.size();
			this.listOfGameServers.add(availableServer);
		}

		// Add the client to the available room and print the information
		availableServer.addClient(client);
		client.setServer(availableServer);
		if (isPlayer)
			this.println("Player connected to server #" + (serverUsed + 1));
		else
			this.println("Client connected to server #" + (serverUsed + 1));
	}

	/**
	 * Remove a server/game room once the game has ended.
	 * 
	 * @param server
	 *            the server to remove.
	 */
	public void removeServer(Server server) {
		this.listOfGameServers.remove(server);
	}

	/**
	 * Prints specified message to the debugging GUI.<br>
	 * This method will <b>bypass</b> the {@link Server#DEBUG debugging
	 * constant}, meaning anything sent to this method will be sent to the GUI.
	 * 
	 * @param message
	 *            the message to send to the GUI.
	 */
	public void println(String message) {
		this.textArea.append(message + "\n");
	}
}