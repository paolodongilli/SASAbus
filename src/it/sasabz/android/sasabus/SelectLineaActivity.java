/**
 *
 * SelectLineaActivity.java
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

public class SelectLineaActivity extends ListActivity {
	
	//this vector provides the list of lines in the entire activity
    private Vector<DBObject> list = null;
    
    public SelectLineaActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		int bacino = 0;
		if (extras != null) {
			bacino = extras.getInt("bacino");
		}
        setContentView(R.layout.standard_listview_layout);
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(R.string.select_linea);
        
        TextView line = (TextView)findViewById(R.id.line);
        TextView from = (TextView)findViewById(R.id.from);
        TextView to = (TextView)findViewById(R.id.to);
        
        line.setText("");
        from.setText("");
        to.setText("");
        
        
        fillData(bacino);
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
        Log.v("LINEA ID", Integer.toString(linea));
    	Intent selDest = new Intent(this, SelectDestinazioneActivity.class);;
    	selDest.putExtra("linea", linea);
    	startActivity(selDest);
    }
    
    /**
     * this method fills the list_view with the lines which are situated into the bacino bacino
     * @param bacino is the bacino chosen for getting the lines
     */
    private void fillData(int bacino) {
    	list = LineaList.getList(bacino);
    	list = LineaList.sort(list);
    	MyListAdapter linee = new MyListAdapter(SASAbus.getContext(), R.id.text, R.layout.standard_row, list);
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
