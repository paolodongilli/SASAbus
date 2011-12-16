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

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.BacinoList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SelectBacinoActivity extends ListActivity {

    private static final int MENU_ABOUT = 0;
    
    public SelectBacinoActivity() {
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
        TextView textView = (TextView) v.findViewById(R.id.bacino);
        String bacino = textView.getText().toString(); 
    	Intent selLinea = new Intent(this, SelectLineaActivity.class);
    	selLinea.putExtra("bacino", bacino);
    	startActivity(selLinea);
    }
    
    private void fillData() {
    	// Get all 'bacini' from the database and create the item list
    	Cursor c = BacinoList.getCursor();
        startManagingCursor(c);
        String bacino = "nome_it";
        if(Locale.getDefault().equals(Locale.GERMANY))
        {
        	bacino = "nome_de";
        }
        String[] from = new String[] { "_id", bacino };
        int[] to = new int[] { R.id.bacinoId, R.id.bacino };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter bacini =
            new SimpleCursorAdapter(this, R.layout.bacini_row, c, from, to);
        setListAdapter(bacini);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(...);  // specific to this activity
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ABOUT: new About(this).show();
			return true;
			// ...
		}
		return false;
	}
}
