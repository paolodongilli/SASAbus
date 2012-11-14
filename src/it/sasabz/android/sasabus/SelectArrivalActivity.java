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
import it.sasabz.android.sasabus.classes.Bacino;
import it.sasabz.android.sasabus.classes.BacinoList;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Linea;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectArrivalActivity extends ListActivity {

	//saves the linea global for this object
    private int linea;
    
    //saves the arrival global for this object
    private String arrivo;
    
    //saves the list of possible parture bus-stops for this object
    private Vector<DBObject> list;

    private Bacino bacino = null;
    private Palina arrival = null;
    
    public SelectArrivalActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
		linea = 0;
		arrivo = null;
		int bacinonr = 0;
		if (extras != null) {
			linea = extras.getInt("linea");
			arrivo = extras.getString("arrival");
			bacinonr = extras.getInt("bacino");
		}
		bacino = BacinoList.getById(bacinonr);
		arrival = PalinaList.getTranslation(arrivo, "de");
		Linea line = LineaList.getById(linea, bacino.getTable_prefix());
		if(arrival == null || line == null)
		{
			Toast.makeText(this, R.string.error_application, Toast.LENGTH_LONG).show();
			finish();
		}
		setContentView(R.layout.palina_listview_layout);
        TextView titel = (TextView)findViewById(R.id.untertitel);
        titel.setText(R.string.select_destination);
        
        Resources res = getResources();
        
        TextView lineat = (TextView)findViewById(R.id.line);
        TextView from = (TextView)findViewById(R.id.from);
        TextView to = (TextView)findViewById(R.id.to);
        
        lineat.setText(res.getString(R.string.line) + " " + line.toString());
        from.setText("");
        to.setText(res.getString(R.string.from) + " " + arrival.toString());
        
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
    	showOrario.putExtra("palina", arrival.getName_de());
    	Palina palina = (Palina)list.get(position);
    	showOrario.putExtra("destinazione", palina.getName_de());
    	showOrario.putExtra("bacino", bacino.getId());
    	startActivity(showOrario);
    }

    /**
     * this method fills the possible parture busstops into the list_view
     */
    private void fillData() {
        list = PalinaList.getListDestinazione(arrivo, linea, bacino.getTable_prefix());
        MyListAdapter paline = new MyListAdapter(SASAbus.getContext(), R.id.text, R.layout.arrival_row, list);
        setListAdapter(paline);
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
