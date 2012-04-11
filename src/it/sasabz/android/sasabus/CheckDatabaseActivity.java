/**
 *
 * CheckDatabaseActivity.java
 *
 * Created: Jan 16, 2011 11:41:06 AM
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

package it.sasabz.android.sasabus;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.Config;
import it.sasabz.android.sasabus.classes.FileRetriever;
import it.sasabz.android.sasabus.classes.MD5Utils;
import it.sasabz.android.sasabus.classes.SasabusFTP;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.AndroidRuntimeException;
import android.util.Log;

public class CheckDatabaseActivity extends ListActivity {

	private final static int DOWNLOAD_SUCCESS_DIALOG = 0;
	private final static int DOWNLOAD_ERROR_DIALOG = 1;
	private final static int MD5_ERROR_DIALOG = 2;
	private final static int NO_NETWORK_CONNECTION = 3;
	private final static int NO_DB_UPDATE_AVAILABLE = 4;

	public CheckDatabaseActivity() {
		
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		check();
	}

	private void check()
	{
		SASAbus config = (SASAbus) getApplicationContext();
		// Check if db exists
		Resources res = getResources();
		String appName = res.getString(R.string.app_name);
		String dbDirName = res.getString(R.string.db_dir);
		String repositoryURL = res.getString(R.string.repository_url);
		String dbFileName = appName + ".db";
		String dbZIPFileName = dbFileName + ".zip";
		String md5FileName = dbFileName + ".md5";

		//Check if the sd-card is mounted
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			throw new AndroidRuntimeException(getResources().getString(R.string.sd_card_not_mounted));
		}
		File dbDir = new File(Environment.getExternalStorageDirectory(),
				dbDirName);
		// check if dbDir exists; if not create it
		if (!dbDir.exists()) {
			dbDir.mkdirs();
		}

		//creates all files (zip, md5 and db)
		File dbFile = new File(dbDir, dbFileName);
		File dbZIPFile = new File(dbDir, dbZIPFileName);
		File md5File = new File(dbDir, md5FileName);

		
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
				if (!download && dbUpdateAvailable(md5FileName, dbDir)) 
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
					new FileRetriever(this, dbZIPFile, dbFile, md5File).execute(dbZIPFileName, md5FileName);
				}
				else 
				{
					//if no db-update is available will be shown a message
					showDialog(NO_DB_UPDATE_AVAILABLE);
				}
			} 
			else 
			{
				//shows dialog for no network connection
				showDialog(NO_NETWORK_CONNECTION);
			}
		}
		else 
		{
			// verify files
			if (!MD5Utils.checksumOK(dbFile, md5File)) 
			{
				//shows dialog that occours a md5-error
				showDialog(MD5_ERROR_DIALOG);
			}
			else 
			{
				//shows dialog that download success
				showDialog(DOWNLOAD_SUCCESS_DIALOG);
			}
		}
	}
	
	
	/**
	 * Called when the activity is about to start interacting with the user.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	private final Dialog createAlertDialog(int msg, String placeholder) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.a_given_string);
		builder.setIcon(R.drawable.icon);
		//builder.setMessage(msg);
		builder.setMessage(String.format(getString(msg),placeholder));
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				startActivity();
			}
		});
		return builder.create();
	}

	
	/**
	 * this method controlls if a db-update is available.
	 * downloads the md5-file of the server and checks if the md5 is a 
	 * new md5. when it is, then returns true, else false
	 * @param md5FileName is the filename of the md5-file on the server
	 * @param dbDir is the local dirname to put into the downloaded md5 
	 * @return a boolean to determinate if an update is necessary or not
	 */
	private boolean dbUpdateAvailable(String md5FileName, File dbDir) {
		boolean update = false;
		File md5File = new File(dbDir, md5FileName);
		long lastLocalMod = md5File.lastModified();
		Date lastLocalModDate = new Date(lastLocalMod);
		String lastRemoteMod;
		Date lastRemoteModDate;
		Resources res = this.getResources();

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
				SasabusFTP ftp = new SasabusFTP();
				
				//connecting and login to the server
				ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)));
				ftp.login(res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));
				
				//
				lastRemoteMod = ftp.getModificationTime(md5FileName);
				ftp.disconnect();
				SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddhhmmss");
				lastRemoteModDate = simple.parse(lastRemoteMod);
				// check if date of remote file is after date of local file
				update = lastRemoteModDate.after(lastLocalModDate);

				Log.v("CheckDatabaseActivity", "Date of local md5: " + lastLocalModDate.toString());
				Log.v("CheckDatabaseActivity", "Date of remote md5: " + lastRemoteModDate.toString());
			}
			catch (MalformedURLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return update;
	}

	/**
	 * this method is creating an allert message
	 * @param msg is the message to be shown in the alert dialog
	 * @return an Dialog to show
	 */
	private final Dialog createErrorAlertDialog(int msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.a_given_string);
		builder.setIcon(R.drawable.icon);
		builder.setMessage(msg);
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				System.exit(0);
			}
		});
		return builder.create();
	}

	
	/**
	 * Called when all downloads were successful and we have to start the
	 * first user activity called SelectModeActivity
	 */
	private void startActivity() {
		finish();
		Intent startact = null;
		 try
	        {
			 	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
	        	int mode = Integer.parseInt(shared.getString("mode", "0"));
	        	Log.v("preferences", "mode: " + mode);
	        	if(mode == 0)
	            {
	            	startact = new Intent(this, SelectModeActivity.class);
	            }
	        	if(mode == 1)
	            {
	            	startact = new Intent(this, SelectPalinaLocationActivity.class);
	            }
	            if(mode == 2)
	            {
	            	startact = new Intent(this, SelectBacinoActivity.class);
	            }
	        	
	        }
		 catch (Exception e)
		 {
			 startact = new Intent(this, SelectModeActivity.class);
			 
		 }
		 if(startact == null)
		 {
			 startact = new Intent(this, SelectModeActivity.class);
		 }
		 startActivity(startact);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case NO_NETWORK_CONNECTION:
			return createErrorAlertDialog(R.string.no_network_connection);
		case NO_DB_UPDATE_AVAILABLE:
			return createErrorAlertDialog(R.string.no_db_update_available);
		case DOWNLOAD_SUCCESS_DIALOG:
			return createAlertDialog(R.string.db_ok, getString(R.string.app_name) + ".db");
		case DOWNLOAD_ERROR_DIALOG:
			return createErrorAlertDialog(R.string.db_download_error);
		case MD5_ERROR_DIALOG:
			return createErrorAlertDialog(R.string.md5_error);
		default:
			return null;
		}
	}

	/**
	 * this method checks if a networkconnection is active or not
	 * @return boolean if the network is reachable or not
	 */
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

	
}