/**
 *
 * ShowOrarioLocationActivity.java
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

import java.util.Date;
import java.util.Vector;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Linea;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.MyPassaggioListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.classes.Passaggio;
import it.sasabz.android.sasabus.classes.PassaggioList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowOrariLocationActivity extends ListActivity {
	
	// provides the linea for this object
	private int linea;

	// provides the destination for this object
	private String destinazione;

	// provides the departure in this object
	private String partenza;
	/*
	 * stores all possible parture times from partenza to destinazione with the
	 * line linea
	 */
	private Vector<Passaggio> list;

	// //is the next departure time of the bus
	private int pos;
	

	public ShowOrariLocationActivity() {
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		linea = 0;
		destinazione = null;
		partenza = null;
		if (extras != null) {
			linea = extras.getInt("linea");
			destinazione = extras.getString("destinazione");
			partenza = extras.getString("partenza");
		}
		
		setContentView(R.layout.standard_listview_layout);
		TextView titel = (TextView)findViewById(R.id.titel);
		Palina destination = PalinaList.getTranslation(destinazione, "de");
		Palina departure = PalinaList.getTranslation(partenza, "de");
		Linea line = LineaList.getById(linea);
		if(destination == null || departure == null || line == null)
		{
			finish();
			Toast.makeText(this, R.string.error_application, Toast.LENGTH_LONG);
		}
		Resources res = getResources();
		String titelstring = res.getString(R.string.show_orari) + ": " + line.toString() + "(" + departure.toString() + "" + destination.toString() + ")";
		titel.setText(titelstring);
		
		fillData();
		
		if (pos != -1) {
			getListView().setSelection(pos);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int orario = list.get(position).getId();
		Intent showWay = new Intent(this, ShowWayActivity.class);
		showWay.putExtra("orario", orario);
		showWay.putExtra("destinazione", destinazione);
		startActivity(showWay);
	}
	
	
	
	/**
	 * Called when the activity is about to start interacting with the user.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	/**
	 * this method fills a list_view with the timetable
	 * @return a Cursor to the time table
	 */
	private void fillData() {
		list = PassaggioList.getVector(linea, destinazione, partenza);
		pos = getNextTimePosition(list);
		MyPassaggioListAdapter pass = new MyPassaggioListAdapter(this, R.id.text, R.layout.standard_row, list, pos);
		setListAdapter(pass);
	}

	/**
	 * This method gets the next departure time and returns the
	 * index of this element
	 * @param c is the cursor to the list_view
	 * @return the index of the next departure time
	 */
	private int getNextTimePosition(Vector<Passaggio> list) {
		int count = list.size();
		if (count == 0) {
			return -1;
		} else if (count == 1) {
			return 0;
		} else {
			int i = 0;
			boolean found = false;
			while (i <= count-2 && !found) {
				Time currentTime = new Time();
				Time sasaTime = new Time();
				Time sasaTimeNext = new Time();
				currentTime.setToNow();
				sasaTime = list.get(i).getOrario();
				sasaTimeNext = list.get(i + 1).getOrario();

				if (sasaTime.after(currentTime)
						|| sasaTime.equals(currentTime)
						|| sasaTime.before(currentTime)
						&& (sasaTimeNext.equals(currentTime) || sasaTimeNext
								.after(currentTime)))
				{
					found = true;
				}
				else
				{
					i++;
				}
			}
			return i;
		}
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
