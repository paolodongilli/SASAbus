package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.CheckDatabaseActivity;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.SASAbus;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;

public class DownloadThread implements Runnable{

	private CheckDatabaseActivity parent = null;
	
	private String filename = null;
	
	public DownloadThread(CheckDatabaseActivity act, String filename) {
		super();
		this.parent = act;
		this.filename = filename;
	}
	
	
	
	@Override
	public void run() {
		SASAbus config = (SASAbus) parent.getApplicationContext();
		// Check if db exists
		Resources res = parent.getResources();
		String dbDirName = res.getString(R.string.db_dir);
		String dbFileName = this.filename;
		String dbZIPFileName = dbFileName + ".zip";
		String md5FileName = dbFileName + ".md5";

		//Check if the sd-card is mounted
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			parent.showDialog(CheckDatabaseActivity.NO_SD_CARD);
			throw new AndroidRuntimeException(parent.getResources().getString(R.string.sd_card_not_mounted));
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
					new FileRetriever(parent, dbZIPFile, dbFile, md5File).execute(dbZIPFileName, md5FileName);
				}
				else 
				{
					//if no db-update is available will be shown a message
					parent.showDialog(CheckDatabaseActivity.NO_DB_UPDATE_AVAILABLE);
				}
			} 
			else 
			{
				//shows dialog for no network connection
				parent.showDialog(CheckDatabaseActivity.NO_NETWORK_CONNECTION);
			}
		}
		else 
		{
			// verify files
			if (!MD5Utils.checksumOK(dbFile, md5File)) 
			{
				//shows dialog that occours a md5-error
				parent.showDialog(CheckDatabaseActivity.MD5_ERROR_DIALOG);
			}
			else 
			{
				//shows dialog that download success
				parent.showDialog(CheckDatabaseActivity.DOWNLOAD_SUCCESS_DIALOG);
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
	private boolean fileUpdateAvailable(String md5FileName, File dbDir) {
		boolean update = false;
		File md5File = new File(dbDir, md5FileName);
		long lastLocalMod = md5File.lastModified();
		Date lastLocalModDate = new Date(lastLocalMod);
		String lastRemoteMod;
		Date lastRemoteModDate;
		Resources res = parent.getResources();

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
	 * this method checks if a networkconnection is active or not
	 * @return boolean if the network is reachable or not
	 */
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (parent.getSystemService(Context.CONNECTIVITY_SERVICE));
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
