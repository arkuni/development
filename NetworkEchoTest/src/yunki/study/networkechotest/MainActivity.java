package yunki.study.networkechotest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String address = "yunki.study.networkechotest";
	private final Handler handler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new NetworkEchoServer().start();
        Button btn = (Button) findViewById(R.id.send_button);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LocalSocket localSocket = null;
				ObjectOutputStream toLocalServer = null;
				try {
					localSocket = new LocalSocket();
					localSocket.connect(new LocalSocketAddress(address));
					toLocalServer = new ObjectOutputStream(localSocket.getOutputStream());
					toLocalServer.writeObject("¾È³ç");
				} catch (Exception e) {
					Log.e("MainActivity","Error! ", e);
				} finally {
					if(toLocalServer != null) {
						try {
							toLocalServer.flush();
							toLocalServer.close();
						}catch (IOException e) {}
					}
					
					if(localSocket != null) {
						try {
							localSocket.close();
						} catch (IOException e) {}
					}
				}
				
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private class NotificationRunnable implements Runnable{
    	private String message = "";
    	public void run() {
    		if(!message.equals("")) {
    			Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    		}
			
		}
    	public void setMessage(String message) {
    		this.message = message;
    	}
    }
    
    private class NetworkEchoServer extends Thread {
    	private ObjectInputStream fromClient = null;
    	public void run() {
			LocalServerSocket localServerSocket = null;
			try {
				localServerSocket = new LocalServerSocket(address);
				NotificationRunnable notiRunnable = new NotificationRunnable();
				while(true){
					LocalSocket localSocket = localServerSocket.accept();
					if(localSocket != null) {
						fromClient = new ObjectInputStream(localSocket.getInputStream());
						notiRunnable.setMessage((String)fromClient.readObject());
						handler.post(notiRunnable);
					}
				}
			}catch (IOException e) {
				Log.e("NetworkEchoServer", "Error! ", e);
			}catch (ClassNotFoundException e) {
				Log.e("NetworkEchoServer", "Error! ", e);
			}
		}
    }
}
