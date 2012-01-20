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

import java.util.Locale;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;

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

<<<<<<< .merge_file_kzv79E
    private String bacino;
    private String linea;
    private String destinazione;
=======
    private static final int MENU_ABOUT = 0;
    
    private Vector<DBObject> list = null;
    
    private int linea;
>>>>>>> .merge_file_sJKukF
    
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
    
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case SharedMenu.MENU_ABOUT:
			{
				new About(this).show();
				return true;
			}
			case SharedMenu.MENU_TEST:
			{
				Intent selLinea = new Intent(this, SelectLineaActivity.class);
				selLinea.putExtra("bacino", "Merano-Meran");
				startActivity(selLinea);
				return true;
			}
		}
		return false;
	}
}
