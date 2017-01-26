package org.jbch.lifewordmission;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Typeface face=Typeface.createFromAsset(getAssets(), "fonts/TRGothic240.otf");
		Button a = (Button)findViewById(R.id.button1);
		a.setTypeface(face);
	}
}
