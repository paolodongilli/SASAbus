/**
 *
 * OnlineSelectStopActivity.java
 * 
 * 
 * Copyright (C) 2012 Markus Windegger
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
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.id;
import it.sasabz.android.sasabus.R.layout;
import it.sasabz.android.sasabus.R.menu;
import it.sasabz.android.sasabus.R.string;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Bacino;
import it.sasabz.android.sasabus.classes.BacinoList;
import it.sasabz.android.sasabus.classes.ConnectionDialog;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.Modus;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.MyXMLConnectionRequestAdapter;
import it.sasabz.android.sasabus.classes.MyXMLStationListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.hafas.XMLConnectionRequest;
import it.sasabz.android.sasabus.hafas.XMLRequest;
import it.sasabz.android.sasabus.hafas.XMLStation;
import it.sasabz.android.sasabus.hafas.services.XMLConnectionRequestList;
import it.sasabz.android.sasabus.hafas.services.XMLStationList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineSelectStopActivity extends Activity {

    private ProgressDialog progress = null;
    private String from = "";
    private String to = "";
    private Palina fromPalina = null;
    private Palina toPalina = null;
    private Date datum = null;
    private Spinner from_spinner = null;
    private Spinner to_spinner = null;
    private Button search;
    
    
    public static final int XML_FAILURE = 0;
    public static final int NO_DATA = 1;
    
	public final static int OFFLINE = 34;
	
	private XMLStationList statlist = null;

    
    private Vector<XMLConnectionRequest> list = null;
    
    public OnlineSelectStopActivity() {
    }

    private Context getContext()
    {
    	return this;
    }
    
    
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!XMLRequest.haveNetworkConnection())
        {
        	Toast.makeText(getContext(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			from = extras.getString("from");
			to = extras.getString("to");
			String lang = "it";
			if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
				lang = "de";
			fromPalina = PalinaList.getTranslation(from, lang);
			toPalina = PalinaList.getTranslation(to, lang);
			SimpleDateFormat simple = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			try
			{
				datum = simple.parse(extras.getString("datetime"));
			}
			catch(Exception e)
			{
				Log.v("Datumsfehler", "Das Datum hat eine falsche Formatierung angenommen!!!");
				Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
		}
		
		 if(from == "" || to == "")
	        {
	        	Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
	        	Log.v("SELECT STOP ERROR", "From: " + from + " | To: " + to);
	        	finish();
	        	return;
	        }
		
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getText(R.string.waiting));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
        
        statlist = new XMLStationList(this);
        if(toPalina != null)
        {
        	to = toPalina.getHaltestelle();
        	Log.v("ONLINE-SELECT-TO", to);
        }
        if(fromPalina != null)
        {
        	from = fromPalina.getHaltestelle();
        	Log.v("ONLINE-SELECT-FROM", from);
        }
        statlist.execute(from, to);
    }


    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    
    public void fillSpinner(Vector<XMLStation> from_list, Vector<XMLStation> to_list)
    {
    	String datetimestring = "";
    	SimpleDateFormat simple = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        datetimestring = simple.format(datum);
        
    	 if(from_list.size() == 1 && to_list.size() == 1)
         {
    		Log.v("Check", "Check");
         	XMLStation from = from_list.firstElement();
 			XMLStation to = to_list.firstElement();
 			
 			getConnectionList(from, to, datetimestring);
         }
    	 else if(from_list.size() == 1 && to.contains(to_list.get(0).getName()))
    	 {
    		Log.v("Check", "Check");
          	XMLStation from = from_list.firstElement();
  			XMLStation to = to_list.firstElement();
  			
  			getConnectionList(from, to, datetimestring);
    	 }
    	 else if(to_list.size() == 1 && from.contains(from_list.get(0).getName()))
    	 {
    		Log.v("Check", "Check");
          	XMLStation from = from_list.firstElement();
  			XMLStation to = to_list.firstElement();
  			
  			getConnectionList(from, to, datetimestring);
    	 }
    	 else
    	 {
	    	
	    	setContentView(R.layout.online_select_layout);
	    	
	    	TextView datetime = (TextView)findViewById(R.id.time);
	        
	        datetime.setText(datetimestring);
	        
	        progress.dismiss();
	        
	        
	        if(from_list == null || to_list == null)
	        {
	        	Toast.makeText(getContext(), R.string.online_connection_error, Toast.LENGTH_LONG).show();
	        	finish();
	        	return;
	        }
	        
	        
	        from_spinner = (Spinner) findViewById(R.id.from_spinner);
	        to_spinner = (Spinner) findViewById(R.id.to_spinner);
	        
	       
	        
	        // Create an ArrayAdapter using the string array and a default spinner layout
	        MyXMLStationListAdapter from_adapter = new MyXMLStationListAdapter(this, from_list);
	        // Create an ArrayAdapter using the string array and a default spinner layout
	        MyXMLStationListAdapter to_adapter = new MyXMLStationListAdapter(this, to_list);
	        
	        
	        // Apply the adapter to the spinner
	        from_spinner.setAdapter(from_adapter);
	        // Apply the adapter to the spinner
	        to_spinner.setAdapter(to_adapter);
	        
	        search = (Button)findViewById(R.id.search);
	        
	        search.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					XMLStation from = (XMLStation)from_spinner.getSelectedItem();
					XMLStation to = (XMLStation)to_spinner.getSelectedItem();
					TextView datetime = (TextView)findViewById(R.id.time);
					getConnectionList(from, to, datetime.getText().toString());
				}
			});
    	 }
    }
    
    public void getConnectionList(XMLStation from, XMLStation to, String datetime)
    {
    	progress.dismiss();
    	Intent showConnection = new Intent(getContext(), OnlineShowConnectionActivity.class);
		showConnection.putExtra("from", from.toXMLString());
		showConnection.putExtra("to", to.toXMLString());
		showConnection.putExtra("datetime", datetime);
		finish();
		startActivity(showConnection);
    }
    
    
    public AlertDialog getErrorDialog(String message)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setTitle(R.string.error);
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.dismiss();
				finish();
			}
		});
		return builder.create();
    }
    
    public void myShowDialog(int res)
    {
    	if(progress != null)
    		progress.dismiss();
    	switch(res)
    	{
    		case XML_FAILURE:
    			getErrorDialog(getResources().getString(R.string.error_station)).show();
    		break;
    		case NO_DATA:
    			getErrorDialog(getResources().getString(R.string.error_connection)).show();
    		break;
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
				startActivity(infos);
				return true;
			}
		}
		return false;
	}
}
