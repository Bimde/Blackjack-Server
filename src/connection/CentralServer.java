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
		args = new String[1];
		args[0] = JOptionPane.showInputDialog("Enter a port: ");

		new CentralServer(args);
	}

	public CentralServer(String[] args) {
		String portStr;
		int port = -1;
		Scanner keyboard = new Scanner(System.in);
		frame = new JFrame("Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textArea = new JTextArea();
		pane = new JScrollPane(textArea);
		pane.setPreferredSize(new Dimension(300, 400));
		frame.add(pane);
		frame.setVisible(true);
		frame.pack();

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

		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Error creating a new server socket on port "
					+ port);
			e.printStackTrace();
		}
		this.listOfServers = new ArrayList<Server>();
		this.listOfServers.add(new Server(this));

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

		if (!serverFound) {
			availableServer = new Server(this);
			serverUsed = this.listOfServers.size();
			this.listOfServers.add(availableServer);
		}

		availableServer.addClient(client);
		client.setServer(availableServer);

		if (isPlayer)
			this.println("Player connected to server #"
					+ (serverUsed + 1));
		else
			this.println("Client connected to server #"
					+ (serverUsed + 1));
	}

	ServerSocket getSocket() {
		return this.socket;
	}

	ArrayList<Server> getListOfServers() {
		return this.listOfServers;
	}

	public void println(String message) {
		this.textArea.append(message + "\n");
	}

	void addServer(Server newServer) {
		this.listOfServers.add(newServer);
	}

	void removeServer(Server server) {
		this.listOfServers.remove(server);
	}

	int getUserNo() {
		return this.userNo;
	}

	int getServerUsed() {
		return this.serverUsed;
	}
}
