package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import gameplay.Dealer;
import utilities.Validator;

public class Server {

	private ArrayList<Client> clients;
	private int noOfPlayers;
	private int playersReady;
	private ServerSocket socket;
	private Dealer dealer;
	public static final String START_MESSAGE = "START";
	public static final int START_COINS = 1000;

	public Server(int port) {
		this.clients = new ArrayList<Client>();
		ServerSocket socket = null;
		this.playersReady = 0;

		try {
			socket = new ServerSocket(port);
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
					noOfPlayers++;
				}
			}
		}
	}

	protected synchronized void ready(int playerNo) {
		this.playersReady++;
		broadcast("% " + playerNo + " READY");
	}

	private void startGame() {
		this.broadcast(START_MESSAGE);
		this.dealer = new Dealer(this, this.clients);
	}

	public synchronized void broadcast(String message) {
		synchronized (this.clients) {
			for (int i = 0; i < clients.size(); i++) {
				this.clients.get(i).broadcast(message);
			}
		}
	}

	public boolean isFull() {
		return (noOfPlayers == 6);
	}

	public static void main(String[] args) {
		String portStr;
		int port = -1;
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

		while (port == -1) {
			System.out.print("Please enter a valid port: ");
			portStr = keyboard.nextLine();
			if (Validator.isValidPort(portStr)) {
				port = Integer.parseInt(portStr);
			}
		}

		new Server(port);
	}
}