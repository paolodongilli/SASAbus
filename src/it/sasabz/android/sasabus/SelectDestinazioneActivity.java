/**
 *
 * SelectLineaActivity.java
 * 
 * Created: Jan 16, 2011 11:41:06 AM
 * 
 * Copyright (C) 2011 Paolo Dongilli
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

import it.sasabz.android.sasabus.R;

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

public class SelectDestinazioneActivity extends ListActivity {

    private static final int MENU_ABOUT = 0;
	private SasaDbAdapter mDbHelper;
    private String bacino;
    private String linea;
    private String destinazione;
    
    public SelectDestinazioneActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
		bacino = null;
		linea = null;
		if (extras != null) {
			bacino = extras.getString("bacino");
			linea = extras.getString("linea");
		}
        
        setContentView(R.layout.select_destinazione_layout);
        mDbHelper = new SasaDbAdapter(this);
        mDbHelper.open();
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
        TextView textView = (TextView) v.findViewById(R.id.destinazione);
        destinazione = textView.getText().toString(); 
    	Intent selPalina = new Intent(this, SelectPalinaActivity.class);
    	selPalina.putExtra("bacino", bacino);
    	selPalina.putExtra("linea", linea);
    	selPalina.putExtra("destinazione", destinazione);
    	startActivity(selPalina);
    }

    
    private void fillData() {
        // Get all 'destinazioni' from the database and create the item list
        Cursor c = mDbHelper.fetchDestinazioni(bacino,linea);
        startManagingCursor(c);

        String[] from = new String[] { "_id" };
        int[] to = new int[] { R.id.destinazione };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter destinazioni =
            new SimpleCursorAdapter(this, R.layout.destinazioni_row, c, from, to);
        setListAdapter(destinazioni);
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
