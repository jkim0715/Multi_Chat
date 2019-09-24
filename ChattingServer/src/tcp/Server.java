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

public class Server { // Server�� ���ϸ� �����

	boolean flag = true; // accept�� ���� while ���� �ȿ��� ���
	boolean rflag = true;
	Map<String, DataOutputStream> map = new HashMap<>(); // ip, port
	Map<String, String> map2 = new HashMap<>(); // ip, id
	Map<String, String> map3 = new HashMap<>(); // id, ip
	int num = 1;
	
	ServerSocket serverSocket;

	public Server() {
	}

	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port); // ServerSocket�� port(���)�� �ϰڴ�
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
		
		sender.setType(false);
		sender.setMsg(msg);
		
		sender.start();
	}

	public void sendMsgToTarget(String msg, String ip) {
		Sender sender = new Sender();
		
		sender.setType(true);
		sender.setMsg(msg);
		sender.setTarget(ip);
		
		sender.start();
	}
	// Inner Class
	class Sender extends Thread {
		String msg;
		boolean sendflag = true;
		String targetIp;
		
		public void setMsg(String msg) {
			this.msg = msg;
		}
		
		public void setType(boolean type) {
			this.sendflag = type;
		}
		
		public void setTarget(String ip) {
			this.targetIp = ip;
		}
		
		// ��ü-> msg~~~~
		// else -> "false,target,msg"
		public void run() {
			if(sendflag) {
				try {
					DataOutputStream output = map.get(targetIp);
					output.writeUTF(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else {
				Collection<DataOutputStream> col = map.values(); // values : key�� �����ϰ� value ����������
				Iterator<DataOutputStream> it = col.iterator();
			
				while (it.hasNext()) {
					try {
						DataOutputStream output = it.next();
						output.writeUTF(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	class Receiver extends Thread {
		Socket socket;
		String ip;
		
		InputStream in;
		DataInputStream din;
		
		OutputStream out;
		DataOutputStream dout;
		
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
			map2.put(ip, "unknown" + num);
			map3.put("unknown" + num, ip);
			num++;
			System.out.println("�����ڼ�:"+map.size());
		}

		public void run() {
			try {
				while (rflag) {
					String str = din.readUTF();

					
					if(str.equals("q")) {
						map3.remove(map2.get(ip));
						map2.remove(ip);
						map.remove(ip);
					
						System.out.println("Out");
						System.out.println("�����ڼ�:"+map.size());
						break;
					}
					
					String[] nic = str.split(" ");
					
					if(nic[0].equals("/�г���")) {
						if(map2.containsKey(socket.getInetAddress().toString())){
							map2.replace(socket.getInetAddress().toString(),nic[1]);
							
							map3.put(nic[1], socket.getInetAddress().toString());
						
						}
					}
					
					else if(nic[0].equals("/�ӼӸ�")) {
						// sendMsg2(ip, msg);
						if(map3.containsKey(nic[1])){
							String ip = map3.get(nic[1]);
							//To Target
							sendMsgToTarget(map2.get(socket.getInetAddress().toString()) + " : (�ӼӸ�) " + nic[2], ip);
							//To Me
							sendMsgToTarget(map2.get(socket.getInetAddress().toString()) + " : (�ӼӸ� to "+nic[1]+") :" + nic[2], socket.getInetAddress().toString());
							
						}
					}
					
					else if(nic[0].equals("/����Ʈ")) {
						String ip = socket.getInetAddress().toString();
						
						String listMsg = "��ȭ���\n";
						
						Collection<String> nics = map2.values();
						Iterator<String> it = nics.iterator();
						
						while(it.hasNext()) {
							String nick = it.next();
							listMsg += nick +"\n";
						}
						
						sendMsgToTarget(listMsg,ip);
					}
					
					else {
						sendMsg(map2.get(socket.getInetAddress().toString()) + " : " + str);
					}
					
					System.out.println(map2.get(socket.getInetAddress().toString()) + " : " + str);
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
