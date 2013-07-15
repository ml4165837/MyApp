package com.NewPower.MyApp;

import android.os.Bundle;
import android.view.Window;

import com.NewPower.Shake.ThemeActivity;

public class AboutUsView extends ThemeActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus);
	}
}
