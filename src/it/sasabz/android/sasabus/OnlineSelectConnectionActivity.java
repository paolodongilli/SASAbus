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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.MyXMLConnectionRequestListAdapter;
import it.sasabz.android.sasabus.hafas.XMLConnectionRequest;
import it.sasabz.android.sasabus.hafas.XMLConnectionRequestList;
import it.sasabz.android.sasabus.hafas.XMLRequest;
import it.sasabz.android.sasabus.hafas.XMLStation;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineSelectConnectionActivity extends ListActivity {

    
    private XMLStation from = null;
    private XMLStation to = null;
    private Date datetime = null;
    
    public OnlineSelectConnectionActivity() {
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
        setContentView(R.layout.connection_listview_layout);
        
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(R.string.mode_online);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			from = new XMLStation();
			from.fromXMLString(extras.getString("from"));
			to = new XMLStation();
			to.fromXMLString(extras.getString("to"));
			SimpleDateFormat simple = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			try
			{
				datetime = simple.parse(extras.getString("datetime"));
			}
			catch(Exception e)
			{
				Log.v("Datumsfehler", "Das Datum hat eine falsche Formatierung angenommen!!!");
				Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
		}	
		
				
		XMLConnectionRequest conreq	= XMLConnectionRequestList.getRequest(from, to, datetime);
		if(conreq != null)
		{
			SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
			Log.v("XML-LOGGER", "conreq-context: " + conreq.getContext());
			Log.v("XML-LOGGER", "transfers: " + conreq.getTransfers());
			Log.v("XML-LOGGER", "duration: " + simple.format(conreq.getDuration()));
			Log.v("XML-LOGGER", "Departure: " + conreq.getDeparture().getStation().getName());
			Log.v("XML-LOGGER", "Departure Time: " + simple.format(conreq.getDeparture().getArrtime()));
		}
		
		fillData();
    }


    private void fillData()
    {
    	XMLConnectionRequest conreq	= XMLConnectionRequestList.getRequest(from, to, datetime);
    	Vector<XMLConnectionRequest> list = null;
		if(conreq != null)
		{
			SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
			list = new Vector<XMLConnectionRequest>();
			list.add(conreq);
		}
		
		MyXMLConnectionRequestListAdapter adapter = new MyXMLConnectionRequestListAdapter(list);
		
		setListAdapter(adapter);
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
