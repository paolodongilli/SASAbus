/**
 *
 * SelectPalinaActivity.java
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
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.classes.SharedMenu;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class SelectPalinaActivity extends ListActivity {

	//saves the linea global for this object
    private int linea;
    
    //saves the destination global for this object
    private String destinazione;
    
    //saves the list of possible parture bus-stops for this object
    private Vector<DBObject> list;

    
    public SelectPalinaActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
		linea = 0;
		destinazione = null;
		if (extras != null) {
			linea = extras.getInt("linea");
			destinazione = extras.getString("destinazione");
		}
        setContentView(R.layout.select_palina_layout);
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
    	Intent showOrario = new Intent(this, ShowOrariActivity.class);
    	showOrario.putExtra("linea", linea);
    	showOrario.putExtra("destinazione", destinazione);
    	Palina palina = (Palina)list.get(position);
    	showOrario.putExtra("palina", palina.getName_de());
    	startActivity(showOrario);
    }

    /**
     * this method fills the possible parture busstops into the list_view
     */
    private void fillData() {
        list = PalinaList.getListDestinazione(destinazione, linea);
        MyListAdapter paline = new MyListAdapter(SASAbus.getContext(), R.id.palina, R.layout.paline_row, list);
        setListAdapter(paline);
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
		}
		return false;
	}
}
