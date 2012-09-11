/**
 *
 * SelectModeActivity.java
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


import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.DateTimePicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineModeActivity extends Activity {

    
    private Vector<DBObject> list = null;
    
    public OnlineModeActivity() {
    }

    private Context getContext()
    {
    	return this;
    }
    
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.online_search_layout);
        
        Date datum = new Date();
        
        TextView datetime = (TextView)findViewById(R.id.time);
        
        datetime.setText(datum.getDay() + "." + datum.getMonth() + "." + (datum.getYear() + 1900) 
        		+ " " + datum.getHours() + ":" + datum.getMinutes());
        
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(R.string.mode_online);
        
        Button search = (Button)findViewById(R.id.search);
        
        search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText from = (EditText)findViewById(R.id.from_text);
				EditText to = (EditText)findViewById(R.id.to_text);
				
				if(!from.getText().toString().trim().equals("") && !to.getText().toString().trim().equals(""))
				{
					Intent getSelect = new Intent(getContext(), OnlineSelectStopActivity.class);
					getSelect.putExtra("from", from.getText().toString());
					getSelect.putExtra("to", to.getText().toString());
					startActivity(getSelect);
				}
			}
		});
        
        ImageButton datepicker = (ImageButton)findViewById(R.id.datepicker);
        
        datepicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(),
						"This method is not implemened yet", Toast.LENGTH_LONG)
						.show();
				// Create the dialog
				final Dialog mDateTimeDialog = new Dialog(getContext());
				// Inflate the root layout
				final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
						.inflate(R.layout.date_time_dialog, null);
				// Grab widget instance
				final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
						.findViewById(R.id.DateTimePicker);
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
							datetimestring += mDateTimePicker
									.get(Calendar.DAY_OF_MONTH)
									+ "."
									+ (mDateTimePicker.get(Calendar.MONTH) + 1)
									+ "."
									+ mDateTimePicker
											.get(Calendar.YEAR) + " ";
							if (mDateTimePicker.is24HourView()) {
								datetimestring += mDateTimePicker
										.get(Calendar.HOUR_OF_DAY)
										+ ":"
										+ mDateTimePicker
												.get(Calendar.MINUTE);
							} else {
								datetimestring += mDateTimePicker
										.get(Calendar.HOUR)
										+ ":"
										+ mDateTimePicker
												.get(Calendar.MINUTE)
										+ " "
										+ (mDateTimePicker
												.get(Calendar.AM_PM) == Calendar.AM ? "AM"
												: "PM");
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

	}


    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 super.onCreateOptionsMenu(menu);
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.optionmenu, menu);
         return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
			case R.id.menu_settings:
			{
				Intent settings = new Intent(this, SetSettingsActivity.class);
				startActivity(settings);
				return true;
			}
			case R.id.menu_infos:
			{
				Intent infos = new Intent(this, InfoActivity.class);
				startActivity(infos);
				return true;
			}
		}
		return false;
	}
}
