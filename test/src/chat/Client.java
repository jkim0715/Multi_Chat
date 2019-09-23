package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	Socket socket;
	boolean rflag = true;

	public Client() {

	}

	public Client(String ip, int port) throws Exception {
		boolean flag = true;
		while (flag) {
			try {
				socket = new Socket(ip, port);
				if (socket != null && socket.isConnected()) {
					break;
				}
			} catch (IOException e) {
				System.out.println("Re-Try");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} // END while
		new Receiver(socket).start();
	}
	
	public void sendMsg(String msg) throws Exception {
		Sender sender = null;
		sender = new Sender(socket);
		sender.setMsg(msg);
		sender.start();
	}

	public void strat() throws Exception {
		Scanner sc = new Scanner(System.in);
		boolean sflag = true;
		while (sflag) {
			System.out.println("Input Msg.");
			String str = sc.nextLine();
			sendMsg(str);
			if(str.equals("q")) {
				break;
			}
		}
	}

	class Sender extends Thread {
		Socket socket;
		OutputStream out;
		DataOutputStream dout;
		String msg;
		
		public Sender() {
		}

		public Sender(Socket socket) throws Exception{
			this.socket = socket;
			out =socket.getOutputStream();
			dout = new DataOutputStream(out);
		}
		
		public void setMsg(String msg) {
			this.msg = msg;
		}

		public void run() {
			if(dout != null) {
				try {
					dout.writeUTF(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} // END Sender

	class Receiver extends Thread {
		Socket socket;
		InputStream in;
		DataInputStream din;

		public Receiver() {
		}

		// Receive data from server
		public Receiver(Socket socket) throws IOException {
			this.socket = socket;
			in = socket.getInputStream();
			din = new DataInputStream(in);

		}

		public void run() {
			try {
				while (rflag) {
					String str = din.readUTF();
					System.out.println(str);
				}
			} catch (Exception e) {

			}

		}
	}// END Receiver

	public static void main(String[] args) {
		Client client = null;

		try {
			client = new Client("70.12.60.108", 8888);
			client.strat();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
