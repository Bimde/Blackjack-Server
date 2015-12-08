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
				Server availableServer=null;
				
				
				for (int serverNo = 0; serverNo < listOfServers.size(); serverNo++)
				{
					Server currentServer = listOfServers.get(serverNo);
					if (!currentServer.gameStarted() && !currentServer.isFull())
					{
						availableServer = currentServer;
						serverUsed = serverNo;
						break;
					}
				}
				
				if (availableServer==null)
				{
					availableServer = new Server();
					serverUsed = listOfServers.size();
					listOfServers.add(availableServer);
				}
				
				Client temp = new Client(client, availableServer);
				new Thread(temp).start();
				availableServer.addClient(temp);
			} catch (Exception e) {
				System.err.println("Error connecting to client");
				e.printStackTrace();
			}
			userNo++;
			serverUsed++;
			System.err.println("Client #" + userNo + " connected to server #" + serverUsed);
		}
	}

}
