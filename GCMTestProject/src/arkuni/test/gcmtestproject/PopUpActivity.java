package arkuni.test.gcmtestproject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpActivity extends Activity {
	private Toast mToast;
	private Context curContext = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		curContext = PopUpActivity.this;
		setContentView(R.layout.popup);
		Intent i = getIntent();
		TextView popup = (TextView)findViewById(R.id.pop_txt);
		/*
		
		
		QRCodeWriter qr = new QRCodeWriter();
		String barcode = "6559123400001234|12345678";
		try {
			int width = 300;
			int height = 400;
					
			
			BitMatrix bitMatrix = qr.encode(barcode, BarcodeFormat.CODE_128, 300, 400);
			BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
			int [] imgpixel = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					//int grey = array[y][x] & 0xff;
					// pixels[y * width + x] = (0xff << 24) | (grey << 16) | (grey << 8) | grey;
					//imgpixel[y * width + x] = 0xff000000 | (0x00010101 * grey);
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	        bitmap.setPixels(imgpixel, 0, width, 0, 0, width, height);

		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			popup.setText(URLDecoder.decode(i.getStringExtra("msg"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
