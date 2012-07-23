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




import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.FileRetriever;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class CheckDatabaseActivity extends Activity {

	public final static int DOWNLOAD_SUCCESS_DIALOG = 0;
	public final static int DOWNLOAD_ERROR_DIALOG = 1;
	public final static int MD5_ERROR_DIALOG = 2;
	public final static int NO_NETWORK_CONNECTION = 3;
	public final static int NO_DB_UPDATE_AVAILABLE = 4;
	public final static int NO_SD_CARD = 5;
	public final static int DB_OK = 6;
	
	public final static int FR_OSM = 0;
	public final static int FR_DB = 1;
	

	
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);
		check_db();
	}
	
	public void check_db()
	{
		Resources res = this.getResources();
		String db_filename = res.getString(R.string.app_name) + ".db";
		try {
			new FileRetriever(this, db_filename, res.getString(R.string.downloading_db), res.getString(R.string.unzipping_db)).execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void check_osm()
	{
		Resources res = this.getResources();
		String osm_filename = res.getString(R.string.app_name_osm) + ".map";
		try {
			new FileRetriever(this, osm_filename, res.getString(R.string.downloading_map), res.getString(R.string.unzipping_map)).execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void showDialog(int id, int res)
	{
		onCreateDialog(id, res).show();
	}

	public final Dialog createAlertDialog(int msg, String placeholder, final int res) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.a_given_string);
		builder.setIcon(R.drawable.icon);
		//builder.setMessage(msg);
		builder.setMessage(String.format(getString(msg),placeholder));
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				if(res == FR_DB)
				{
					check_osm();
				}
				else
				{
					startActivity();
				}
				
			}
		});
		return builder.create();
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
	public void startActivity() {
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

	protected Dialog onCreateDialog(int id, int res) {
		switch (id) {
		case NO_NETWORK_CONNECTION:
			return createErrorAlertDialog(R.string.no_network_connection);
		case NO_DB_UPDATE_AVAILABLE:
			return createErrorAlertDialog(R.string.no_db_update_available);
		case DOWNLOAD_SUCCESS_DIALOG:
			String filename = getString(R.string.app_name)+ ".db";
			if(res == FR_OSM)
				filename = getString(R.string.app_name_osm)+ ".map";
			return createAlertDialog(R.string.db_ok,  filename, res);
		case DOWNLOAD_ERROR_DIALOG:
			return createErrorAlertDialog(R.string.db_download_error);
		case MD5_ERROR_DIALOG:
			return createErrorAlertDialog(R.string.md5_error);
		case NO_SD_CARD:
			return createErrorAlertDialog(R.string.sd_card_not_mounted);
		default:
			return null;
		}
	}


	
}