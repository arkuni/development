import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ThreadEchoServer {
	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(6000); //port no
		while(true) {
			Socket socket = serverSocket.accept();
			new ClientConnection(socket);
		}
	}
	
	private static class ClientConnection extends Thread{
		private Socket socket;
		private InputStream fromClient;
		private OutputStream toClient;
		private int bufsize = 512;
		public ClientConnection( Socket socket) throws IOException {
			System.out.println(socket + ": 연결됨");
			this.socket = socket;
			fromClient = socket.getInputStream();
			toClient = socket.getOutputStream();
			this.start();
		}
		public void run() {
			try {
				byte [] buf = new byte[bufsize];
				int data = 0;
				while((data=fromClient.read(buf))!=-1) {
					String message = new String(buf,0,data);
					toClient.write(buf,0,data);
					toClient.flush();
					System.out.println(socket + ": " + message);
					System.out.flush();
				}
				System.out.println(socket+": 정상 종료");
			}catch (IOException e) {
				e.printStackTrace();
				System.err.println(socket + ": 연결종료 ("+ e +")");
			}finally {
				if ( fromClient!=null) {
					try {
						fromClient.close();
					} catch (IOException e) {}
				}
				if ( toClient!=null) {
					try {
						toClient.close();
					} catch (IOException e) {}
				}
				try {
					socket.close();
				} catch (IOException e) {}
				
			}
		}
	}
}
