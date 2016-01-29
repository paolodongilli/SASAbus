/*
 * SASAbus - Android app for SASA bus open data
 *
 * AndroidOpenDataLocalStorage.java
 *
 * Created: Jan 3, 2014 11:29:26 AM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.sasabz.sasabus.data;

import it.sasabz.sasabus.opendata.client.SASAbusOpenDataLocalStorage;
import it.sasabz.sasabus.opendata.client.model.BusDayTypeList;
import it.sasabz.sasabus.opendata.client.model.BusExceptionTimeBetweenStopsList;
import it.sasabz.sasabus.opendata.client.model.BusLineList;
import it.sasabz.sasabus.opendata.client.model.BusPathList;
import it.sasabz.sasabus.opendata.client.model.BusStandardTimeBetweenStopsList;
import it.sasabz.sasabus.opendata.client.model.BusWaitTimeAtStopList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import bz.davide.dmxmljson.json.HTTPAsyncJSONDownloader;
import bz.davide.dmxmljson.json.OrgJSONParser;
import bz.davide.dmxmljson.unmarshalling.IOUtil;

public class AndroidOpenDataLocalStorage extends SASAbusOpenDataLocalStorage
{
	File rootFolder;
	File mapTilesRootFolder;

	Thread backgroundThread;

	@SuppressLint("NewApi")
	// Required for context.getExternalFilesDir for sdk_int < 8
	public AndroidOpenDataLocalStorage(Context context) throws Exception
	{
		super(new OrgJSONParser());

		/*
		 * Creation of the directory-path where the data of SASA (Json format)
		 * gets stored
		 */
		File sdcardFilesDir;
		if (android.os.Build.VERSION.SDK_INT < 8)
		{
			sdcardFilesDir = Environment.getExternalStorageDirectory();
			sdcardFilesDir = new File(sdcardFilesDir, "Android");
			sdcardFilesDir = new File(sdcardFilesDir, "data");
			sdcardFilesDir = new File(sdcardFilesDir, context.getPackageName());
			sdcardFilesDir = new File(sdcardFilesDir, "files");
		}
		else
		{
			sdcardFilesDir = context.getExternalFilesDir(null);
		}

		/*
		 * Creating root folder for the data
		 */
		this.rootFolder = new File(sdcardFilesDir, "sasabus-opendata");
		/*
		 * Creating folder for openstreetmap folder
		 */
		this.mapTilesRootFolder = new File(sdcardFilesDir, "osm-tiles");

		/*
		 * Checking if the folders exists, if not, so creation of the folders
		 */
		if (!this.rootFolder.exists())
		{
			this.rootFolder.mkdirs();
		}
		if (!this.mapTilesRootFolder.exists())
		{
			this.mapTilesRootFolder.mkdirs();
		}

		this.backgroundThread = null;

	}

	/**
	 * This function
	 * 
	 * @throws IOException
	 */
	public void preloadData() throws IOException
	{

		this.getBusStations();

		final int backgroundThreadPriority = android.os.Process.THREAD_PRIORITY_LOWEST;// android.os.Process.THREAD_PRIORITY_BACKGROUND;

		this.backgroundThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				android.os.Process.setThreadPriority(backgroundThreadPriority);

				try
				{
					Thread.sleep(600); // Let UI to display!

					AndroidOpenDataLocalStorage.super.getBusDayTypeList();
					AndroidOpenDataLocalStorage.super.getBusLines();
					AndroidOpenDataLocalStorage.super.getBusPathList();

					AndroidOpenDataLocalStorage.super
							.getBusStandardTimeBetweenStopsList();
					AndroidOpenDataLocalStorage.super
							.getBusExceptionTimeBetweenStopsList();
					AndroidOpenDataLocalStorage.super
							.getBusWaitTimeAtStopList();
					AndroidOpenDataLocalStorage.super
							.getBusDefaultWaitTimeAtStopList();
					AndroidOpenDataLocalStorage.super
							.getBusLineWaitTimeAtStopList();

				}
				catch (Exception ioxxx)
				{
					ioxxx.printStackTrace();
				}
			}
		});
		this.backgroundThread.start();
	}

	@Override
	public BusDayTypeList getBusDayTypeList() throws IOException
	{
		try
		{
			if (this.backgroundThread != null)
			{
				this.backgroundThread.join();
			}
			return super.getBusDayTypeList();
		}
		catch (InterruptedException e)
		{
			throw IOUtil.wrapIntoIOException(e);
		}
	}

	@Override
	public BusLineList getBusLines() throws IOException
	{
		try
		{
			if (this.backgroundThread != null)
			{
				this.backgroundThread.join();
			}
			return super.getBusLines();
		}
		catch (InterruptedException e)
		{
			throw IOUtil.wrapIntoIOException(e);
		}
	}

	@Override
	public BusPathList getBusPathList() throws IOException
	{
		try
		{
			if (this.backgroundThread != null)
			{
				this.backgroundThread.join();
			}
			return super.getBusPathList();
		}
		catch (InterruptedException e)
		{
			throw IOUtil.wrapIntoIOException(e);
		}
	}

	@Override
	public BusStandardTimeBetweenStopsList getBusStandardTimeBetweenStopsList()
			throws IOException
	{
		try
		{
			if (this.backgroundThread != null)
			{
				this.backgroundThread.join();
			}
			return super.getBusStandardTimeBetweenStopsList();
		}
		catch (InterruptedException e)
		{
			throw IOUtil.wrapIntoIOException(e);
		}
	}

	@Override
	public BusExceptionTimeBetweenStopsList getBusExceptionTimeBetweenStopsList()
			throws IOException
	{
		try
		{
			if (this.backgroundThread != null)
			{
				this.backgroundThread.join();
			}
			return super.getBusExceptionTimeBetweenStopsList();
		}
		catch (InterruptedException e)
		{
			throw IOUtil.wrapIntoIOException(e);
		}
	}

	@Override
	public BusWaitTimeAtStopList getBusWaitTimeAtStopList() throws IOException
	{
		try
		{
			if (this.backgroundThread != null)
			{
				this.backgroundThread.join();
			}
			return super.getBusWaitTimeAtStopList();
		}
		catch (InterruptedException e)
		{
			throw IOUtil.wrapIntoIOException(e);
		}
	}

	@Override
	public void setData(String key, String data) throws IOException
	{
		this.writeFile(new File(this.rootFolder, key), data);
	}

	@Override
	@JavascriptInterface
	public String getData(String key) throws IOException
	{
		File file = new File(this.rootFolder, key);
		if (!file.exists())
		{
			return null;
		}
		String data = this.readFile(file);
		return data;
	}

	public String readFile(File f) throws IOException
	{
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		HTTPAsyncJSONDownloader.copyAllBytesAndCloseStreams(fis, outputStream);
		String ret = outputStream.toString("UTF-8");
		return ret;
	}

	public void writeFile(File f, String content) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(content.getBytes("UTF-8"));
		fos.close();
	}

	public File getMapTilesRootFolder()
	{
		return this.mapTilesRootFolder;
	}

}
