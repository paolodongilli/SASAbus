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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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

// FileDownloader is my own delegate class that performs the
// actual downloading and is initialized with the source URL.
public class FileRetriever extends AsyncTask<String, String, String> {
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

		PowerManager pm = (PowerManager) this.activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, TAG);
		// Obtain a wakelock for SCREEN_DIM_WAKE_LOCK
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		originalRequestedOrientation = activity.getRequestedOrientation();
		activity
		.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		wakeLock.acquire();
	}

	@Override
	protected String doInBackground(String... params) {
		int count;

		try {
			// download dbZIPFile

			Looper.prepare();

			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(res.getString(R.string.downloading_db));
			progressDialog.setCancelable(false);

			URL url = new URL(params[0]);
			URLConnection conn = url.openConnection();
			conn.connect();

			// this will be useful so that you can show a typical 0-100%
			// progress bar
			

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(dbZIPFile);

			int lenghtOfFile = conn.getContentLength();
			byte data[] = new byte[1024];
			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				publishProgress("" + (int) total * 100 / lenghtOfFile);
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();

			progressDialog.dismiss();

			// unzip dbZIPFile
			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(res.getString(R.string.unzipping_db));
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

			url = new URL(params[1]);
			conn = url.openConnection();
			conn.connect();

			// this will be useful so that you can show a typical 0-100%
			// progress bar
			lenghtOfFile = conn.getContentLength();

			// download the file
			input = new BufferedInputStream(url.openStream());
			output = new FileOutputStream(this.md5File);

			data = new byte[1024];
			total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				publishProgress("" + (int) total * 100 / lenghtOfFile);
				output.write(data, 0, count);
			}
			publishProgress("" + (int) total * 100 / lenghtOfFile);

			output.flush();
			output.close();
			input.close();

			progressDialog.dismiss();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		return null;
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