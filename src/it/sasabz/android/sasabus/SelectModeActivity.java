/**
 *
 * SelectLineaActivity.java
 * 
 * Created: Jan 16, 2011 11:41:06 AM
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
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
import it.sasabz.android.sasabus.classes.Bacino;
import it.sasabz.android.sasabus.classes.BacinoList;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.Modus;
import it.sasabz.android.sasabus.classes.MyListAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
        setContentView(R.layout.select_bacino_layout);
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
    
    public void fillData()
    {
    	list = new Vector<DBObject>();
    	
    	Modus mod = new Modus();
    	mod.setId(1);
    	mod.setString_de("GPS Modus");
    	mod.setString_it("Modo GPS");
    	list.add(mod);
    	
    	mod = new Modus();
    	mod.setId(2);
    	mod.setString_de("Normaler Modus");
    	mod.setString_it("Modo normale");
    	list.add(mod);
    	
    	MyListAdapter modi = new MyListAdapter(SASAbus.getContext(), R.id.linea, R.layout.linee_row, list);
        setListAdapter(modi);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(...);  // specific to this activity
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }

    
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case SharedMenu.MENU_ABOUT:
			{
				new About(this).show();
				return true;
			}
		}
		return false;
	}
}
