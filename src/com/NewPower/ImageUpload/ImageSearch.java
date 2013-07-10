package com.NewPower.ImageUpload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.NewPower.Browser.BrowserView;
import com.NewPower.MyApp.R;

public class ImageSearch extends Activity {

	private static final String url = "http://101.227.253.253:8080/ImageServer/upServer";
	private static final String[] s = {"google", "baidu", "sougou", "tineye", "iqdb"};
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private ImageView imageShow;
	private TextView camera;
	private TextView choice;
	private String filename;
	private Bitmap upbitmap;
	private TextView upload;
	private String webUrl;
	private String requestUrl;
	private String googleUrl = "http://www.google.com.hk/searchbyimage?image_url=";
	private String sougouUrl = "http://pic.sogou.com/ris?query=";
	private String tineyeUrl = "http://www.tineye.com/search/?url=";
	private String iqdbUrl = "http://iqdb.org/?url=";
	private String baiduUrl = "http://stu.baidu.com/i?rt=0&rn=10&ct=1&tn=baiduimage&objurl=";
	private int tag = 0;
	
	private Handler handler;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageupload);
		imageShow = (ImageView)findViewById(R.id.imageShow);
		camera = (TextView)findViewById(R.id.camera);
		choice = (TextView)findViewById(R.id.choice);
		upload = (TextView)findViewById(R.id.upload);
		spinner = (Spinner)findViewById(R.id.spinner);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, s);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		camera.setOnClickListener(new CameraListener());
		choice.setOnClickListener(new ChoiceListener());
		upload.setOnClickListener(new UploadListener());
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		spinner.setVisibility(View.VISIBLE);
		handler=new MyHandler();
	}

	public class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
			// TODO Auto-generated method stub
			if("google" == s[position]) {
				requestUrl = googleUrl;
			} else if("baidu" == s[position]) {
				requestUrl = baiduUrl;
			} else if("sougou" == s[position]) {
				requestUrl = sougouUrl;
			} else if("tineye" == s[position]) {
				requestUrl = tineyeUrl;
			} else if("iqdb" == s[position]) {
				requestUrl = iqdbUrl;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	}
	public class CameraListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			filename = System.currentTimeMillis() + ".jpg";
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory(), filename)));
			startActivityForResult(intent, 1);
		}
	}
	
	public class ChoiceListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 2);
		}
	}
	
	public class UploadListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(tag != 0) {
				progressDialog = ProgressDialog.show(ImageSearch.this, "加载中...", "请等待...", true, true);
				new Thread(new Runnable() {
					public void run() {
						upload();
						webUrl = requestUrl + "http://101.227.253.253:8080/ImageServer/files/" + filename;
						Intent intent = new Intent();
						intent.setClass(ImageSearch.this, BrowserView.class);
						intent.putExtra("website", webUrl);
						startActivity(intent);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						finish();
						handler.sendMessage(new Message());
					}
				}).start();
			} else {
				Toast.makeText(ImageSearch.this, "请选择图片!", 100).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch (requestCode) {
		case 1:
			if(resultCode == Activity.RESULT_OK) {
				Bitmap bitmap=BitmapFactory.decodeFile(Environment.
						getExternalStorageDirectory().getPath()+"/"+filename);
				float wight=bitmap.getWidth();
				float height=bitmap.getHeight();
				imageShow.setImageBitmap(ZoomImg.zoomImage(bitmap, wight/10, height/10));
				upbitmap=ZoomImg.zoomImage(bitmap, wight/5, height/5);
				tag = 1;
			}
				break;
		case 2:
			if(data!=null){
				filename = System.currentTimeMillis() + ".jpg";
				System.out.println(getAbsoluteImagePath(data.getData()));
				upbitmap=BitmapFactory.decodeFile(getAbsoluteImagePath(data.getData()));
				float wight = upbitmap.getWidth();
				float height = upbitmap.getHeight();
				imageShow.setImageBitmap(ZoomImg.zoomImage(upbitmap, wight/10, height/10));
				upbitmap=ZoomImg.zoomImage(upbitmap, upbitmap.getWidth()/5, upbitmap.getHeight()/5);
				tag = 2;
			} else {
				tag = 0;
			}
			break;
		default:
			break;
		}
	}

	protected String getAbsoluteImagePath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void upload() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		upbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] b = stream.toByteArray();
		String file = new String(Base64.encodeLines(b));
		HttpClient client = new DefaultHttpClient();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("file", file));
		formparams.add(new BasicNameValuePair("filename", filename));
		HttpPost post = new HttpPost(url);
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity e = response.getEntity();
			System.out.println(EntityUtils.toString(e));
			if (200 == response.getStatusLine().getStatusCode()) {
				System.out.println("上传完成");
				System.out.println(filename);
			} else {
				System.out.println("上传失败");
				Toast.makeText(ImageSearch.this, "上传失败,请重新上传!", 100).show();
			}
			client.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
		}
	}
}