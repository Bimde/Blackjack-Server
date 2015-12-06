import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

public class BetterServerTester implements ActionListener, KeyListener {
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	static boolean running = true;
	String text;
	JFrame messenger = new JFrame("William");
	static JTextArea chatBox = new JTextArea();
	// Making a socket to get info from server.
	static Socket mySocket;
	JTextField messageBox = new JTextField();
	JButton send = new JButton("send");
	static PrintWriter output;
	Thread inputThread;

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		BetterServerTester client = new BetterServerTester();

	}

	public static class IdleTimer implements Runnable {

		public void run() {
			while (true) {
//				output.println("Connected");
//				output.flush();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}

		}

	}

	public BetterServerTester() throws UnknownHostException, IOException {

		chatBox.setSize(300, 300);
		chatBox.setLocation(0, 0);
		panel.setSize(300, 500);
		panel.setLayout(null);
		panel.add(chatBox);
		// JScrollPane pane = new JScrollPane();
		// pane.add(chatBox);
		// panel.add(pane);

		send.addActionListener(this);
		send.addKeyListener(this);
		send.setSize(100, 50);
		send.setLocation(200, 400);
		panel.add(send);

		messageBox.setLocation(0, 400);
		messageBox.setSize(200, 30);
		messageBox.setText("");
		panel.add(messageBox);

		frame.setSize(300, 500);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);

		chatBox.append("Waiting for server connection");
		mySocket = new Socket("192.168.0.11", 5000);
		// mySocket= new Socket("127.0.0.1",5001);
		chatBox.append("\nFound the server.\n");

		inputThread = new Thread(new InputHandler());
		inputThread.start();

		output = new PrintWriter(mySocket.getOutputStream());
	}

	public void actionPerformed(ActionEvent arg0) {
		String text = messageBox.getText();
		if (!text.trim().isEmpty()) {
			output.println(text);
			output.flush();
			messageBox.setText("");
		}

	}

	public void keyPressed(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_SPACE) {
			String text = messageBox.getText();
			if (!text.trim().isEmpty()) {
				output.println(text);
				output.flush();
				messageBox.setText("");

			}
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	private static class InputHandler implements Runnable {

		public void run() {
			// Getting input from the server.
			BufferedReader myReader = null;
			try {
				myReader = new BufferedReader(new InputStreamReader(
						mySocket.getInputStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Runnable timer = new IdleTimer();
			Thread idleTimer = new Thread(timer);
			idleTimer.start();

			String msg = "";

			while (running) {

				try {
					if (myReader.ready()) {
						try {
							msg = myReader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						chatBox.append(msg + "\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

}
