/**
 * 
 *
 * SelectDestinazioneLocationActivity.java
 * 
 * Created: 23.01.2012 17:37:02
 * 
 * Copyright (C) 2011 Paolo Dongilli and Markus Windegger
 * 
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
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectDestinazioneLocationActivity extends ListActivity {

	//this is the list which provides the locations in the entire activity
    private Vector<DBObject> list = null;
    
    //this string "saves" the chosen parture-busstop 
    private String partenza;
    
    public SelectDestinazioneLocationActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        partenza = null;
		if (extras != null) {
			partenza = extras.getString("partenza");
		}
        
		Palina part = PalinaList.getTranslation(partenza, "de");
		if(part == null)
		{
			Toast.makeText(this, R.string.select_destination, Toast.LENGTH_LONG);
			finish();
		}
        setContentView(R.layout.standard_listview_layout);
        Resources res = getResources();
        String titelstring = res.getString(R.string.select_destination) + ": (" + part.toString() + " -> ?)";
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(titelstring);
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
    	Intent selDest = new Intent(this, SelectLineaLocationActivity.class);
    	selDest.putExtra("destinazione", destinazione.getName_de());
    	selDest.putExtra("partenza", partenza);
    	startActivity(selDest);
    	
    }

    /**
     * This method gets a list of possible parture bus-stops and fill them into the list-view
     */
    private void fillData() {
    	list = PalinaList.getListPartenza(partenza);
    	MyListAdapter destinazioni = new MyListAdapter(SASAbus.getContext(), R.id.text, R.layout.standard_row, list);
        setListAdapter(destinazioni);
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

