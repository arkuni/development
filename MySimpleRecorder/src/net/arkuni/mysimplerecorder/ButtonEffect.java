package net.arkuni.mysimplerecorder;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public abstract class ButtonEffect {
	private Animation xFlipOff;
	private Animation xFlipOn;
	private Animation yFlipOff;
	private Animation yFlipOn;
	private Context context;
	private View hideView;
	private View activeView;
	private Button clickBtn;
	private boolean isXFlip = true;
	private boolean isStarted = false;
	private Handler handler = new Handler();
	public ButtonEffect(Context context, View hideView, View activeView, Button clickBtn) {
		this.context = context;
		this.xFlipOff	= AnimationUtils.loadAnimation(this.context, R.anim.flip_x_off);
		this.xFlipOn	= AnimationUtils.loadAnimation(this.context, R.anim.flip_x_on);
		this.yFlipOff	= AnimationUtils.loadAnimation(this.context, R.anim.flip_y_off);
		this.yFlipOn	= AnimationUtils.loadAnimation(this.context, R.anim.flip_y_on);
		
		this.hideView = hideView;
		this.activeView = activeView;
		this.clickBtn = clickBtn;
	}
	
	protected void actionStart() {
		if (!isStarted) flipButton();
	}
	
	protected abstract void clickMainAction();
	
	protected void flipButton() {
		isStarted = true;
		final Animation flipOff = isXFlip ? xFlipOff : yFlipOff;
		final Animation flipOn = isXFlip ? xFlipOn : yFlipOn;
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					hideView.startAnimation(flipOff);
					
				} catch (Exception e) {
				}

			}
		});
		//handler.sendEmptyMessageDelayed(0, 300);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					hideView.setAnimation(null);
					hideView.setVisibility(View.INVISIBLE);
					activeView.startAnimation(flipOn);
					
				} catch (Exception e) {
				}

			}
		},300);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					activeView.setAnimation(null);
					activeView.setVisibility(View.VISIBLE);
					clickBtn.setClickable(true);
					isStarted = false;
					clickMainAction();
				} catch (Exception e) {
				}

			}
		},700);
	}

	public void setXFlip(boolean isXFlip) {
		this.isXFlip = isXFlip;
	}
}
