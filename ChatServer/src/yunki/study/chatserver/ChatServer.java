package yunki.study.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	private static boolean serverReady = false;
	public static void main(String[] args) throws IOException{
		ServerSocket server = new ServerSocket(6000);
		while(!serverReady) {
			Socket client = server.accept();
			System.out.println("cur ip : " + client.getInetAddress());
			ChatClientRequestHandler clientHandler = new ChatClientRequestHandler(client);
			clientHandler.clientRequestInitStart();
			
		}
	}
}
