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
import it.sasabz.android.sasabus.classes.PassaggioList;
import it.sasabz.android.sasabus.classes.SharedMenu;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class ShowOrariLocationActivity extends ListActivity {
	
	//provides the linea for this object
		private int linea;

		//provides the destination for this object
		private String destinazione;
		
		//provides the departure in this object
		private String  partenza;

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

		setContentView(R.layout.show_orari_layout);
		Cursor c = fillData();
		int pos = getNextTimePosition(c);
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
	 * this method fills a list_view with the timetable
	 * @return a Cursor to the time table
	 */
	private Cursor fillData() {
		// Get next 'orari' from the database and create the item list
		Cursor c = PassaggioList.getCursor(linea, destinazione, partenza);
		startManagingCursor(c);
		String[] from = new String[] { "_id" };
		int[] to = new int[] { R.id.orario };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter orari = new SimpleCursorAdapter(this,
				R.layout.orari_row, c, from, to) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final View row = super.getView(position, convertView, parent);
				Cursor c = getCursor();
				c.moveToPosition(position);
				// get current time
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
				Calendar cal = Calendar.getInstance();
				try {
					Date currentTime = timeFormat.parse(timeFormat.format(cal
							.getTime()));
					Date sasaTime = timeFormat.parse(c.getString(0));
					if (sasaTime.after(currentTime))
						row.setBackgroundColor(Color.rgb(0, 70, 0));
					else if (sasaTime.before(currentTime))
						row.setBackgroundColor(Color.rgb(70, 0, 0));
					else
						row.setBackgroundColor(Color.rgb(255, 125, 33));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return row;
			}
		};

		setListAdapter(orari);
		return c;
	}

	/**
	 * This method gets the next departure time and returns the
	 * index of this element
	 * @param c is the cursor to the list_view
	 * @return the index of the next departure time
	 */
	private int getNextTimePosition(Cursor c) {
		int count = c.getCount();
		if (count == 0) {
			return -1;
		} else if (count == 1) {
			return 0;
		} else {
			int i = 0;
			boolean found = false;
			while (i <= count-2 && !found) {
				c.moveToPosition(i);
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
				Calendar cal = Calendar.getInstance();

				Date currentTime;
				Date sasaTime;
				Date sasaTimeNext;
				try {
					currentTime = timeFormat.parse(timeFormat.format(cal
							.getTime()));
					sasaTime = timeFormat.parse(c.getString(0));
					c.moveToPosition(i + 1);
					sasaTimeNext = timeFormat.parse(c.getString(0));

					if (sasaTime.after(currentTime)
							|| sasaTime.equals(currentTime)
							|| sasaTime.before(currentTime)
							&& (sasaTimeNext.equals(currentTime) || sasaTimeNext
									.after(currentTime)))
						found = true;
					else
						i++;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
