package org.thewheatfield.smarter.evcs;


import java.io.File;
import java.io.FilenameFilter;
import java.security.GuardedObject;
import java.util.ArrayList;
import java.util.Collections;


import org.thewheatfield.smarter.evcs.R;
import org.thewheatfield.smarter.evcs.R.id;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

// step 1 - 1
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener; // step 1 - 0

// gallery - 1
import android.widget.Gallery;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

// gallery - 0
public class EVCS extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testFunction(savedInstanceState);
	}

	

	// ////////////////////////////////////////////////////////////////////////////////////////
	// http://mihaifonoage.blogspot.com/2009/09/displaying-images-from-sd-card-in.html


	private void launchNewIntent(File path) {
		
		Intent i = new Intent(this, EVCS.class);
		if(path != null) i.putExtra("path", mCurrPath);
		startActivityForResult(i, 1);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


		mCurrPath = mCurrPath.getParentFile();        
		File FOLDER_EXTERNAL = Environment.getExternalStorageDirectory();
		File FOLDER_CUSTOM = new File(FOLDER_EXTERNAL, "SMARTER/custom");
        if (!mCurrPath.equals(FOLDER_CUSTOM))
        {
        	try
        	{
    	        Bundle extras = intent.getExtras();
    	        if(extras.getString("MODE") == "HOME")
    	        {
    				Bundle bundle = new Bundle();
    				bundle.putString("MODE", "HOME");
    				Intent mIntent = new Intent();
    				mIntent.putExtras(bundle);
    				setResult(RESULT_OK, mIntent);
    				finish();
    	        }
        	}
        	catch(Exception e)
        	{
        	
        	}
        	
        }
    }
	
	
	Uri[] mUrls = null;
	File[] mFiles = null;
	private File mCurrPath = null;
	private File mBasePath = null;
	private File[] currFiles;
	private File mSelectedImage = null;
	private MediaPlayer mp = new MediaPlayer();			
	private Integer myPosition = -1;
	private void testFunction(Bundle savedInstanceState) {
		// set layout file
		setContentView(R.layout.main);
		ImageButton imgBtn;
		
		//*
		imgBtn = (ImageButton) findViewById(R.id.btnSelectionYes);
		imgBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showSelectionMode(false);
				playAudio(mBasePath + "/sounds/audio_yes.mp3");

				

//*				
				// show selection
				
				// find folder corresponding to this image
				// folder name is just remove .extension for image file name
				// "theImage.jpg" > "theImage" folder

				String imgFilename = mFiles[myPosition].getName();
				int posDot = imgFilename.lastIndexOf(".");
				if (posDot != -1) {
					String baseFilename = imgFilename.substring(0, posDot);

					//Toast.makeText(test.this, "filename:  " + baseFilename,Toast.LENGTH_SHORT).show();
					// see if there is any folder for this image
					// if so get file list
					// else go to next item in sequence
					File dir = new File(mCurrPath, baseFilename);
					if (dir.exists() && dir.isDirectory()) {
						//Toast.makeText(test.this, "found folder", Toast.LENGTH_SHORT).show();
						mCurrPath = dir;

//						loadImages();
						launchNewIntent(mCurrPath);

					} else {

					}
				}				
				//*/
			}
		});
		imgBtn = (ImageButton) findViewById(R.id.btnSelectionBack);
		imgBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		imgBtn = (ImageButton) findViewById(R.id.btnSelectionHome);
		imgBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("MODE", "HOME");
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
		imgBtn = (ImageButton) findViewById(R.id.btnSelectionNo);
		imgBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				playAudio(mBasePath + "/sounds/audio_no.mp3");
				showSelectionMode(false);
				//loadImages();
			}
		});
		//*/
		ImageView picturesView = (ImageView) findViewById(R.id.imageMain);
		picturesView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//loadImages();
			}
		});

		TextView tv = new TextView(this);
		TextView txtView = (TextView) findViewById(R.id.theTextView);

		File FOLDER_EXTERNAL = Environment.getExternalStorageDirectory();
		File FOLDER_SMARTER = new File(FOLDER_EXTERNAL, "SMARTER");
		File FOLDER_CUSTOM = new File(FOLDER_EXTERNAL, "SMARTER/custom");

		mBasePath = FOLDER_SMARTER;
		mCurrPath = FOLDER_CUSTOM;

		File tmpPath = null;
		if (tmpPath == null) {
			Bundle extras = getIntent().getExtras();
			tmpPath = extras != null ? (File) extras.getSerializable("path") : null;
		}
		if (tmpPath != null) mCurrPath = tmpPath;
//*/
		loadImages();
		
		// load profile picture
		if (mCurrPath.equals(FOLDER_CUSTOM))
		{
			File profilePic = new File(FOLDER_SMARTER, "images/profile.jpg");
			if (profilePic.isFile())
			{
				picturesView.setImageURI(Uri.parse(profilePic.getAbsolutePath()));
			}
			else
			{
				picturesView.setImageDrawable(getResources().getDrawable(R.drawable.profile));
			}
		}
		else // load main image in the parent directory .jpg
		{
			showSelectionMode(false);
			File profilePic = new File(mCurrPath.getParent() + "/" + mCurrPath.getName() + ".jpg");
			//playAudio(mCurrPath.getParent() + "/" + mCurrPath.getName() + "vq.mp3");

			if (profilePic.isFile())
			{
				picturesView.setImageURI(Uri.parse(profilePic.getAbsolutePath()));
			}
			else
			{
			}

		}
		
		
		//Toast.makeText(test.this,"loading: " + mCurrPath.getAbsolutePath(),Toast.LENGTH_SHORT).show();
		
	}
	private boolean playAudio(String path)
	{
		File audioFile = new File(path);
		if (audioFile.isFile())
		{
			try
			{
				mp.reset();
			    mp.setDataSource(audioFile.getPath());
			    mp.prepare();
			    mp.start();								
			}
			catch(Exception e)
			{
				Toast.makeText(EVCS.this,"ERROR cannot play file",Toast.LENGTH_SHORT).show();
				return false;
			}
		}
	
		return true;
	}
	private void showSelectionMode(boolean show)
	{
		View theView = (View) findViewById(R.id.containerSelection);
		if (show)
			theView.setVisibility(View.VISIBLE);
		else
			theView.setVisibility(View.GONE);
		
		theView = (View) findViewById(R.id.containerGallery);
		if (show)
			theView.setVisibility(View.GONE);
		else
			theView.setVisibility(View.VISIBLE);

		theView = (View) findViewById(R.id.imageMain);			
		if (show)
			theView.setVisibility(View.VISIBLE);
		else
			theView.setVisibility(View.GONE);
		
		theView = (View) findViewById(R.id.containerNav);			
		if (show)
			theView.setVisibility(View.GONE);
		else
			theView.setVisibility(View.VISIBLE);
		
	}
	private void loadImages() {
		TextView txtView = (TextView) findViewById(R.id.theTextView);
		txtView.setText(mCurrPath.getAbsolutePath());

		class ImageFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".jpg") || name.endsWith(".png"));
			}
		}

		if (mCurrPath.isDirectory()) {
			currFiles = mCurrPath.listFiles();
			mFiles = mCurrPath.listFiles(new ImageFilter());

			/*
			 * mFiles = new String[imagelist.length]; for(int i= 0 ; i<
			 * imagelist.length; i++) { mFiles[i] =
			 * imagelist[i].getAbsolutePath(); } mUrls = new Uri[mFiles.length];
			 * for(int i=0; i < mFiles.length; i++) { mUrls[i] =
			 * Uri.parse(mFiles[i]); }
			 */

			/*
			 * for(int i = 0; currFiles != null && i < currFiles.length; i++) {
			 * tv = new TextView(lv.getContext());
			 * tv.setText(currFiles[i].getName()); } //
			 */
			// lv.setAdapter(new FileListAdapter(this));
		} else {
			class FileFilterEmpty implements FilenameFilter {
				public boolean accept(File dir, String name) {
					return false;
				}
			}
			mFiles = mCurrPath.getParentFile().listFiles(new FileFilterEmpty());
			Toast.makeText(EVCS.this,
					"this shouldn't happen " + mCurrPath.getAbsolutePath(),
					Toast.LENGTH_SHORT).show();
		}

		Gallery g = (Gallery) findViewById(R.id.containerGallery);
		g.setAdapter(new ImageAdapterSMARTER(this));
		g.setOnItemClickListener(new OnItemClickListener() {
			// image selected.
			//    	show image in main view
			//		hide selection carousel
			//		show selection 
			
			public void onItemClick(AdapterView parent, View v, int position,long id) {
				
				// show image on screen
				ImageView i = (ImageView) findViewById(R.id.imageMain);
				i.setImageURI(Uri.parse(mFiles[position].getAbsolutePath()));
				mSelectedImage= mFiles[position];
				
				showSelectionMode(true);
				myPosition = position;
				// play audio				
				if(mFiles[position].getAbsolutePath().toLowerCase().trim().lastIndexOf(".jpg") != -1)
				{
					playAudio(mFiles[position].getAbsolutePath().trim().substring(0, mFiles[position].getAbsolutePath().toLowerCase().trim().lastIndexOf(".jpg")) + "vq.mp3");
				}
				
				/*
				// show selection
				
				// find folder corresponding to this image
				// folder name is just remove .extension for image file name
				// "theImage.jpg" > "theImage" folder

				String imgFilename = mFiles[position].getName();
				int posDot = imgFilename.lastIndexOf(".");
				if (posDot != -1) {
					String baseFilename = imgFilename.substring(0, posDot);

					//Toast.makeText(test.this, "filename:  " + baseFilename,Toast.LENGTH_SHORT).show();
					// see if there is any folder for this image
					// if so get file list
					// else go to next item in sequence
					File dir = new File(mCurrPath, baseFilename);
					if (dir.exists() && dir.isDirectory()) {
						//Toast.makeText(test.this, "found folder", Toast.LENGTH_SHORT).show();
						mCurrPath = dir;

//						loadImages();
						launchNewIntent(mCurrPath);

					} else {

					}
				}
				//*/

			}
		});
	}

	public class ImageAdapterSMARTER extends BaseAdapter {
		Context mContext;

		public ImageAdapterSMARTER(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mFiles.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setImageURI(Uri.parse(mFiles[position].getAbsolutePath()));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(260, 250));
			return i;
		}
	}

}
