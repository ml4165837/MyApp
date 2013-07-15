package com.NewPower.Browser;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.NewPower.ImageUpload.ImageSearch;
import com.NewPower.MyApp.R;
import com.NewPower.Shake.ThemeActivity;

public class BrowserView extends ThemeActivity {

	private ImageView goreturn;
	private ImageView refresh;
	private ImageView goback;
	private ImageView forward;
	private ProgressBar progressBar;
	private WebView webview;
	private String ua = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Win64; x64; Trident/4.0)";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		goreturn = (ImageView)findViewById(R.id.Browser_head_back);
		refresh = (ImageView)findViewById(R.id.refresh);
		goback = (ImageView)findViewById(R.id.browser_foot_goback);
		forward = (ImageView)findViewById(R.id.browser_foot_forward);
		progressBar = (ProgressBar)findViewById(R.id.WebViewProgress);
		webview = (WebView)findViewById(R.id.Webview);
		webview.setInitialScale(50);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webview.requestFocus();
		Intent i = getIntent();
		String website = i.getStringExtra("website");
		System.out.println("website ---> " + website);
		webview.loadUrl(website);
		webview.getSettings().setUserAgentString(ua);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
		
		webview.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				progressBar.setProgress(newProgress);
				progressBar.setVisibility(View.VISIBLE);
				if(progressBar.getProgress() == 100) {
					progressBar.setVisibility(View.GONE);
				}
			}
		});
		
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.reload();
			}
		});
		
		goback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.goBack();
			}
		});
		
		forward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.goForward();
			}
		});
		
		goreturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BrowserView.this, ImageSearch.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right); 
				finish();
			}
		});
		webview.setOnLongClickListener( new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				HitTestResult result = webview.getHitTestResult();
		        int type = result.getType();  
		        switch (type) {
		        	//Á´½Ó
		            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
		                break;
		            //Í¼Æ¬
		            case WebView.HitTestResult.IMAGE_TYPE:  
		            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:  
		                break;  
		            default:  
		            //¿Õ°×
		                break;
		        }
				return true;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if( webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK ) {
			webview.goBack();
            return true;
         } else {
        	 Intent intent = new Intent();
        	 intent.setClass(BrowserView.this, ImageSearch.class);
        	 startActivity(intent);
        	 finish();
        	 return true;
         }
	}
}