package arkuni.study.chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String CHAT_LOT = "CHATTING_LOG";
	private ArrayAdapter<String> messageItems;
	
	private ListView messagePanel;
	private Button sendBtn;
	private Button stopBtn;
	private Button connBtn;
	private EditText chatMsg;
	private ConnectionThread connThread;
	private Handler messageHandler =  new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_main);
        messageItems = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.message);
        messagePanel = (ListView)findViewById(R.id.message_panel);
        messagePanel.setAdapter(messageItems);
        chatMsg = (EditText)findViewById(R.id.chat_message);
        connBtn = (Button)findViewById(R.id.connectBtn);
        connBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				connThread = new ConnectionThread("192.168.3.49",6000);
				connThread.start();
				stopBtn.setEnabled(true);
				sendBtn.setEnabled(true);
				connBtn.setEnabled(false);
				
			}
		});
        sendBtn = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = chatMsg.getText().toString();
				if(message != null && message.length() > 0) {
					connThread.sendChatMessage(message);
					chatMsg.setText("");
				} else {
					Toast.makeText(getApplicationContext(), "메세지를 입력하세요.", Toast.LENGTH_SHORT).show();
				}
				chatMsg.requestFocus();
				
			}
		});
        stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( connThread != null && connThread.isAlive()) {
					
					connThread.interrupt();
					connThread.releaseThread();
					
				}
				connBtn.setEnabled(true);
				sendBtn.setEnabled(false);
				stopBtn.setEnabled(false);
				
			}
		});
		sendBtn.setEnabled(false);
		stopBtn.setEnabled(false);
        
    }
    private class ConnectionThread extends Thread {
    	private DataInputStream fromServer;
    	private DataOutputStream toServer;
    	private Socket socket;
    	private String chatName;
    	public ConnectionThread(String ipAddress, int portNo) {
    		chatName = "박윤기2 : ";
    		try {
				socket = new Socket(ipAddress, portNo);
				socket.setTcpNoDelay(true);
				fromServer = new DataInputStream(socket.getInputStream());
				toServer = new DataOutputStream(socket.getOutputStream());
				toServer.writeUTF(chatName + " entered. ");
				toServer.flush();
			} catch (IOException e) {
				Log.e(CHAT_LOT, "exception : " + e.toString());
			}
		}
    	@Override
    	public void run() {
    		try{
    			while(!isInterrupted()){

    				final String chatMessage = fromServer.readUTF();
    				messageHandler.post(new Runnable() {

    					@Override
    					public void run() {
    						messageItems.add(chatMessage);

    					}
    				});
    			}
    		} catch (IOException e) {
    			handleException(e);
    		} finally {
    			releaseThread();
    		}
    		
    	}
    	public void sendChatMessage(String message) {
    		try {
    			toServer.writeUTF(chatName+ message);
    			toServer.flush();
    		}catch (IOException e) {
    			Log.e(CHAT_LOT, "exception : " + e.toString());
			}
    	}
    	public synchronized void handleException(IOException e) {
    		if (this != Thread.currentThread()) {
    			this.interrupt();
    			Log.e(CHAT_LOT, "exception : " + e.toString());
    			releaseThread();
    		}
    	}
    	public synchronized void releaseThread() {
    		if(fromServer != null) {
    			try {
    				fromServer.close();
    				
    			} catch (IOException e) {}
    		}
    		if(toServer != null) {
    			try {
    				toServer.close();
    			} catch (IOException e) {}
    		}
    		if(socket != null) {
    			try {
    				socket.close();
    			} catch (IOException e) {}
    		}
    	}
    }
    public void onPause() {
    	super.onPause();
    	if (connThread != null && connThread.isAlive()) {
    		connThread.interrupt();
    		connThread.releaseThread();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
