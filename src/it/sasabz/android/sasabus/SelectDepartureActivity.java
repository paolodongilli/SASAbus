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
import it.sasabz.android.sasabus.R.id;
import it.sasabz.android.sasabus.R.layout;
import it.sasabz.android.sasabus.R.menu;
import it.sasabz.android.sasabus.R.string;
import it.sasabz.android.sasabus.classes.*;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectDepartureActivity extends ListActivity {

    
    private Vector<DBObject> list = null;
    
    private int linea;
    
    private Bacino bacino = null;
    
    public SelectDepartureActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        linea = 0;
        int bacinonr = 0;
		if (extras != null) {
			linea = extras.getInt("linea");
			bacinonr = extras.getInt("bacino");
		}
		bacino = BacinoList.getById(bacinonr);
        Linea line = LineaList.getById(linea, bacino.getTable_prefix());
        if(line == null)
        {
        	Toast.makeText(this, R.string.error_application, Toast.LENGTH_LONG).show();
        	finish();
        }
        setContentView(R.layout.palina_listview_layout);
        TextView titel = (TextView)findViewById(R.id.untertitel);
        titel.setText(R.string.select_palina);
        
        Resources res = getResources();
        
        TextView lineat = (TextView)findViewById(R.id.line);
        TextView from = (TextView)findViewById(R.id.from);
        TextView to = (TextView)findViewById(R.id.to);
        
        lineat.setText(res.getString(R.string.line_txt) + " " + line.toString());
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
    	Palina arrival = (Palina)list.get(position);
    	Intent selDest = new Intent(this, SelectArrivalActivity.class);
    	selDest.putExtra("arrival", arrival.getName_de());
    	selDest.putExtra("bacino", bacino.getId());
    	selDest.putExtra("linea", linea);
    	startActivity(selDest);
    	
    }

    /**
     * this method gets a list of palinas and fills the list_view with the palinas
     */
    private void fillData() {
    	list = PalinaList.getListLinea(linea, bacino.getTable_prefix());
    	MyListAdapter destinazioni = new MyListAdapter(SASAbus.getContext(), R.id.text, R.layout.departure_row, list);
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
