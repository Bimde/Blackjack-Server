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

public class CentralServer {
	private ServerSocket socket;
	private ArrayList<Server> listOfServers;
	private int userNo = 0;
	private int serverUsed;
	private JTextArea textArea;
	private JFrame frame;
	private JScrollPane pane;

	public static void main(String[] args) {
		new CentralServer();
	}

	/**
	 * Constructor for a new CentralServer object. This central server will hold
	 * all servers/games started on the same IP address/port.
	 */
	public CentralServer() {
		int port = -1;
		Scanner keyboard = new Scanner(System.in);
		
		// Create a gui for debugging
		this.frame = new JFrame("~~~Blackjack Server~~~");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.textArea = new JTextArea();
		this.pane = new JScrollPane(this.textArea);
		((DefaultCaret) this.textArea.getCaret())
				.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.textArea.setEditable(false);
		this.pane.setPreferredSize(new Dimension(300, 400));
		this.frame.add(this.pane);
		this.frame.setVisible(true);
		this.frame.pack();

		// While the port entered is invalid, continually ask for another port
		while (port == -1) {
			String portStr = null;
			while (portStr == null)
				portStr = JOptionPane
						.showInputDialog("Enter a port (1-5 digits): ");
			if (Validator.isValidPort(portStr)) {
				port = Integer.parseInt(portStr);
			}
		}

		keyboard.close();

		// Create the socket based on the port enterred
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Error creating a new server socket on port "
					+ port);
			e.printStackTrace();
		}
		this.listOfServers = new ArrayList<Server>();
		this.listOfServers.add(new Server(this));

		// Accept and connect new clients who join the server
		while (true) {
			this.println("Waiting for client to connect...");
			try {
				Socket client = this.socket.accept();
				Client temp = new Client(client, this);
				new Thread(temp).start();
				this.userNo++;
				this.println("Client #" + this.userNo + " has connected");
			} catch (Exception e) {
				this.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Place a player/spectator in the next available game room (player in the
	 * first non-full && non-started room and spectator in the first non-started
	 * room)
	 * 
	 * @param client
	 *            the client to add to the server
	 * @param isPlayer
	 *            whether or not it is a player
	 */
	void addToServer(Client client, boolean isPlayer) {
		Server availableServer = null;
		int serverUsed = 0;
		boolean serverFound = false;

		// Uses a regular for loop instead of for-each loop to prevent changes
		// in list of servers during search causing iterator to throw
		// ConcurrentModificationException
		for (int serverNo = 0; !serverFound
				&& serverNo < this.listOfServers.size(); serverNo++) {
			Server currentServer = this.listOfServers.get(serverNo);
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
			serverUsed = this.listOfServers.size();
			this.listOfServers.add(availableServer);
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
	 *  Remove the game room once the game has ended
	 * @param server the server to remove
	 */
	public void removeServer(Server server) {
		this.listOfServers.remove(server);
	}
	
	/**
	 * Print to the debugging gui console
	 * @param message the message to print
	 */
	public void println(String message) {
		this.textArea.append(message + "\n");
	}

	ArrayList<Server> getListOfServers() {
		return this.listOfServers;
	}
	
	public int getServerUsed() {
		return this.serverUsed;
	}
}