package com.NewPower.Shake;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.Window;

import com.NewPower.MyApp.R;
import com.NewPower.Shake.ShakeListener.OnShakeListener;

public class ShakeAndShake extends ThemeActivity {
	
	private ShakeListener shakeListener;
	private Vibrator vibrator;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shakeview);
		vibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);


		shakeListener = new ShakeListener(this);
		shakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startVibrate();
	                Preference.setTheme(ShakeAndShake.this, theme == R.style.AppTheme_Default ? R.style.AppTheme_Another : R.style.AppTheme_Default);
	                reload();
	                shakeListener.stop();
			}
		});
	}
	public void startVibrate() {
		vibrator.vibrate(10);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (shakeListener != null) {
			shakeListener.stop();
		}
	}
}
