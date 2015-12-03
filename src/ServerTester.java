//import java.io.*;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.Scanner;
//
//import utilities.Validator;
//
//public class ServerTester {
//	private static Socket socket;
//	private static BufferedReader in;
//	private static PrintWriter out;
//	private static Scanner keyboard;
//
//	public static void main(String[] args) {
//		keyboard = new Scanner(System.in);
//
//		System.out.print("Please enter an IP address: ");
//		String ipAddress = keyboard.nextLine();
//
//		String portStr;
//		int port = -1;
//		if (args.length > 0) {
//			if (Validator.isValidPort(args[0])) {
//				port = Integer.parseInt(args[0]);
//			}
//		} else {
//			System.out.print("Please enter a port: ");
//			portStr = keyboard.nextLine();
//			if (Validator.isValidPort(portStr)) {
//				port = Integer.parseInt(portStr);
//			}
//		}
//
//		while (port == -1) {
//			System.out.print("Please enter a valid port: ");
//			portStr = keyboard.nextLine();
//			if (Validator.isValidPort(portStr)) {
//				port = Integer.parseInt(portStr);
//			}
//		}
//
//		try {
//			socket = new Socket(ipAddress, port);
//			// socket = new Socket("127.0.0.1", 5000);
//			in = new BufferedReader(new InputStreamReader(
//					socket.getInputStream()));
//			out = new PrintWriter(socket.getOutputStream());
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Thread t1 = new Thread(new ReadThread());
//		Thread t2 = new Thread(new WriteThread());
//
//		t1.run();
//		t2.run();
//	}
//
//	private static class ReadThread implements Runnable {
//		@Override
//		public void run() {
//			while (true) {
//				try {
//					if (in.ready()) {
//						System.out.println(in.readLine());
//					}
//				} catch (IOException e) {
//					System.out.println("Error reading");
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private static class WriteThread implements Runnable {
//		@Override
//		public void run() {
//			while (true) {
//				try {
//					out.println(keyboard.nextLine());
//					out.flush();
//				} catch (Exception e) {
//					System.out.println("Error writing");
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//}

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import utilities.Validator;

public class ServerTester {
	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);

		// System.out.print("Please enter an IP address: ");
		// String ipAddress = keyboard.nextLine();
		//
		// String portStr;
		// int port = -1;
		// if (args.length > 0) {
		// if (Validator.isValidPort(args[0])) {
		// port = Integer.parseInt(args[0]);
		// }
		// } else {
		// System.out.print("Please enter a port: ");
		// portStr = keyboard.nextLine();
		// if (Validator.isValidPort(portStr)) {
		// port = Integer.parseInt(portStr);
		// }
		// }
		//
		// while (port == -1) {
		// System.out.print("Please enter a valid port: ");
		// portStr = keyboard.nextLine();
		// if (Validator.isValidPort(portStr)) {
		// port = Integer.parseInt(portStr);
		// }
		// }

		try {
			// Socket socket = new Socket(ipAddress, port);
			socket = new Socket("127.0.0.1", 5000);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				out.println(keyboard.nextLine());
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}