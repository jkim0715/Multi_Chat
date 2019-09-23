package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


// socket만들고 reciver 받을준비, scanner로 입력받아 key in 하면 sender에 의해 메시지 보내기
public class Client {

	Socket socket;
	boolean rflag = true;

	public Client() {

	}

	public Client(String ip, int port) throws IOException {
		boolean flag = true;
		while (flag) {
			try {
				socket = new Socket(ip, port);
				if (socket != null && socket.isConnected()) {
					break;
				}
			} catch (Exception e) { // 서버가 안켜져있으면
				System.out.println("Re-Try");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} // End while
		new Receiver(socket).start();
	}

	public void sendMsg(String msg) throws IOException {
		Sender sender = null;
		sender = new Sender(socket);
		sender.setMsg(msg);
		sender.start();
	}
	
	public void start() throws Exception {
		Scanner sc = new Scanner(System.in);
		boolean sflag = true;
		while (sflag) {
			System.out.println("Input Msg.");
			String str = sc.next();
			sendMsg(str);
			if(str.equals("q")) {
				break;
			}
		}
		sc.close();
	}

	// Inner Class
	class Sender extends Thread {
		Socket socket;
		OutputStream out;
		DataOutputStream dout;
		String msg;

		public Sender(Socket socket) throws IOException {
//			this.socket = socket;
			out = socket.getOutputStream();
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
	}

	class Receiver extends Thread {
		Socket socket;
		InputStream in;
		DataInputStream din;

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
	}

	public static void main(String[] args) {
		Client client = null;

		try {
			client = new Client("70.12.60.110", 8888);
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
