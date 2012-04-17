/**
 *
 * SelectLineaLocationActivity.java
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

import java.util.Locale;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Linea;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.MyListAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SelectLineaLocationActivity extends ListActivity {

	//this is the list of lines
    private Vector<DBObject> list = null;
    
    //this is the parture busstop name
    private String partenza = null;
    
    //this is the destination busstop name
    private String destinazione = null;
    
    
    public SelectLineaLocationActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		partenza = null;
		destinazione = null;
		if (extras != null) {
			partenza = extras.getString("partenza");
			destinazione = extras.getString("destinazione");
		}
        setContentView(R.layout.select_linea_layout);
        fillData();
    }

    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        int linea = list.get(position).getId();
    	Intent selDest = new Intent(this, ShowOrariLocationActivity.class);;
    	selDest.putExtra("linea", linea);
    	selDest.putExtra("partenza", partenza);
    	selDest.putExtra("destinazione", destinazione);
    	startActivity(selDest);
    }
    
    /**
     * this method fills the list_view with the lines which connect the parture busstop 
     * with the destination busstop
     */
    private void fillData() {
    	list = LineaList.getListDestPart(destinazione, partenza);
    	MyListAdapter linee = new MyListAdapter(SASAbus.getContext(), R.id.linea, R.layout.linee_row, list);
        setListAdapter(linee);
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
		}
		return false;
	}
}
