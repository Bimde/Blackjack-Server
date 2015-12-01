package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

	private Server server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	private int playerNo;

	public Client(Socket client, Server server, int playerNo) {
		this.server = server;
		this.socket = client;
		this.playerNo = playerNo;
	}

	@Override
	public void run() {
		try {
			this.out = new PrintWriter(this.socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error getting client's output stream");
			e.printStackTrace();
		}
		try {
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Error getting client's input stream");
			e.printStackTrace();
		}
		try {
			this.name = in.readLine();
		} catch (IOException e) {
			System.out.println("Error getting client's name");
			e.printStackTrace();
		}
	}

	public String getName() {
		return this.name;
	}
}
