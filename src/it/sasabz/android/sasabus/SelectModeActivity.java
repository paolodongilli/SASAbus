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

import java.util.Locale;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Bacino;
import it.sasabz.android.sasabus.classes.BacinoList;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.Modus;
import it.sasabz.android.sasabus.classes.MyListAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
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

public class SelectModeActivity extends ListActivity {

    
    private Vector<DBObject> list = null;
    
    public SelectModeActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.standard_listview_layout);
        
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(R.string.select_mode);
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
        int mode = list.get(position).getId();
        /*
         * If the mode select is the first one, then starts the gps-mode,
         * otherwise with the mode 2 selected, starts the normal mode
         */
        if(mode == 1)
        {
        	Intent selLinea = new Intent(this, SelectPalinaLocationActivity.class);
        	startActivity(selLinea);
        }
        if(mode == 2)
        {
        	Intent selLinea = new Intent(this, SelectBacinoActivity.class);
        	startActivity(selLinea);
        }
    }
    
    /**
     * fills the list_view with the modes which are offered to the user
     */
    public void fillData()
    {
    	Resources res = this.getResources();
    	
    	list = new Vector<DBObject>();
    	
    	//GPS Mode
    	Modus mod = new Modus();
    	mod.setId(1);
    	mod.setString(res.getString(R.string.mode_gps));
    	list.add(mod);
    	
    	//Normal Mode
    	mod = new Modus();
    	mod.setId(2);
    	mod.setString(res.getString(R.string.mode_normal));
    	list.add(mod);
    	//fill the modes into the list_view
    	MyListAdapter modi = new MyListAdapter(SASAbus.getContext(), R.id.text, R.layout.standard_row, list);
        setListAdapter(modi);
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
