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

import it.sasabz.android.sasabus.SASAbus;

import it.sasabz.android.sasabus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.SlidingDrawer;



/**
 * FileDownloader is my own delegate class that performs the
 * actual downloading and is initialized with the source URL.
 * 
 * @author Paolo Dongilli and Markus Windegger
 *
 */
public class FileRetriever  extends AsyncTask<Void, String, Long>{
	private static final String TAG = "FileRetriever";
	
	private ProgressDialog progressDialog;

	private File dbZIPFile;
	private File md5File;
	private Resources res;

	private String filename;
	private PowerManager.WakeLock wakeLock;
	private CheckDatabaseActivity activity;
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
	public FileRetriever(CheckDatabaseActivity activity, String filename) {
		super();
		this.filename = filename;
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
	
	public FileRetriever(CheckDatabaseActivity activity, String filename, String download, String unzipping) {
		super();
		this.activity = activity;
		this.res = activity.getResources();
		this.filename = filename;
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
	protected Long doInBackground(Void...params) {	
		SASAbus config = (SASAbus) activity.getApplicationContext();
		// Check if db exists
		Resources res = activity.getResources();
		String dbDirName = res.getString(R.string.db_dir);
		String dbFileName = this.filename;
		String dbZIPFileName = dbFileName + ".zip";
		String md5FileName = dbFileName + ".md5";

		//Check if the sd-card is mounted
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return Long.valueOf(CheckDatabaseActivity.NO_SD_CARD);
		}
		File dbDir = new File(Environment.getExternalStorageDirectory(),
				dbDirName);
		// check if dbDir exists; if not create it
		if (!dbDir.exists()) {
			dbDir.mkdirs();
		}

		//creates all files (zip, md5 and db)
		File dbFile = new File(dbDir, dbFileName);
		dbZIPFile = new File(dbDir, dbZIPFileName);
		md5File = new File(dbDir, md5FileName);

		
		boolean download = false;
		if (dbFile.exists() && md5File.exists())
		{
			/*
			 * checks if the md5-sum are equal
			 * if not, directly download is true, whe have to do an update
			 * else we are checking other properties to download new database or not
			 */
			if (!MD5Utils.checksumOK(dbFile, md5File))
			{
				download = true;
			}
			else 
			{
				String end = null;
				try
				{
					end = Config.getEndDate();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				try 
				{
					Date endDate = timeFormat.parse(end);
					Date currentDate = timeFormat.parse(timeFormat.format(cal
							.getTime()));
					Log.v("CheckDatabaseActivity", "endDate: " + endDate.toString() + "; currentDate: " + currentDate.toString());
					if (currentDate.after(endDate)) 
					{
						download = true;
						config.setDbDownloadAttempts(config.getDbDownloadAttempts() + 1);
					}
				} 
				catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!download && fileUpdateAvailable(md5FileName, dbDir)) 
				{
					download = true;
				}
			}
		} 
		else 
		{
			download = true;
		}

		if (download) 
		{
			// verify we have a network connection
			if (haveNetworkConnection()) 
			{
				if(config.getDbDownloadAttempts() < 2) 
				{
					//download the new Database and the new md5-file with the FileRetriver
					int result = download(dbZIPFileName, md5FileName);
					Log.v("FR", "Nach herausschmeissen bin ich hier mit wert: " + result + "!");
					return Long.valueOf(result);
				}
				else 
				{
					//if no db-update is available will be shown a message
					return Long.valueOf(CheckDatabaseActivity.NO_DB_UPDATE_AVAILABLE);
				}
			} 
			else 
			{
				//shows dialog for no network connection
				return Long.valueOf(CheckDatabaseActivity.NO_NETWORK_CONNECTION);
			}
		}
		else 
		{
			// verify files
			if (!MD5Utils.checksumOK(dbFile, md5File)) 
			{
				//shows dialog that occours a md5-error
				return Long.valueOf(CheckDatabaseActivity.MD5_ERROR_DIALOG);
			}
			else 
			{
				//shows dialog that download success
				return Long.valueOf(CheckDatabaseActivity.DOWNLOAD_SUCCESS_DIALOG);
			}
		}
	}
		
		
		
	/**
	 * this method controlls if a db-update is available.
	 * downloads the md5-file of the server and checks if the md5 is a 
	 * new md5. when it is, then returns true, else false
	 * @param md5FileName is the filename of the md5-file on the server
	 * @param dbDir is the local dirname to put into the downloaded md5 
	 * @return a boolean to determinate if an update is necessary or not
	 */
	public boolean fileUpdateAvailable(String md5FileName, File dbDir) {
		boolean update = false;
		File md5File = new File(dbDir, md5FileName);
		long lastLocalMod = md5File.lastModified();
		Date lastLocalModDate = new Date(lastLocalMod);
		String lastRemoteMod;
		Date lastRemoteModDate;
		Resources res = activity.getResources();

		// verify we have a network connection, otherwise act as no update is available
		// and update remains false
		if (haveNetworkConnection()) 
		{
			try 
			{
				/*
				 * istanziate an object of the SasabusFTP, which provides the most
				 * important methods for connecting and getting files from an FTP 
				 * server
				 */
				//SasabusFTP ftp = new SasabusFTP();
				SasabusHTTP http = new SasabusHTTP(res.getString(R.string.http_repository_url));
				
				
				//connecting and login to the server
//				ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)));
//				ftp.login(res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));
				
				//
				//lastRemoteMod = ftp.getModificationTime(md5FileName);
				//ftp.disconnect();
				//SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddhhmmss");
				//lastRemoteModDate = simple.parse(lastRemoteMod);
				lastRemoteModDate = http.getModificationTime(md5FileName);
				// check if date of remote file is after date of local file
				update = lastRemoteModDate.after(lastLocalModDate);

				Log.v("CheckDatabaseActivity", "Date of local md5: " + lastLocalModDate.toString());
				Log.v("CheckDatabaseActivity", "Date of remote md5: " + lastRemoteModDate.toString());
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return update;
	}
	
	
	/**
	 * this method checks if a networkconnection is active or not
	 * @return boolean if the network is reachable or not
	 */
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (activity.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			//testing WIFI connection
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			//testing GPRS/EDGE/UMTS/HDSPA/HUSPA/LTE connection
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
		
		
		
		
	private int download(String dbZIPFileName, String md5FileName)
	{
		//SasabusFTP ftp = null;
		SasabusHTTP http = null;
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
			try
			{
				Thread.sleep(2000);
			}
			catch(InterruptedException exe)
			{
				
			}
			//creating a new ftp connection
			//ftp = new SasabusFTP();
			http = new SasabusHTTP(activity.getResources().getString(R.string.http_repository_url));
			
			try
			{
//				try
//				{
//					ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)));
//					ftp.login(res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//					return CheckDatabaseActivity.DOWNLOAD_RETRY;
//				}
			
				// download the file
				FileOutputStream output = new FileOutputStream(dbZIPFile);
				
				try 
				{
					//ftp.bin();
					//ftp.get(output, dbZIPFileName, this);
					http.get(output, dbZIPFileName, this);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					progressDialog.dismiss();
					try
					{
						//ftp.disconnect();
						output.flush();
						output.close();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					e.printStackTrace();
					return CheckDatabaseActivity.DOWNLOAD_RETRY;
				}
				try
				{
					output.flush();
					output.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
	
				progressDialog.dismiss();
	
				// download md5sum
	
				progressDialog = new ProgressDialog(this.activity);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setMessage(res.getString(R.string.downloading_md5));
				progressDialog.setCancelable(false);
				
				output = new FileOutputStream(this.md5File);
				
				try
				{
					Thread.sleep(2000);
				}
				catch(InterruptedException exe)
				{
					
				}
				
				try {
					//ftp.bin();
					//ftp.get(output, md5FileName, this);
					http.get(output, md5FileName, this);
				} catch (Exception e) {
					e.printStackTrace();
					progressDialog.dismiss();
					try
					{
						//ftp.disconnect();
						output.flush();
						output.close();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					return CheckDatabaseActivity.DOWNLOAD_RETRY;
				}
				try
				{
					output.flush();
					output.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
				progressDialog.dismiss();
				try
				{
					//ftp.disconnect();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					//ftp.disconnect();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				return CheckDatabaseActivity.NO_NETWORK_CONNECTION;
			}
			
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
			
			File dbFile = new File(dbZIPFile.getParentFile().getAbsolutePath(), this.filename);
			if(!MD5Utils.checksumOK(dbFile, md5File))
			{
				progressDialog.dismiss();
				return CheckDatabaseActivity.MD5_ERROR_DIALOG;
			}
			
			progressDialog.dismiss();

		} catch (Exception e) {
			e.printStackTrace();
			return CheckDatabaseActivity.DOWNLOAD_ERROR_DIALOG;
		}
		return CheckDatabaseActivity.DB_OK;
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
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);

		wakeLock.release();
		activity.setRequestedOrientation(originalRequestedOrientation);

		Log.v("FileRetriever", "this result has been arrived in the onPostExecute-method: " + result);
		
		if(this.filename.equals(activity.getResources().getString(R.string.app_name) + ".db"))
		{
			activity.showDialog(result.intValue(), CheckDatabaseActivity.FR_DB);
		}
		else
		{
			activity.showDialog(result.intValue(), CheckDatabaseActivity.FR_OSM);
		}
		
		

	}
	
	
}