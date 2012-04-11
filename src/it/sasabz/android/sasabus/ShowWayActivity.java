/**
 *
 * ShowWayActivity.java
 * 
 * Created: Mar 15, 2012 22:40:06 PM
 * 
 * Copyright (C) 2012 Paolo Dongilli and Markus Windegger
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
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.MyPassaggioListAdapter;
import it.sasabz.android.sasabus.classes.MyWayListAdapter;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.classes.Passaggio;
import it.sasabz.android.sasabus.classes.PassaggioList;
import it.sasabz.android.sasabus.classes.SharedMenu;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ShowWayActivity extends ListActivity {


	
	//provides the linea for this object
	private int orarioId;

	//provides the destination for this object
	private String destinazione;
	
	//provides the list for this object of all passages during the actual day
	private Vector<Passaggio> list = null;
	
	/*
	 * is the position of the most actual bus-stop, where the bus at
	 * the moment is when he is in time :)
	 */
	private int pos;
	
	//testfinal
	private final int POINTER = 10;

	public ShowWayActivity() {
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		orarioId = 0;
		destinazione = null;
		if (extras != null) {
			orarioId = extras.getInt("orario");
			destinazione = extras.getString("destinazione");
		}

		setContentView(R.layout.way_layout);
		fillData();
		if (pos != -1) {
			getListView().setSelection(pos);
		}
	}

	/**
	 * Called when the activity is about to start interacting with the user.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * fills the listview with the timetable
	 * @return a cursor to the time table
	 */
	private void fillData() {
		list = PassaggioList.getVectorWay(orarioId, destinazione);
		pos = getNextTimePosition(list);
		int[] wherelist = {R.id.palina, R.id.orario};
        MyWayListAdapter paline = new MyWayListAdapter(this, wherelist, R.layout.way_row, list, pos);
        setListAdapter(paline);
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
		// menu.add(...); // specific to this activity
		SharedMenu.onCreateOptionsMenu(menu);
		menu.add(0, POINTER, 3, R.string.pointing);
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
			case SharedMenu.MENU_SETTINGS:
			{
				Intent settings = new Intent(this, SetSettingsActivity.class);
				startActivity(settings);
				return true;
			}
			case POINTER:
			{
				Intent pointeract = new Intent(this, PointingLocationActivity.class);
				Passaggio pas = PassaggioList.getById(orarioId);
				pointeract.putExtra("palina", pas.getIdPalina());
				startActivity(pointeract);
				return true;
			}
		}
		return false;
	}
}
