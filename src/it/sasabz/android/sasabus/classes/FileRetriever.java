/**
 *
 * FileRetriever.java
 *
 * Created: Feb 3, 2011 10:59:34 PM
 *
 * Copyright (C) 2011 Paolo Dongilli and Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package it.sasabz.android.sasabus.classes;


import it.sasabz.android.sasabus.CheckDatabaseActivity;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.string;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;


import android.os.AsyncTask;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;



/**
 * FileDownloader is my own delegate class that performs the
 * actual downloading and is initialized with the source URL.
 * 
 * @author Paolo Dongilli and Markus Windegger
 *
 */
public class FileRetriever  extends AsyncTask<String, String, String>{
	private static final String TAG = "FileRetriever";
	
	private ProgressDialog progressDialog;

	private AlertDialog alertDialog;
	private File dbFile;
	private File dbZIPFile;
	private File md5File;
	private Resources res;

	private String dir;
	private PowerManager.WakeLock wakeLock;
	private Activity activity;
	private transient int originalRequestedOrientation;
	
	private String download = null;
	private String unzipping = null;

	/**
	 * This constructor takes an activity, a dbZipFile to store the 
	 * downloaded Zip file, a dbFile to store the unzipped database and at least
	 * a md5File to save the downloaded md5File
	 * @param activity is the actual activity
	 * @param dbZIPFile is the file to save the downloaded zip file
	 * @param dbFile is the file to save the unzipped db file
	 * @param md5File is the file to save the downloaded md5 file
	 */
	public FileRetriever(Activity activity, File dbZIPFile, File dbFile,
			File md5File) {
		super();
		this.dbZIPFile = dbZIPFile;
		this.dbFile = dbFile;
		this.md5File = md5File;
		this.activity = activity;
		this.res = activity.getResources();

		//getting the power manager from the activity
		PowerManager pm = (PowerManager) this.activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, TAG);
		// Obtain a wakelock for SCREEN_DIM_WAKE_LOCK
		originalRequestedOrientation = activity.getRequestedOrientation();
		activity
		.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		wakeLock.acquire();
	}
	
	public FileRetriever(Activity activity, File dbZIPFile, File dbFile,
			File md5File, String download, String unzipping) {
		super();
		this.dbZIPFile = dbZIPFile;
		this.dbFile = dbFile;
		this.md5File = md5File;
		this.activity = activity;
		this.res = activity.getResources();
		this.download = download;
		this.unzipping = unzipping;

		//getting the power manager from the activity
		PowerManager pm = (PowerManager) this.activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, TAG);
		// Obtain a wakelock for SCREEN_DIM_WAKE_LOCK
		originalRequestedOrientation = activity.getRequestedOrientation();
		activity
		.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		wakeLock.acquire();
	}

	

	
	/**
	 * @return the progressDialog
	 */
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	/**
	 * @param progressDialog the progressDialog to set
	 */
	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		originalRequestedOrientation = activity.getRequestedOrientation();
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		wakeLock.acquire();
	}

	@Override
	protected String doInBackground(String... params) {	
		try {
			// download dbZIPFile
			
			Looper.prepare();
			
			progressDialog = new ProgressDialog(this.activity);
			
			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			if(download == null)
				progressDialog.setMessage(res.getString(R.string.downloading_db));
			else
				progressDialog.setMessage(download);
			progressDialog.setCancelable(false);
			
			//creating a new ftp connection
			SasabusFTP ftp = new SasabusFTP();
			
			ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)), 
					res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));
			
			// download the file
			FileOutputStream output = new FileOutputStream(dbZIPFile);
			
			try 
			{
				ftp.bin();
				ftp.get(output, params[0], this);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return "ko";
			}
			finally
			{
				try
				{
					ftp.disconnect();
					output.flush();
					output.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return "ko";
				}
			}

			progressDialog.dismiss();

			// unzip dbZIPFile
			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			if(unzipping == null)
				progressDialog.setMessage(res.getString(R.string.unzipping_db));
			else
				progressDialog.setMessage(unzipping);
			progressDialog.setCancelable(false);
			progressDialog.setProgress(0);
			Decompress d = new Decompress(dbZIPFile.getAbsolutePath(),
					dbZIPFile.getParent());
			d.unzip();
			dbZIPFile.delete();
			progressDialog.dismiss();
			
			
			// download md5sum

			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(res.getString(R.string.downloading_md5));
			progressDialog.setCancelable(false);

			ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)));
			ftp.login(res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));

			output = new FileOutputStream(this.md5File);
			
			try {
				ftp.bin();
				ftp.get(output, params[1], this);
			} catch (IOException e) {
				e.printStackTrace();
				return "ko";
			}
			finally
			{
				try
				{
					ftp.disconnect();
					output.flush();
					output.close();

				}
				catch (Exception e)
				{
					e.printStackTrace();
					return "ko";
				}
			}
	
			progressDialog.dismiss();

		} catch (Exception e) {
			e.printStackTrace();
			return "ko";
		}
		
		
		return "ok";
	}

	/**
	 * publishes the updated progress to the progressbar
	 * @param proc is the updated progress
	 */
	public void publishProgress(String proc) {
		super.publishProgress(proc);
	}
	
	@Override
	public void onProgressUpdate(String... args) {
		this.progressDialog.setProgress(Integer.parseInt(args[0]));
		progressDialog.show();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		wakeLock.release();
		activity.setRequestedOrientation(originalRequestedOrientation);

		// Run next activity
		this.activity.finish();
		Intent checkDB = new Intent(this.activity, CheckDatabaseActivity.class);
		this.activity.startActivity(checkDB);

	}
	
	
}