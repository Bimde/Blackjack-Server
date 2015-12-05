import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleClient {

	private Socket socket;
	private BufferedReader in, input;
	private PrintWriter out;
	static final String IP = "127.0.0.1";

	public SimpleClient() {
		try {
			this.socket = new Socket(IP, 5000);
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.out = new PrintWriter(this.socket.getOutputStream());
		} catch (Exception e) {
			System.err.println("Error connecting to server and getting input / output streams");
			e.printStackTrace();
		}

		new Thread() {
			public void run() {
				while (true) {
					try {
						System.out.println(SimpleClient.this.in.readLine());
					} catch (IOException e) {
						System.err.println("Error printing out message sent by server");
						e.printStackTrace();
					}
				}
			}
		}.start();

		this.input = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				this.out.println(this.input.readLine());
			} catch (IOException e) {
				System.err.println("Error sending message to server");
				e.printStackTrace();
			}
			this.out.flush();
		}
	}

	public static void main(String[] args) {
		new SimpleClient();
	}
}
