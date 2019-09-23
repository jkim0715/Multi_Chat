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


//class ServerThread extends Thread{
//	//4.
//	Socket socket;
//	OutputStream out;
//	DataOutputStream dout;
//	
//	InputStream in;
//	DataInputStream din;
//		
//	//5.
//	public ServerThread(Socket socket) throws IOException {
//		this.socket = socket;
//		out = socket.getOutputStream();
//		dout = new DataOutputStream(out);	
//		
//		in =socket.getInputStream();
//		din = new DataInputStream(in);
//	}
//	
//	//6.
//	public void run() {
//		try {
//			String str = null;
//			str = din.readUTF();
//			System.out.println(socket.getInetAddress()+":"+str);
//			dout.writeUTF("똑똑 계세요~~?");
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally { 
//			//closing
//			if(dout != null) {
//				try {
//					dout.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if(din != null) {
//				try {
//					din.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if(socket != null) {
//				try {
//					socket.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//	}
//} // Thread END.


public class Server {
	//1.
	boolean flag = true;
	boolean rflag = true;
	Map<String, DataOutputStream> map = new HashMap<>();
	
	
	ServerSocket serverSocket;

	public Server() {
	
	}
	//2.
	public Server(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		System.out.println("Server Started");
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					while(flag) {
						System.out.println("Server Ready..");
						//wait...
						Socket socket = serverSocket.accept();
						new Receiver(socket).start();
						System.out.println(socket.getInetAddress());
						
						
					}
					System.out.println("Sever END");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		};
		new Thread(r).start();
	}
	
	public void start() throws IOException{
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
	
	
	
	public void sendMsg(String msg) {
		Sender sender = new Sender();
		sender.setMsg(msg);
		sender.start();
	}
	class Sender extends Thread {
		String msg;
				
		public void setMsg(String msg) {
			this.msg = msg;
		}

		public void run() {
				Collection<DataOutputStream> col = map.values();
				Iterator<DataOutputStream> it = col.iterator();
				while(it.hasNext()) {
					try {
						it.next().writeUTF(msg);
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

		
		OutputStream out;
		DataOutputStream dout;
		String ip;
		public Receiver() {
		}

		public Receiver(Socket socket) throws IOException {
			this.socket = socket;
			in = socket.getInputStream();
			din = new DataInputStream(in);
			out =socket.getOutputStream();
			dout = new DataOutputStream(out);
			ip = socket.getInetAddress().toString();
			map.put(ip, dout);
			System.out.println(map.size());
				
		}

		public void run() {
			try {
				while (rflag) {
					String str = din.readUTF();
					if(str.equals("q")){
						map.remove(ip);
						System.out.println(map.size());
						break;
					}
					System.out.println(str);
					sendMsg(str);
				}
				if(socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				System.out.println("비정상 아웃");
				map.remove(ip);
				System.out.println("남은 접속자수 :"+map.size());
				e.printStackTrace();
				
				
			}

		}
	}// END Receiver
	
	
	
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
