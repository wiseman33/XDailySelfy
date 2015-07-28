/**
 * This file is part of XDailySelfy.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    Danil Knysh, 2015
 */
package com.wiseman33.xdailyselfy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements PreviewAddPhotoDialog.NoticeDialogListener {

	static final String TAG = "XDAILYSELFY_APP";
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final String PREVIEW_SAVE_DIALOG_TAG = "PREVIEW_SAVE_DIALOG_TAG";

	private ProgressBar mProgressBar;
	private Bitmap tmpSelfy;
	private PreviewAddPhotoDialog previewSaveDialog;
	private SelfyListAdapter mAdapter;
	
	private void showPreviewSaveDialog(Bitmap image) {
        FragmentManager fm = getFragmentManager();
        previewSaveDialog = new PreviewAddPhotoDialog(image);
        
        previewSaveDialog.show(fm, PREVIEW_SAVE_DIALOG_TAG);
    }
	
	private void closePreviewSaveDialog() {
		if (null != previewSaveDialog && null != previewSaveDialog.getDialog()) {
			previewSaveDialog.getDialog().cancel();
			previewSaveDialog = null;
		}
	}
	
	private void addNewSelfy() {
		SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		mAdapter.add(new Selfy(tmpSelfy, date_format.format(Calendar.getInstance().getTime())));
		tmpSelfy = null;
	}
	
	private void loadImages() {
		new LoadSelfiesTask().execute(0);
	}
	
	private void saveImageToFile() {
		new SaveSelfyTask().execute(tmpSelfy);
	}
	
	private String buildFileName() {
		SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy_hh_mm_sss");
		return "XDailySelfy_" + format.format(Calendar.getInstance().getTime()) + ".png";
	}

	private InputStream Bitmap2InputStream(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		saveImageToFile();
	}
	
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		closePreviewSaveDialog();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mAdapter = new SelfyListAdapter(getApplicationContext());
		
		ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(mAdapter);
		
		Intent notificationIntent = new Intent(getApplicationContext(), SelfyNotification.class);
		PendingIntent contentIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent,
	                                                           PendingIntent.FLAG_CANCEL_CURRENT);

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(System.currentTimeMillis());

        
	    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	    am.cancel(contentIntent);
	    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2*60*1000, 
	    		2*60*1000, contentIntent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter.getCount() == 0)
			loadImages();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_reload) {
			mAdapter.clear();
			loadImages();
		}
		if (id == R.id.action_take_selfy) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		    }

		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        tmpSelfy = (Bitmap) extras.get("data");
	        showPreviewSaveDialog(tmpSelfy);
	    }
	}
	
	class SaveSelfyTask extends AsyncTask<Bitmap, Integer, Boolean> {

		private boolean copy(InputStream is, OutputStream os, int inputStremSize) {
			final byte[] buf = new byte[1024];
			int numBytes;
			int readNumBytes = 0;
			try {
				while (-1 != (numBytes = is.read(buf))) {
					os.write(buf, 0, numBytes);
					readNumBytes += numBytes;
					publishProgress((int)((float)readNumBytes/inputStremSize * 100));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					is.close();
					os.close();
				} catch (IOException e) {
					Log.e(TAG, "IOException");
					return false;
				}
			}
			
			return true;
		}
		
		private boolean copyImageToMemory(File outFile, Bitmap image) {
			try {

				BufferedOutputStream os = new BufferedOutputStream(
						new FileOutputStream(outFile));

				BufferedInputStream is = new BufferedInputStream(Bitmap2InputStream(image));

				return copy(is, os, image.getByteCount());
			} catch (FileNotFoundException e) {
				Log.e(TAG, "FileNotFoundException");
				return false;
			}
		}
		
		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Bitmap... image) {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {

				File outFile = new File(
						getExternalFilesDir(Environment.DIRECTORY_PICTURES),
						buildFileName());
				
				if (!outFile.exists())
					return Boolean.valueOf(copyImageToMemory(outFile, image[0]));			
			} 
			return Boolean.valueOf(false);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);			
			closePreviewSaveDialog();
			addNewSelfy();
			if (!result) {
				Toast.makeText(getApplicationContext(), "Error handled during saving.", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	class LoadSelfiesTask extends AsyncTask<Integer, Integer, Selfy[]> {
		
		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected Selfy[] doInBackground(Integer... params) {
			Selfy[] all_selfies = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {

				File sdcard = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				File dirs = new File(sdcard.getAbsolutePath());

				if(dirs.exists()) {
				    File[] files = dirs.listFiles();		
				    if (0 != files.length) {
				    	all_selfies = new Selfy[files.length];
				    				    	
					    for (int i =0; i < files.length; ++i) {
					    	BitmapFactory.Options options = new BitmapFactory.Options();
					    	options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					    	Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath(), options);
					    	
					    	SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
					    	all_selfies[i] = new Selfy(bitmap, date_format.format(new Date(files[i].lastModified())));
					    	publishProgress((int)((float)(i+1)/files.length * 100));
					    }
				    }
				}			
			} 
			return all_selfies;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Selfy[] selfies) {
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);			
			
			if (null != selfies) {
				for(Selfy item : selfies) {
					mAdapter.add(item);
				}
			}
		}
	}
}
