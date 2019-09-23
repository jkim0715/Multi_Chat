package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Server { // Server는 소켓만 만든다

	boolean flag = true; // accept를 위한 while 루프 안에서 사용
	boolean rflag = true;
	Map<String, DataOutputStream> map = new HashMap<>(); // ip, port
	Map<String, String> map2 = new HashMap<>(); // ip, id
	ServerSocket serverSocket;

	public Server() { }

	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port); // ServerSocket을 port(몇번)로 하겠다
		System.out.println("Server Start..");
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					 while (flag) {
						System.out.println("Server Ready..");
						Socket socket = serverSocket.accept();
						new Receiver(socket).start();
						System.out.println(socket.getInetAddress());
					} 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}

	public void start() throws IOException {
		Scanner sc = new Scanner(System.in);
		boolean sflag = true;
		while (sflag) {
			System.out.println("Input Msg.");
			String str = sc.next();
			sendMsg(str);
			if (str.equals("q")) {
				break;
			}
		}
		System.out.println("Bye....");
		sc.close();
	}

	public void sendMsg(String msg) {
		Sender sender = new Sender();
		sender.setMsg(msg);
		sender.start();
	}

	// Inner Class
	class Sender extends Thread {

		String msg;

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public void run() {
			Collection<DataOutputStream> col = map.values(); // values : key값 무시하고 value 꺼낼수있음
			Iterator<DataOutputStream> it = col.iterator();
			while (it.hasNext()) {
				try {
					it.next().writeUTF(msg);
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
		OutputStream out;
		DataOutputStream dout;
		
		String ip;

		public Receiver() {

		}

		public Receiver(Socket socket) throws IOException {
			this.socket = socket;
			in = socket.getInputStream();
			din = new DataInputStream(in);

			out = socket.getOutputStream();
			dout = new DataOutputStream(out);
			ip = socket.getInetAddress().toString();
			map.put(ip, dout);
			System.out.println("접속자수:"+map.size());
		}

		public void run() {
			try {
				while (rflag) {
					String str = din.readUTF();
//					System.out.print(socket.getInetAddress()+": ");
					if(socket.getInetAddress().toString().equals("/70.12.60.108")) {
						System.out.print("지연 : ");
					}else if(socket.getInetAddress().toString().equals("/70.12.60.106")) {
						System.out.print("재영 : ");
					}else if(socket.getInetAddress().toString().equals("/70.12.60.99")) {
						System.out.print("지훈 : ");
					}else {
						System.out.print(socket.getInetAddress()+": ");
					}
					if(str.equals("q")) {
						map.remove(ip);
						System.out.println("Out");
						System.out.println("접속자수:"+map.size());
						break;
					}
					System.out.println(str);
					sendMsg(str);
				}
				if(socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			server = new Server(8888);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
