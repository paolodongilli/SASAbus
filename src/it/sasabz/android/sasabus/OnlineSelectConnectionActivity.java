/**
 *
 * OnlineSelectConnectionActivity.java
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
import java.util.Vector;


import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.ConnectionDialog;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.MyXMLConnectionRequestAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.hafas.XMLAttributVariante;
import it.sasabz.android.sasabus.hafas.XMLConnection;
import it.sasabz.android.sasabus.hafas.XMLConnectionRequest;
import it.sasabz.android.sasabus.hafas.XMLJourney;
import it.sasabz.android.sasabus.hafas.XMLRequest;
import it.sasabz.android.sasabus.hafas.XMLStation;
import it.sasabz.android.sasabus.hafas.XMLWalk;
import it.sasabz.android.sasabus.hafas.services.XMLConnectionRequestList;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineSelectConnectionActivity extends ListActivity {

    
    private XMLStation from = null;
    private XMLStation to = null;
    private Date datetime = null;
    
    public static final int NO_DATA = 0;
    
    private ProgressDialog progress;
    
    
    private Vector<XMLConnectionRequest> list = null;
    
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
		
		progress = new ProgressDialog(this);
		progress.setMessage(getResources().getString(R.string.waiting));
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.show();
		
		XMLConnectionRequestList req = new XMLConnectionRequestList(from, to, datetime, this);
		req.execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	XMLConnectionRequest conreq = list.get(position);
    	if(conreq.getConnectionlist() == null)
    	{
    		Log.v("XML-LOGGER", "Die Liste der Verbindungsdetails  ist null!!!!");
    	}
    	ConnectionDialog dial = new ConnectionDialog(this, conreq.getConnectionlist());
    	dial.show();
    }
    

    public void fillData(Vector<XMLConnectionRequest> list)
    {
		this.list = list;
		
		MyXMLConnectionRequestAdapter adapter = new MyXMLConnectionRequestAdapter(list);
		
		setListAdapter(adapter);
		
		progress.dismiss();
    }
    
    
    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void myShowDialog(int res)
    {
    	if(progress != null)
    		progress.dismiss();
    	switch(res)
    	{
    	case NO_DATA:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setCancelable(false);
			builder.setMessage(R.string.error_connection);
			builder.setTitle(R.string.error);
			builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					finish();
				}
			});
			builder.create().show();
    		break;
    	}
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
