package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import utilities.Validator;

public class CentralServer {

	private static ServerSocket socket;

	private static ArrayList<Server>listOfServers;
	
	private static int userNo = 0;
	private static int serverUsed;
	
	public static void main(String[] args) throws IOException 
	{


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
		
		socket = new ServerSocket(port);
		listOfServers = new ArrayList<Server>();

		listOfServers.add(new Server());
		
		while (true) {
			System.err.println("Waiting for client to connect...");
			try {
				Socket client = socket.accept();
				
				Client temp = new Client(client);
				new Thread(temp).start();
				userNo++;
				System.err.println("Client #" + userNo + " has connected");
			}
		 catch (Exception e) {
			System.err.println("Error connecting to client");
			e.printStackTrace();
		}
		}
	}

	public static ServerSocket getSocket() {
		return socket;
	}

	public static void setSocket(ServerSocket socket) {
		CentralServer.socket = socket;
	}

	public static ArrayList<Server> getListOfServers() {
		return listOfServers;
	}

	public static void setListOfServers(ArrayList<Server> listOfServers) {
		CentralServer.listOfServers = listOfServers;
	}
	
	public static void addServer(Server newServer) {
		listOfServers.add(newServer);
	}

	public static int getUserNo() {
		return userNo;
	}

	public static void setUserNo(int userNo) {
		CentralServer.userNo = userNo;
	}

	public static int getServerUsed() {
		return serverUsed;
	}

	public static void setServerUsed(int serverUsed) {
		CentralServer.serverUsed = serverUsed;
	}

}
