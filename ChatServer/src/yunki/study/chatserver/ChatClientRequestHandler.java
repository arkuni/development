package yunki.study.chatserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatClientRequestHandler implements Runnable{
	private Socket client;
	private static ArrayList<ChatClientRequestHandler> handlers = new ArrayList<ChatClientRequestHandler>(100);
	public ChatClientRequestHandler(Socket socket){
		this.client = socket;
	}
	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	protected Thread listener;
	
	public void clientRequestInitStart() {
		if(listener == null) {
			try {
				dataIn = new DataInputStream(new BufferedInputStream(client.getInputStream()));
				dataOut = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
				listener = new Thread(this);
				listener.start();
			} catch (IOException e) {
				System.err.println("error! : " + e);
			} 
		}
	}
	
	public void stop() {
		if(listener != null) {
			if(listener != Thread.currentThread()) {
				listener.interrupt();
			}
			listener = null;
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {}
			}
			if (dataOut != null) {
				try {
					dataOut.close();
				} catch (IOException e) {}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public void run() {
		try {
			handlers.add(this);
			while(!Thread.interrupted()) {
				String message = dataIn.readUTF();
				boadcastChatMessage(message);
			}
		} catch (EOFException e) {
			// TODO: handle exception
		} catch (IOException e) {
			if (listener == Thread.currentThread()) e.printStackTrace();
		} finally {
			handlers.remove(this);
			
		}
		stop();
	}
	private void boadcastChatMessage(String message) {
		synchronized (handlers) {
			Iterator<ChatClientRequestHandler> chatClients = handlers.iterator();
			while(chatClients.hasNext()) {
				ChatClientRequestHandler handler = chatClients.next();
				try {
					handler.dataOut.writeUTF(message);
					handler.dataOut.flush();
					
				} catch (IOException e) {
					handler.stop();
				}
			}
		}
	}
}
