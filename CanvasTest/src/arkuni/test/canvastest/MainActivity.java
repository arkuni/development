package arkuni.test.canvastest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private int lcdH, lcdW;
	private SurfaceHolder _surfaceHolder;
	private Panel _panel;
	private MyThread _thread;
	private int shipposx = 240;
	private int shipposy = 600;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);     
		setContentView(new Panel(this));
	}
	
	class Panel extends SurfaceView implements SurfaceHolder.Callback{
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			synchronized(_thread.getSurfaceHolder()){
				Log.d("test", "MotionAction : " + event.getAction());
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_MOVE:
					shipposx = (int) event.getX();
					shipposy = (int) event.getY()-76;
					invalidate();
					break;
				}
				  
				return true;
			}
			
		}
		public Star star[]=new Star[100];
		private Bitmap airtmp;
		private Bitmap air;
		public Bitmap moon =BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		public Bitmap back;
		public Panel(Context context){
			super(context);
			airtmp = BitmapFactory.decodeResource(getResources(), R.drawable.air2);
			back = BitmapFactory.decodeResource(getResources(), R.drawable.back);
			getHolder().addCallback(this);
			_thread=new MyThread(getHolder(),this);
			Matrix matrix = new Matrix(); 
	  		matrix.preTranslate(100, 240);
	  		matrix.preRotate(-90);
	  		matrix.preTranslate(76, 23); //0,0에 갖다 놓는 역할
	  		air = Bitmap. createBitmap(airtmp, 0, 0, 152, 45, matrix, true); 
		}

		protected void onDraw(Canvas canvas){
			
	  		canvas.drawColor(Color.BLACK);
	  		canvas.drawBitmap(back,0,0,null);
	  		for(int i=0;i<100;i++){
	  			star[i].chkStar();
	  			star[i].drawStar(canvas);
	  		}
	  		canvas.drawBitmap(air,shipposx,shipposy,null);

		}  	

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
			lcdW= width;
			lcdH = height;
	  		for(int i=0;i<70;i++)star[i]=new Star(5, 2);
	  		for(int i=70;i<95;i++)star[i]=new Star(20, 6);
	  		for(int i=95;i<100;i++)star[i]=new Star(50, 16);
		}  	
		public void surfaceCreated(SurfaceHolder holder){
			_thread.setRunning(true);
			_thread.start();
		}  	
		public void surfaceDestroyed(SurfaceHolder holder){
			boolean retry=true;
			_thread.setRunning(false);

			while(retry){
				try{
					_thread.join();
					retry=false;
				}catch(InterruptedException e){  				
				}
			}
		}
	}
	class MyThread extends Thread {
		
		
		private boolean _run=false;

		public MyThread(SurfaceHolder surfaceHolder, Panel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}
		public void setRunning(boolean run) {
			_run = run;
		}
		public Object getSurfaceHolder(){
    		return _surfaceHolder;
    	}

		public void run(){
			Canvas c;
			while(_run) {
				c=null;
				try {
					c=_surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						_panel.onDraw(c);
					}
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					if(c!=null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}

	class Timer{
		long start, now, delay;
		int num, max;
		public Timer(int d, int m){
			delay = d;
			max = m;
		}
		void tStart() {
			start = System.currentTimeMillis();
		}
		int tChk(){
			now = System.currentTimeMillis();
			if(now-start>delay){
				num++;
				if(num>max-1)num=0;
				start = now-(now%delay);
			}
			return num;
		}
	}
	class Star{
		public float x,y,radius,spd;
		public int color;
		public Paint paint=new Paint();
		public Matrix mat = new Matrix();
		public Star(int r, int s){  		
			spd = s;
			radius=(float)r;
			x = (int)(Math.random()*lcdW);
			y = (int)(Math.random()*lcdH);
			color = -(int)(Math.random()*16777216);
			int tran = (int)(Math.random()*255);
			//Log.d("color", "Color.parseColor() : " + Color.parseColor("#ffffff"));
			paint.setColor(200*color);
		}
		void drawStar(Canvas c){
			float moonrad = radius/50;
			mat.reset();
			mat.preTranslate(x-radius, y-radius);
			mat.preScale(moonrad, moonrad);
			c.drawBitmap(_panel.moon, mat, null);
			c.drawCircle(x,y,radius,paint);
		}
		void chkStar(){
			y+=spd;
			if (y>800) {
				x = (int)(Math.random()*lcdW);
				y = 0 -(int)(Math.random()*30);
			}
			
		}
		int myRandom(int min, int max){
			return (int)(Math.random()*(max-min))+min;
		}
	}
}

