package com.NewPower.MyApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.NewPower.Aviary.AviaryActivity;
import com.NewPower.ImageUpload.ImageSearch;
import com.NewPower.Shake.ShakeAndShake;
import com.NewPower.Shake.ThemeActivity;
import com.NewPower.SwichView.OnViewChangeListener;
import com.NewPower.SwichView.ScrollLayout;
import com.baidu.sharesdk.BaiduShareException;
import com.baidu.sharesdk.BaiduSocialShare;
import com.baidu.sharesdk.ShareContent;
import com.baidu.sharesdk.ShareListener;
import com.baidu.sharesdk.Utility;
import com.baidu.sharesdk.ui.BaiduSocialShareUserInterface;

@SuppressWarnings("deprecation")
public class MainActivity extends ThemeActivity {
	
	private final static String ApiKey = "VE6GdaZ9v70aCYanoP2kW5mU";
	//private ImageView topButton;
	private ImageView leftButton;
	private ImageView rightTopButton;
	private ImageView rightDownButton;
	private ImageView slidingButton1, slidingButton2;
	private ScrollLayout scrollLayout;
	private SlidingDrawer slidingDrawer;
	private long exit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//topButton = (ImageView)findViewById(R.id.top);
		leftButton = (ImageView)findViewById(R.id.left);
		rightTopButton = (ImageView)findViewById(R.id.rightTop);
		rightDownButton = (ImageView)findViewById(R.id.rightDown);
		slidingButton1 = (ImageView)findViewById(R.id.slidingButton1);
		slidingButton2 = (ImageView)findViewById(R.id.slidingButton2);
		rightTopButton.setOnTouchListener(new RightTopButtonListener());
		rightDownButton.setOnTouchListener(new RightDownButtonListener());
		leftButton.setOnTouchListener(new ShakeButtonListener());
		slidingButton1.setOnTouchListener(new SlidingButtonListener1());
		slidingButton2.setOnTouchListener(new SlidingButtonListener2());
		scrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayout); 	
    	scrollLayout.SetOnViewChangeListener(new ScrollLayoutlistener());
    	slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
    	//scrollLayout.setOnTouchListener(new ScrollLayoutTouchlistener());
	}

	/*class ScrollLayoutTouchlistener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() != MotionEvent.ACTION_MOVE) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ImageDownload.class);
				startActivity(intent);
			}
			return true;
		}
	}*/
	
	class RightDownButtonListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_UP) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AviaryActivity.class);
				startActivity(intent);
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN){  
                /*v.setBackgroundResource(R.drawable.left);  */
            }else if(event.getAction()==MotionEvent.ACTION_UP){  
               // v.setBackgroundResource(R.drawable.top); 
            }
			return false;
		}
	}
	
	class ScrollLayoutlistener implements OnViewChangeListener {

		@Override
		public void OnViewChange(int view) {
			// TODO Auto-generated method stub
			
		}
	}
	class RightTopButtonListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_UP) {
				Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_LONG).show();
				System.out.println("chick");
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ImageSearch.class);
				startActivity(intent);
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN){  
                /*v.setBackgroundResource(R.drawable.left);  */
            }else if(event.getAction()==MotionEvent.ACTION_UP){  
               // v.setBackgroundResource(R.drawable.top); 
            }
			return false;
		}
	}
	
	class ShakeButtonListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_UP) {
				Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_LONG).show();
				System.out.println("chick");
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ShakeAndShake.class);
				startActivity(intent);
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN){  
                /*v.setBackgroundResource(R.drawable.left);  */
            }else if(event.getAction()==MotionEvent.ACTION_UP){  
               // v.setBackgroundResource(R.drawable.top); 
            }
			return false;
		}
	}
	
	class SlidingButtonListener1 implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_UP) {
				Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_LONG).show();
				System.out.println("chick");
				startSharing();
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN){  
                v.setBackgroundResource(R.drawable.firstslidingbutton_press);  
            }else if(event.getAction()==MotionEvent.ACTION_UP){  
                v.setBackgroundResource(R.drawable.firstslidingbutton); 
            }
			return false;
		}
	}
	
	class SlidingButtonListener2 implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_UP) {
				Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AboutUsView.class);
				startActivity(intent);
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN){  
                v.setBackgroundResource(R.drawable.secondslidingbutton_press);  
            }else if(event.getAction()==MotionEvent.ACTION_UP){  
                v.setBackgroundResource(R.drawable.secondslidingbutton); 
            }
			return false;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(slidingDrawer.isOpened()) {
			slidingDrawer.close();
		} else {
			if(keyCode == KeyEvent.KEYCODE_BACK) {
				if((System.currentTimeMillis() - exit ) > 2000) {
					Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_LONG).show();
					exit = System.currentTimeMillis();
				} else {
					finish();
				}
				return true;
			}
		}
		return true;
	}
private void startSharing(){
		
		// create baidu social share instance
		BaiduSocialShare bss = BaiduSocialShare.getInstance(this, ApiKey);
		
		// create content to share
		ShareContent content = new ShareContent();
		content.setTitle("Test");
		content.setContent("测试专用");
		content.setUrl("http://www.baidu.com");
		
		// create the user interface
		BaiduSocialShareUserInterface bssui = bss.getSocialShareUserInterfaceInstance();
		
		// start to share
		bssui.showShareMenu(this, content, Utility.SHARE_THEME_STYLE, new ShareListener(){

			@Override
			public void onApiComplete(String arg0) {
				Toast.makeText(MainActivity.this, "on api complete " + arg0, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAuthComplete(Bundle arg0) {
				Toast.makeText(MainActivity.this, "on auth complete", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onError(BaiduShareException arg0) {
				Toast.makeText(MainActivity.this, "on error", Toast.LENGTH_LONG).show();
			}
		});
	}
}
