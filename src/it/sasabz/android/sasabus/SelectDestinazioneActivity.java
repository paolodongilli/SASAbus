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
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        Linea line = LineaList.getById(linea);
        if(line == null)
        {
        	Toast.makeText(this, R.string.error_application, Toast.LENGTH_LONG);
        	finish();
        }
        setContentView(R.layout.standard_listview_layout);
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(R.string.select_destination);
        
        Resources res = getResources();
        
        TextView lineat = (TextView)findViewById(R.id.line);
        TextView from = (TextView)findViewById(R.id.from);
        TextView to = (TextView)findViewById(R.id.to);
        
        lineat.setText(res.getString(R.string.line) + " " + line.toString());
        from.setText("");
        to.setText("");
        
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
