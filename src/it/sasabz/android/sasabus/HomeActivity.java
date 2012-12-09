/**
 *
 * OnlineModeActivity.java
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package it.sasabz.android.sasabus;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.DateTimePicker;
import it.sasabz.android.sasabus.classes.MyAutocompleteAdapter;
import it.sasabz.android.sasabus.classes.MySQLiteDBAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.classes.services.CheckUpdate;
import it.sasabz.android.sasabus.classes.services.FileRetriever;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	
	public final static int DOWNLOAD_AVAILABLE = 0;
	public final static int DOWNLOAD_FILES = 1;
	public final static int DB_OK = 2;
	public final static int NO_SD_CARD = 3;
	
	
	public final static int FR_OSM = 0;
	public final static int FR_DB = 1;
	public final static int DB_UP = 2;
	
	public final static int OFFLINE = 34;
	
	private CheckUpdate updatecheck = null;
    
    public HomeActivity() {
    }

    private HomeActivity getThis()
    {
    	return this;
    }
    
    
    
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.online_search_layout);
        
        Date datum = new Date();
        SimpleDateFormat simple = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        
        
        TextView datetime = (TextView)findViewById(R.id.time);
        String datetimestring = "";
        
        datetimestring = simple.format(datum);
        
        datetime.setText(datetimestring);
        
        Button search = (Button)findViewById(R.id.search);
        
        search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AutoCompleteTextView from = (AutoCompleteTextView)findViewById(R.id.from_text);
				AutoCompleteTextView to = (AutoCompleteTextView)findViewById(R.id.to_text);
				TextView datetime = (TextView)findViewById(R.id.time);
				
				String from_txt = getThis().getResources().getString(R.string.from_txt);
				
				if((!from.getText().toString().trim().equals("") || !from.getHint().toString().trim().equals(from_txt)) && !to.getText().toString().trim().equals(""))
				{
					Intent getSelect = new Intent(getThis(), OnlineSelectStopActivity.class);
					String fromtext = "";
					if(from.getText().toString().trim().equals(""))
						fromtext = from.getHint().toString();
					else
						fromtext = from.getText().toString();
					String totext = to.getText().toString();
					fromtext = "(" + fromtext.replace(" -", ")");
					totext = "(" + totext.replace(" -", ")");
					
					getSelect.putExtra("from", fromtext);
					getSelect.putExtra("to", totext);
					getSelect.putExtra("datetime", datetime.getText().toString());
					myStartActivity(getSelect);
				}
			}
		});
        
        datetime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Create the dialog
				final Dialog mDateTimeDialog = new Dialog(getThis());
				// Inflate the root layout
				final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
						.inflate(R.layout.date_time_dialog, null);
				// Grab widget instance
				final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
						.findViewById(R.id.DateTimePicker);
				TextView dt = (TextView)findViewById(R.id.time);
				String datetimestring = dt.getText().toString();
				SimpleDateFormat datetimeformat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				Date datetime = null;
				try
				{
					datetime = datetimeformat.parse(datetimestring);
				}
				catch(Exception e)
				{
					;
				}
				mDateTimePicker.updateTime(datetime.getHours(), datetime.getMinutes());
				mDateTimePicker.updateDate(datetime.getYear() + 1900, datetime.getMonth(), datetime.getDate());
				// Check is system is set to use 24h time (this doesn't seem to
				// work as expected though)
				final String timeS = android.provider.Settings.System
						.getString(getContentResolver(),
								android.provider.Settings.System.TIME_12_24);
				final boolean is24h = !(timeS == null || timeS.equals("12"));

				// Update demo TextViews when the "OK" button is clicked
				((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
							mDateTimePicker.clearFocus();
							String datetimestring = "";
							int day = mDateTimePicker.get(Calendar.DAY_OF_MONTH);
							int month = mDateTimePicker.get(Calendar.MONTH) + 1;
							int year = mDateTimePicker.get(Calendar.YEAR);
							int hour = 0;
							int min = 0;
							int append = 0;
							if (mDateTimePicker.is24HourView()) {
								hour = mDateTimePicker.get(Calendar.HOUR_OF_DAY);
								min = mDateTimePicker.get(Calendar.MINUTE);
							} else {
								hour = mDateTimePicker.get(Calendar.HOUR);
								min = mDateTimePicker.get(Calendar.MINUTE);
								if(mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM)
								{
									append = 1;
								}
								else
								{
									append = 2;
								}
							}
							if (day < 10)
							{
								datetimestring += "0";
							}
							datetimestring += (day + ".");
							if(month < 10)
							{
								datetimestring += "0";
							}
							datetimestring += (month + "." + year + " ");
							if(hour < 10)
							{
								datetimestring += "0";
							}
							datetimestring += (hour + ":");
							if(min < 10)
							{
								datetimestring += "0";
							}
							datetimestring += min;
							
							switch(append)
							{
							case 1:
								datetimestring += " AM";
								break;
							case 2:
								datetimestring += " AM";
								break;
							}
							
							TextView time = (TextView)findViewById(R.id.time);
							time.setText(datetimestring);
							mDateTimeDialog.dismiss();
						}
					});
				// Cancel the dialog when the "Cancel" button is clicked
				((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDateTimeDialog.cancel();
							}
						});

				// Reset Date and Time pickers when the "Reset" button is
				// clicked
				((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDateTimePicker.reset();
							}
						});

				// Setup TimePicker
				mDateTimePicker.setIs24HourView(is24h);
				// No title on the dialog window
				mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				// Set the dialog content view
				mDateTimeDialog.setContentView(mDateTimeDialogView);
				// Display the dialog
				mDateTimeDialog.show();
			}

		});
        
        ImageButton datepicker = (ImageButton)findViewById(R.id.datepicker);
        
        datepicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Create the dialog
				final Dialog mDateTimeDialog = new Dialog(getThis());
				// Inflate the root layout
				final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
						.inflate(R.layout.date_time_dialog, null);
				// Grab widget instance
				final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
						.findViewById(R.id.DateTimePicker);
				TextView dt = (TextView)findViewById(R.id.time);
				String datetimestring = dt.getText().toString();
				SimpleDateFormat datetimeformat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				Date datetime = null;
				try
				{
					datetime = datetimeformat.parse(datetimestring);
				}
				catch(Exception e)
				{
					;
				}
				mDateTimePicker.updateTime(datetime.getHours(), datetime.getMinutes());
				mDateTimePicker.updateDate(datetime.getYear() + 1900, datetime.getMonth(), datetime.getDate());
				// Check is system is set to use 24h time (this doesn't seem to
				// work as expected though)
				final String timeS = android.provider.Settings.System
						.getString(getContentResolver(),
								android.provider.Settings.System.TIME_12_24);
				final boolean is24h = !(timeS == null || timeS.equals("12"));

				// Update demo TextViews when the "OK" button is clicked
				((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
							mDateTimePicker.clearFocus();
							String datetimestring = "";
							int day = mDateTimePicker.get(Calendar.DAY_OF_MONTH);
							int month = mDateTimePicker.get(Calendar.MONTH) + 1;
							int year = mDateTimePicker.get(Calendar.YEAR);
							int hour = 0;
							int min = 0;
							int append = 0;
							if (mDateTimePicker.is24HourView()) {
								hour = mDateTimePicker.get(Calendar.HOUR_OF_DAY);
								min = mDateTimePicker.get(Calendar.MINUTE);
							} else {
								hour = mDateTimePicker.get(Calendar.HOUR);
								min = mDateTimePicker.get(Calendar.MINUTE);
								if(mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM)
								{
									append = 1;
								}
								else
								{
									append = 2;
								}
							}
							if (day < 10)
							{
								datetimestring += "0";
							}
							datetimestring += (day + ".");
							if(month < 10)
							{
								datetimestring += "0";
							}
							datetimestring += (month + "." + year + " ");
							if(hour < 10)
							{
								datetimestring += "0";
							}
							datetimestring += (hour + ":");
							if(min < 10)
							{
								datetimestring += "0";
							}
							datetimestring += min;
							
							switch(append)
							{
							case 1:
								datetimestring += " AM";
								break;
							case 2:
								datetimestring += " AM";
								break;
							}
							
							TextView time = (TextView)findViewById(R.id.time);
							time.setText(datetimestring);
							mDateTimeDialog.dismiss();
						}
					});
				// Cancel the dialog when the "Cancel" button is clicked
				((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDateTimeDialog.cancel();
							}
						});

				// Reset Date and Time pickers when the "Reset" button is
				// clicked
				((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDateTimePicker.reset();
							}
						});

				// Setup TimePicker
				mDateTimePicker.setIs24HourView(is24h);
				// No title on the dialog window
				mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				// Set the dialog content view
				mDateTimeDialog.setContentView(mDateTimeDialogView);
				// Display the dialog
				mDateTimeDialog.show();
			}

		});
        
        ImageView img = (ImageView)findViewById(R.id.cippy);
        img.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.firstavenue.client"));
                startActivity(intent);
            }
        });
        
        if(MySQLiteDBAdapter.exists(this))
        {
	        updatecheck = new CheckUpdate(this);
	        updatecheck.execute();
        }
        else
        {
        	Intent download = new Intent(this, CheckDatabaseActivity.class);
			startActivity(download);
        }
        
	}


    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
        AutoCompleteTextView from = (AutoCompleteTextView)findViewById(R.id.from_text);
        AutoCompleteTextView to = (AutoCompleteTextView)findViewById(R.id.to_text);
        LocationManager locman = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location lastloc = locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(MySQLiteDBAdapter.exists(this))
        {
	        if(lastloc == null)
	        {
	        	lastloc = locman.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	        }
	        if(lastloc != null)
	        {
	        	try
	        	{
	        		Palina palina = PalinaList.getPalinaGPS(lastloc);
	        		if(palina != null)
	        		{
	        			from.setHint(palina.toString());
	        		}
	        	}
	        	catch(Exception e)
	        	{
	        		Log.e("HomeActivity", "Fehler bei der Location", e);
	        	}
	        }
	        else
	        {
	        	Log.v("HomeActivity", "No location found!!");
	        }
	        Vector<DBObject> palinalist = PalinaList.getNameList(); 
	        MyAutocompleteAdapter adapterfrom = new MyAutocompleteAdapter(this, android.R.layout.simple_list_item_1, palinalist);
	        MyAutocompleteAdapter adapterto = new MyAutocompleteAdapter(this, android.R.layout.simple_list_item_1, palinalist);
	        from.setAdapter(adapterfrom);
	        to.setAdapter(adapterto);
        }
    }

    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 super.onCreateOptionsMenu(menu);
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.optionmenu, menu);
    	 menu.add(0, OFFLINE, 3, R.string.menu_old_mode);
         return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case OFFLINE:
			{
				Intent oldmode = new Intent(this, SelectBacinoActivity.class);
				startActivity(oldmode);
				return true;
			}
			case R.id.menu_about:
			{
				new About(this).show();
				return true;
			}
			case R.id.menu_credits:
			{
				new Credits(this).show();
				return true;
			}	
			case R.id.menu_infos:
			{
				Intent infos = new Intent(this, InfoActivity.class);
				myStartActivity(infos);
				return true;
			}
		}
		return false;
	}
    
    public void showDialog(int id, int res)
	{
		Dialog dia = onCreateDialog(id, res);
		if(dia != null)
			dia.show();
	}
    
    protected Dialog onCreateDialog(int id, int res) {
		switch (id) {
		case DOWNLOAD_AVAILABLE:
			return createDownloadAlertDialog(R.string.download_available);
		case NO_SD_CARD:
			return createErrorDialog(R.string.sd_card_not_mounted);
		case DOWNLOAD_FILES:
			Intent down = new Intent(this, CheckDatabaseActivity.class);
			myStartActivity(down);
			return null;
		default:
			return null;
		}
	}
    
    public final Dialog createErrorDialog(int msg) 
   	{
   		AlertDialog.Builder builder = new AlertDialog.Builder(this);
   		// builder.setTitle(R.string.a_given_string);
   		builder.setIcon(R.drawable.icon);
   		builder.setMessage(msg);
   		builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

   			@Override
   			public void onClick(DialogInterface dialog, int id) {
   				dialog.dismiss();
   				System.exit(-3);
   			}
   		});
   		
   		return builder.create();
   	}
    
    
    public void myStartActivity(Intent intent)
    {
    	if(!haveNetworkConnection())
    	{
    		createOfflineAlertDialog();
    		return;
    	}
    	if(updatecheck != null)
		{
			Log.v("HomeActivity", "AsynTask cancelled!");
			updatecheck.cancel(true);
		}
    	super.startActivity(intent);
    }
    
    /**
	 * this method checks if a networkconnection is active or not
	 * @return boolean if the network is reachable or not
	 */
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (this.getSystemService(Context.CONNECTIVITY_SERVICE));
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
    
	private void createOfflineAlertDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.information);
		builder.setMessage(R.string.offline_info);
		
		builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent oldmode = new Intent(getThis(), SelectBacinoActivity.class);
				startActivity(oldmode);
			}
			
		});
		builder.create().show();
		
	}
	
    public final Dialog createDownloadAlertDialog(int msg) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.a_given_string);
		builder.setIcon(R.drawable.icon);
		//builder.setMessage(msg);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				Intent download = new Intent(getThis(), CheckDatabaseActivity.class);
				myStartActivity(download);
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
    
}
