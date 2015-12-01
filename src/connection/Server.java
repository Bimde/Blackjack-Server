package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	ArrayList<Client> clients;
	ServerSocket socket;
	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000;
	public static final int MIN_BET = 1;

	public Server() {
		this.clients = new ArrayList<Client>();
		ServerSocket socket = null;

		try {
			socket = new ServerSocket(5000);
		} catch (IOException e) {
			System.err.println("Error starting server.");
			e.printStackTrace();
		}
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

	protected synchronized void newPlayer(int playerNo, String name) {
		synchronized (this.clients) {
			for (int i = 0; i < clients.size(); i++) {
				if (i + 1 != playerNo) {
					this.clients.get(i).broadcast(playerNo + " " + name);
				}
			}
		}
	}

	private void startGame() {
		this.broadcast(START_MESSAGE);
	}

	public void broadcast(String message) {
		synchronized (this.clients) {
			for (int i = 0; i < clients.size(); i++) {
				this.clients.get(i).broadcast(message);
			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}

}
