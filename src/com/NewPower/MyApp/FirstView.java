package com.NewPower.MyApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.NewPower.Shake.ThemeActivity;

public class FirstView extends ThemeActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.firstview, null);
		setContentView(view);
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f,1.0f);
		alphaAnimation.setDuration(2000);
		view.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				Intent intent = new Intent(FirstView.this, MainActivity.class);
		        startActivity(intent);
		        finish();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
		});
    }
}

