package com.NewPower.Aviary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.NewPower.MyApp.R;
import com.NewPower.Shake.ThemeActivity;
import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.headless.AviaryExecutionException;
import com.aviary.android.feather.headless.AviaryInitializationException;
import com.aviary.android.feather.headless.filters.NativeFilterProxy;
import com.aviary.android.feather.headless.media.ExifInterfaceWrapper;
import com.aviary.android.feather.headless.moa.MoaHD;
import com.aviary.android.feather.headless.utils.IOUtils;
import com.aviary.android.feather.headless.utils.MegaPixels;
import com.aviary.android.feather.headless.utils.StringUtils;
import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.library.providers.FeatherContentProvider;
import com.aviary.android.feather.library.providers.FeatherContentProvider.ActionsDbColumns.Action;
import com.aviary.android.feather.library.utils.DecodeUtils;
import com.aviary.android.feather.library.utils.ImageLoader.ImageSizes;
import com.aviary.android.feather.library.utils.SystemUtils;

public class AviaryActivity extends ThemeActivity {
	
	private static final int ACTION_REQUEST_GALLERY = 99;
	private static final int ACTION_REQUEST_FEATHER = 100;
	private static final int EXTERNAL_STORAGE_UNAVAILABLE = 1;
	public static final String LOG_TAG = "image-launcher";
	private static final String API_KEY = "me2s6y513v8tt2li";
	private static final String FOLDER_NAME = "weitu";

	Button galleryButton;
	Button editButton;
	ImageView image;
	View imageContainer;
	String outputFilePath;
	Uri imageUri;
	int imageWidth, imageHeight;
	private File galleryFolder;
	private String sessionId;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate( savedInstanceState );
		setContentView( R.layout.imageeditingview );

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		imageWidth = (int) ( (float) metrics.widthPixels / 1.5 );
		imageHeight = (int) ( (float) metrics.heightPixels / 1.5 );

		galleryButton.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View v ) {
				pickFromGallery();
			}
		} );

		editButton.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View v ) {
				if ( imageUri != null ) {
					startFeather( imageUri );
				}
			}
		} );

		imageContainer.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View v ) {
				Uri uri = pickRandomImage();
				if ( uri != null ) {
					loadAsync( uri );
				}
			}
		} );

		imageContainer.setLongClickable( true );
		imageContainer.setOnLongClickListener( new OnLongClickListener() {

			@Override
			public boolean onLongClick( View v ) {
				if ( imageUri != null ) {
					openContextMenu( v );
					return true;
				}
				return false;
			}
		} );

		galleryFolder = createFolders();
		registerForContextMenu( imageContainer );
	}

	@Override
	protected void onResume() {
		super.onResume();

		if ( getIntent() != null ) {
			handleIntent( getIntent() );
			setIntent( new Intent() );
		}
	}

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );
		menu.setHeaderTitle( "Menu" );
		menu.add( 0, 0, 0, "Details" );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item ) {

		final int order = item.getOrder();
		switch ( order ) {
			case 0:
				showCurrentImageDetails();
				return true;
		}

		return super.onContextItemSelected( item );
	}

	private void handleIntent( Intent intent ) {

		String action = intent.getAction();

		if ( null != action ) {

			if ( Intent.ACTION_SEND.equals( action ) ) {

				Bundle extras = intent.getExtras();
				if ( extras != null && extras.containsKey( Intent.EXTRA_STREAM ) ) {
					Uri uri = (Uri) extras.get( Intent.EXTRA_STREAM );
					loadAsync( uri );
				}
			} else if ( Intent.ACTION_VIEW.equals( action ) ) {
				Uri data = intent.getData();
				loadAsync( data );
			}
		}
	}

	private void loadAsync( final Uri uri ) {

		Drawable toRecycle = image.getDrawable();
		if ( toRecycle != null && toRecycle instanceof BitmapDrawable ) {
			if ( ( (BitmapDrawable) image.getDrawable() ).getBitmap() != null )
				( (BitmapDrawable) image.getDrawable() ).getBitmap().recycle();
		}
		image.setImageDrawable( null );
		imageUri = null;

		DownloadAsync task = new DownloadAsync();
		task.execute( uri );
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		outputFilePath = null;
	}

	private void showCurrentImageDetails() {
		if ( null != imageUri ) {
			ImageInfo info;
			try {
				info = new ImageInfo( this, imageUri );
			} catch ( IOException e ) {
				e.printStackTrace();
				return;
			}

			if ( null != info ) {
				Intent intent = new Intent( this, ImageInfoActivity.class );
				intent.putExtra( "image-info", info );
				startActivity( intent );
			}
		}
	}

	private boolean deleteFileNoThrow( String path ) {
		File file;
		try {
			file = new File( path );
		} catch ( NullPointerException e ) {
			return false;
		}

		if ( file.exists() ) {
			return file.delete();
		}
		return false;
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		galleryButton = (Button) findViewById( R.id.button1 );
		editButton = (Button) findViewById( R.id.button2 );
		image = ( (ImageView) findViewById( R.id.image ) );
		imageContainer = findViewById( R.id.image_container );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.main, menu );
		return true;
	}

	@Override
	protected Dialog onCreateDialog( int id ) {
		Dialog dialog = null;
		switch ( id ) {
			case EXTERNAL_STORAGE_UNAVAILABLE:
				dialog = new AlertDialog.Builder( this ).setTitle( R.string.external_storage_na_title )
						.setMessage( R.string.external_storage_na_message ).create();
				break;
		}
		return dialog;
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		if ( resultCode == RESULT_OK ) {
			switch ( requestCode ) {
				case ACTION_REQUEST_GALLERY:
					loadAsync( data.getData() );
					break;

				case ACTION_REQUEST_FEATHER:
					
					boolean changed = true;
					
					if( null != data ) {
						Bundle extra = data.getExtras();
						if( null != extra ) {
							changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
						}
					}
					
					if( !changed ) {
					}
					
					updateMedia( outputFilePath );
					
					loadAsync( data.getData() );
					onSaveCompleted( outputFilePath );
					outputFilePath = null;
					break;
			}
		} else if ( resultCode == RESULT_CANCELED ) {
			switch ( requestCode ) {
				case ACTION_REQUEST_FEATHER:

					if ( null != sessionId) deleteSession( sessionId );

					if ( outputFilePath != null ) {
						deleteFileNoThrow( outputFilePath );
						outputFilePath = null;
					}
					break;
			}
		}
	}

	private void onSaveCompleted( final String filepath ) {

		if ( sessionId != null ) {

			OnClickListener yesListener = new OnClickListener() {

				@Override
				public void onClick( DialogInterface dialog, int which ) {
					if ( null != sessionId ) {
						processHD( sessionId );
					}
					sessionId = null;
				}
			};

			OnClickListener noListener = new OnClickListener() {

				@Override
				public void onClick( DialogInterface dialog, int which ) {

					if ( null != sessionId ) {
						deleteSession( sessionId );
					}

					if ( !isFinishing() ) {
						dialog.dismiss();
					}
					sessionId = null;
				}
			};

			Dialog dialog = new AlertDialog.Builder( this ).setTitle( "HiRes" )
					.setMessage( "一个低分辨路的图片已经生成了,你还想生成一个高分辨率的图片吗?" )
					.setPositiveButton( android.R.string.yes, yesListener ).setNegativeButton( android.R.string.no, noListener )
					.setCancelable( false ).create();

			dialog.show();
		}
	}

	@SuppressWarnings("deprecation")
	private boolean setImageURI( final Uri uri, final Bitmap bitmap ) {

		Log.d( LOG_TAG, "image size: " + bitmap.getWidth() + "x" + bitmap.getHeight() );
		image.setImageBitmap( bitmap );
		image.setBackgroundDrawable( null );

		editButton.setEnabled( true );
		imageUri = uri;
		return true;
	}

	private void updateMedia( String filepath ) {
		Log.i( LOG_TAG, "updateMedia: " + filepath );
		MediaScannerConnection.scanFile( getApplicationContext(), new String[] { filepath }, null, null );
	}

	@SuppressWarnings("unused")
	private Uri pickRandomImage() {
		Cursor c = getContentResolver().query( Images.Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns._ID, ImageColumns.DATA },
				ImageColumns.SIZE + ">?", new String[] { "90000" }, null );
		Uri uri = null;

		if ( c != null ) {
			int total = c.getCount();
			int position = (int) ( Math.random() * total );
			Log.d( LOG_TAG, "pickRandomImage. total images: " + total + ", position: " + position );
			if ( total > 0 ) {
				if ( c.moveToPosition( position ) ) {
					String data = c.getString( c.getColumnIndex( Images.ImageColumns.DATA ) );
					long id = c.getLong( c.getColumnIndex( Images.ImageColumns._ID ) );
					
					uri = Uri.parse( data );
					
					Log.d( LOG_TAG, uri.toString() );
				}
			}
			c.close();
		}
		return uri;
	}
	private File getNextFileName() {
		if ( galleryFolder != null ) {
			if ( galleryFolder.exists() ) {
				File file = new File( galleryFolder, "aviary_" + System.currentTimeMillis() + ".jpg" );
				return file;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private void startFeather( Uri uri ) {

		if ( !isExternalStorageAvilable() ) {
			showDialog( EXTERNAL_STORAGE_UNAVAILABLE );
			return;
		}

		File file = getNextFileName();
		
		
		if ( null != file ) {
			outputFilePath = file.getAbsolutePath();
		} else {
			new AlertDialog.Builder( this ).setTitle( android.R.string.dialog_alert_title ).setMessage( "Failed to create a new File" )
					.show();
			return;
		}
		
		Intent newIntent = new Intent( this, FeatherActivity.class );

		newIntent.setData( uri );

		newIntent.putExtra( Constants.EXTRA_OUTPUT, Uri.parse( "file://" + outputFilePath ) );

		newIntent.putExtra( Constants.EXTRA_OUTPUT_FORMAT, Bitmap.CompressFormat.JPEG.name() );

		newIntent.putExtra( Constants.EXTRA_OUTPUT_QUALITY, 90 );
		final DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( metrics );
		int max_size = Math.max( metrics.widthPixels, metrics.heightPixels );
		max_size = (int) ( (float) max_size / 1.2f );
		newIntent.putExtra( Constants.EXTRA_MAX_IMAGE_SIZE, max_size );

		sessionId = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
		newIntent.putExtra( Constants.EXTRA_OUTPUT_HIRES_SESSION_ID, sessionId );

		newIntent.putExtra( Constants.EXTRA_IN_SAVE_ON_NO_CHANGES, true );
		
		startActivityForResult( newIntent, ACTION_REQUEST_FEATHER );
	}

	private boolean isExternalStorageAvilable() {
		String state = Environment.getExternalStorageState();
		if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
			return true;
		}
		return false;
	}

	private void pickFromGallery() {
		Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
		intent.setType( "image/*" );

		Intent chooser = Intent.createChooser( intent, "选择照片" );
		startActivityForResult( chooser, ACTION_REQUEST_GALLERY );
	}

	private File createFolders() {

		File baseDir;

		if ( android.os.Build.VERSION.SDK_INT < 8 ) {
			baseDir = Environment.getExternalStorageDirectory();
		} else {
			baseDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES );
		}

		if ( baseDir == null ) return Environment.getExternalStorageDirectory();

		Log.d( LOG_TAG, "Pictures folder: " + baseDir.getAbsolutePath() );
		File aviaryFolder = new File( baseDir, FOLDER_NAME );

		if ( aviaryFolder.exists() ) return aviaryFolder;
		if ( aviaryFolder.mkdirs() ) return aviaryFolder;

		return Environment.getExternalStorageDirectory();
	}

	private void processHD( final String session_name ) {

		File destination = getNextFileName();

		try {
			if ( destination == null || !destination.createNewFile() ) {
				return;
			}
		} catch ( IOException e ) {
			Toast.makeText( this, e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
			return;
		}

		String error = null;

		FeatherContentProvider.SessionsDbColumns.Session session = null;

		Uri sessionUri = FeatherContentProvider.SessionsDbColumns.getContentUri( this, session_name );

		Cursor cursor = getContentResolver().query( sessionUri, null, null, null, null );

		if ( null != cursor ) {
			session = FeatherContentProvider.SessionsDbColumns.Session.Create( cursor );
			cursor.close();
		}

		if ( null != session ) {
			Uri actionsUri = FeatherContentProvider.ActionsDbColumns.getContentUri( this, session.session );

			cursor = getContentResolver().query( actionsUri, null, null, null, null );

			if ( null != cursor ) {
				HDAsyncTask task = new HDAsyncTask( Uri.parse( session.file_name ), destination.getAbsolutePath(), session_name );
				task.execute( cursor );
			} else {
				error = "无法检索的操作列表!";
			}
		} else {
			error = "无法检索会话信息";
		}

		if ( null != error ) {
			Toast.makeText( this, error, Toast.LENGTH_LONG ).show();
		}
	}

	private void deleteSession( final String session_id ) {
		Uri uri = FeatherContentProvider.SessionsDbColumns.getContentUri( this, session_id );
		getContentResolver().delete( uri, null, null );
	}

	private class HDAsyncTask extends AsyncTask<Cursor, Integer, String> {

		Uri uri_;
		String dstPath_;
		ProgressDialog progress_;
		String session_;
		ExifInterfaceWrapper exif_;

		public HDAsyncTask( Uri source, String destination, String session_id ) {
			uri_ = source;
			dstPath_ = destination;
			session_ = session_id;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_ = new ProgressDialog( AviaryActivity.this );
			progress_.setIndeterminate( true );
			progress_.setTitle( "Processing Hi-res image" );
			progress_.setMessage( "Loading image..." );
			progress_.setProgressStyle( ProgressDialog.STYLE_SPINNER );
			progress_.setCancelable( false );
			progress_.show();
		}

		@Override
		protected void onProgressUpdate( Integer... values ) {
			super.onProgressUpdate( values );
			final int index = values[0];
			final int total = values[1];
			String message = "";
			if ( index == -1 )
				message = "Saving image...";
			else
				message = "Applying action " + ( index + 1 ) + " of " + ( total );
			progress_.setMessage( message );
		}

		@Override
		protected String doInBackground( Cursor... params ) {
			Cursor cursor = params[0];

			if ( null != cursor ) {

				try {
					NativeFilterProxy.init( getBaseContext(), API_KEY );
				} catch ( AviaryInitializationException e ) {
					return e.getMessage();
				}
				
				
				MoaHD moa = new MoaHD();
				moa.setMaxMegaPixels( MegaPixels.Mp30 );
				boolean loaded;
				try {
					loaded = loadImage( moa );
				} catch ( AviaryExecutionException e ) {
					return e.getMessage();
				}

				if ( loaded ) {
					final int total_actions = cursor.getCount();
					if ( cursor.moveToFirst() ) {
						do {
							publishProgress( cursor.getPosition(), total_actions );
							Action action = Action.Create( cursor );
							if ( null != action ) {
								moa.applyActions( action.getActions() );
							} else {
							}

						} while ( cursor.moveToNext() );
					}

					publishProgress( -1, -1 );
					
					try {
						moa.save( dstPath_ );
					} catch ( AviaryExecutionException e ) {
						return e.getMessage();
					} finally {
						moa.dispose();
					}

					if ( null != exif_ ) {
						saveExif( exif_, dstPath_ );
					}

				} else {
					return "加载图片失败咯";
				}
				
				cursor.close();

				if( moa.isLoaded() ) {
					try {
						moa.unload();
					} catch ( AviaryExecutionException e ) {}
				}
				
				if( !moa.isDisposed() ) {
					moa.dispose();
				}
			}

			return null;
		}

		private void saveExif( ExifInterfaceWrapper originalExif, String filename ) {
			ExifInterfaceWrapper newExif = null;
			try {
				newExif = new ExifInterfaceWrapper( dstPath_ );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
			if ( null != newExif && null != originalExif ) {
				originalExif.copyTo( newExif );
				newExif.setAttribute( ExifInterfaceWrapper.TAG_ORIENTATION, "0" );
				newExif.setAttribute( ExifInterfaceWrapper.TAG_SOFTWARE, "Aviary " + FeatherActivity.SDK_VERSION );
				newExif.setAttribute( ExifInterfaceWrapper.TAG_DATETIME, ExifInterfaceWrapper.formatDate( new Date() ) );
				try {
					newExif.saveAttributes();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPostExecute( String errorString ) {
			super.onPostExecute( errorString );

			if ( progress_.getWindow() != null ) {
				progress_.dismiss();
			}

			if ( null != errorString ) {
				Toast.makeText( AviaryActivity.this, "这儿有个错误: " + errorString, Toast.LENGTH_SHORT ).show();
				return;
			}

			updateMedia( dstPath_ );

			new AlertDialog.Builder( AviaryActivity.this ).setTitle( "File saved" )
					.setMessage( "文件保存在 " + dstPath_ + "中，你想看看高清文件吗?" )
					.setPositiveButton( android.R.string.yes, new OnClickListener() {

						@Override
						public void onClick( DialogInterface dialog, int which ) {

							Intent intent = new Intent( Intent.ACTION_VIEW );

							String filepath = dstPath_;
							if ( !filepath.startsWith( "file:" ) ) {
								filepath = "file://" + filepath;
							}
							intent.setDataAndType( Uri.parse( filepath ), "image/jpeg" );
							startActivity( intent );

						}
					} ).setNegativeButton( android.R.string.no, null ).show();

			deleteSession( session_ );
		}

		private boolean loadImage( MoaHD moa ) throws AviaryExecutionException {
			final String srcPath = IOUtils.getRealFilePath( AviaryActivity.this, uri_ );
			if ( srcPath != null ) {

				try {
					exif_ = new ExifInterfaceWrapper( srcPath );
				} catch ( IOException e ) {
					e.printStackTrace();
				}
				moa.load( srcPath );
				return true;
				
			} else {

				if ( SystemUtils.isHoneyComb() ) {
					InputStream stream = null;
					try {
						stream = getContentResolver().openInputStream( uri_ );
					} catch ( IOException e ) {
						e.printStackTrace();
						return false;
					}
					
					moa.load( stream );
					return true;
					
				} else {
					ParcelFileDescriptor fd = null;
					try {
						fd = getContentResolver().openFileDescriptor( uri_, "r" );
					} catch ( FileNotFoundException e ) {
						e.printStackTrace();
						return false;
					}
					
					moa.load( fd.getFileDescriptor() );
					return true;
				}
			}
		}
	}

	class DownloadAsync extends AsyncTask<Uri, Void, Bitmap> implements OnCancelListener {

		ProgressDialog progress;
		private Uri uri;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = new ProgressDialog( AviaryActivity.this );
			progress.setIndeterminate( true );
			progress.setCancelable( true );
			progress.setMessage( "正在加载哈..." );
			progress.setOnCancelListener( this );
			progress.show();
		}

		@Override
		protected Bitmap doInBackground( Uri... params ) {
			uri = params[0];
			Bitmap bitmap = null;

			while ( imageContainer.getWidth() < 1 ) {
				try {
					Thread.sleep( 1 );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}

			ImageSizes sizes = new ImageSizes();
			bitmap = DecodeUtils.decode( AviaryActivity.this, uri, imageWidth, imageHeight, sizes );
			return bitmap;
		}

		@Override
		protected void onPostExecute( Bitmap result ) {
			super.onPostExecute( result );

			if ( progress.getWindow() != null ) {
				progress.dismiss();
			}

			if ( result != null ) {
				setImageURI( uri, result );
			} else {
				Toast.makeText( AviaryActivity.this, "加载图片失败" + uri, Toast.LENGTH_SHORT ).show();
			}
		}

		@Override
		public void onCancel( DialogInterface dialog ) {
			this.cancel( true );
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

	}
}
