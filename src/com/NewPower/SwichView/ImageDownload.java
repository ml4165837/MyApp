package com.NewPower.SwichView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.NewPower.MyApp.R;

public class ImageDownload extends Activity {

	private ImageButton downLoadButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swicthdownload);
		downLoadButton = (ImageButton)findViewById(R.id.downloadimagebutton);
		downLoadButton.setOnClickListener(new ButtonListener());
	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
			MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", "description");
			Toast.makeText(ImageDownload.this, "Í¼Æ¬ÒÑ±£´æµ½/DCIM/Camera", Toast.LENGTH_LONG).show();
		}
	}
}
