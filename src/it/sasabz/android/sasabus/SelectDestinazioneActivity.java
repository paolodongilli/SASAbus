/**
 *
 * SelectDestinazioneActivity.java
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

import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;

public class SelectDestinazioneActivity extends ListActivity {

    
    private Vector<DBObject> list = null;
    
    private int linea;
    
    public SelectDestinazioneActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        linea = 0;
		if (extras != null) {
			linea = extras.getInt("linea");
		}
        
        setContentView(R.layout.select_destinazione_layout);
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
    	Palina destinazione = (Palina)list.get(position);
    	Intent selDest = new Intent(this, SelectPalinaActivity.class);
    	selDest.putExtra("destinazione", destinazione.getName_de());
    	selDest.putExtra("linea", linea);
    	startActivity(selDest);
    	
    }

    /**
     * this method gets a list of palinas and fills the list_view with the palinas
     */
    private void fillData() {
    	list = PalinaList.getListLinea(linea);
    	MyListAdapter destinazioni = new MyListAdapter(SASAbus.getContext(), R.id.destinazione, R.layout.destinazioni_row, list);
        setListAdapter(destinazioni);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(...);  // specific to this activity
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case SharedMenu.MENU_ABOUT:
			{
				new About(this).show();
				return true;
			}
			case SharedMenu.MENU_CREDITS:
			{
				new Credits(this).show();
				return true;
			}	
			case SharedMenu.MENU_SETTINGS:
			{
				Intent settings = new Intent(this, SettingsActivity.class);
				startActivity(settings);
				return true;
			}
		}
		return false;
	}
}
