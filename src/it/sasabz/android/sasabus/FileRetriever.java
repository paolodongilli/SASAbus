/**
 *
 * FileRetriever.java
 *
 * Created: Feb 3, 2011 10:59:34 PM
 *
 * Copyright (C) 2011 Paolo Dongilli
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


import it.sasabz.android.sasabus.classes.SasabusFTP;

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


import android.os.PowerManager;
import android.util.Log;


// FileDownloader is my own delegate class that performs the
// actual downloading and is initialized with the source URL.
public class FileRetriever  {
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

	public FileRetriever(Activity activity, File dbZIPFile, File dbFile,
			File md5File) {
		super();
		this.dbZIPFile = dbZIPFile;
		this.dbFile = dbFile;
		this.md5File = md5File;
		this.activity = activity;
		this.res = activity.getResources();

		
		
	}

	
	public boolean download(String dbZipFileName, String md5FileName) {
		
		PowerManager pm = (PowerManager) this.activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, TAG);
		// Obtain a wakelock for SCREEN_DIM_WAKE_LOCK
		originalRequestedOrientation = activity.getRequestedOrientation();
		activity
		.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		wakeLock.acquire();
		
		try {
			// download dbZIPFile
			
			
			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(res.getString(R.string.downloading_db));
			progressDialog.setCancelable(false);
			progressDialog.setProgress(0);
			progressDialog.show();
			
			//URL url = new URL(params[0]);
			//URLConnection conn = url.openConnection();
			//conn.connect();

			// this will be useful so that you can show a typical 0-100%
			// progress bar
			
			//FTPClient ftp = new FTPClient();
			//ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)));
			//ftp.login(res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));
			SasabusFTP ftp = new SasabusFTP();
			
			ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)), 
					res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));
			
			// download the file
			FileOutputStream output = new FileOutputStream(dbZIPFile);
			
			try 
			{
				ftp.get(output, dbZIPFile.getName());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return false;
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
					return false;
				}
			}
			

			progressDialog.dismiss();

			// unzip dbZIPFile
			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(res.getString(R.string.unzipping_db));
			progressDialog.setCancelable(false);
			progressDialog.setProgress(0);
			progressDialog.show();
			Decompress d = new Decompress(dbZIPFile.getAbsolutePath(),
					dbZIPFile.getParent());
			d.unzip();
			dbZIPFile.delete();
			progressDialog.dismiss();
			
			
			// download md5sum

			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(res.getString(R.string.downloading_md5));
			progressDialog.setCancelable(false);

			ftp.connect(res.getString(R.string.repository_url), Integer.parseInt(res.getString(R.string.repository_port)));
			ftp.login(res.getString(R.string.ftp_user), res.getString(R.string.ftp_passwd));

			
			
			// this will be useful so that you can show a typical 0-100%
			// progress bar

			output = new FileOutputStream(this.md5File);
			
			try {
				ftp.get(output, md5FileName);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
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
					return false;
				}
			}

			

			
			progressDialog.dismiss();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		wakeLock.release();
		activity.setRequestedOrientation(originalRequestedOrientation);
		
		
		return true;
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
}