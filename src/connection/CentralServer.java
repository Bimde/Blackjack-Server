package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import utilities.Validator;

public class CentralServer {

	private ServerSocket socket;
	private ArrayList<Server> listOfServers;
	private int userNo = 0;
	private int serverUsed;

	public static void main(String[] args) {
		new CentralServer(args);
	}

	public CentralServer(String[] args) {
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

		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Error creating a new server socket on port " + port);
			e.printStackTrace();
		}
		this.listOfServers = new ArrayList<Server>();
		this.listOfServers.add(new Server());

		while (true) {
			System.err.println("Waiting for client to connect...");
			try {
				Socket client = this.socket.accept();
				Client temp = new Client(client, this);
				new Thread(temp).start();
				this.userNo++;
				System.err.println("Client #" + userNo + " has connected");
			} catch (Exception e) {
				System.err.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}

	public ServerSocket getSocket() {
		return this.socket;
	}

	public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}

	public ArrayList<Server> getListOfServers() {
		return this.listOfServers;
	}

	public void setListOfServers(ArrayList<Server> listOfServers) {
		this.listOfServers = listOfServers;
	}

	public void addServer(Server newServer) {
		this.listOfServers.add(newServer);
	}

	public int getUserNo() {
		return this.userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

	public int getServerUsed() {
		return this.serverUsed;
	}

	public void setServerUsed(int serverUsed) {
		this.serverUsed = serverUsed;
	}

}
