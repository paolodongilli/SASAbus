/**
 *
 * NextBusFragment.java
 *
 * Created: Jun 19, 2013 09:41:00 AM
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.sasabz.sasabus.ui.busstop;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.BusSchedulesDatabase;
import it.sasabz.sasabus.ui.Utility;
import it.sasabz.sasabus.ui.busschedules.BuslineDepartureAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 *Fragment for the Tab with the next Bus
 */
public class NextBusFragment extends SherlockFragment{

	private ListView listviewNextBuses;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_next_bus, container, false);
		
		initializeViews(view);
		
		addAdapterToListView();
		
		return view;
	}
	
	private void initializeViews(View view) {
		listviewNextBuses = (ListView) view.findViewById(R.id.listview_next_buses);
	}
	
	private void addAdapterToListView() {
		ListAdapter adapter = new NextBusAdapter(getSherlockActivity(),
				R.layout.listview_item_next_bus, R.id.textview_busline, 
				BusSchedulesDatabase.getNextBusesItineraryForBusstop(null));
		listviewNextBuses.setAdapter(adapter);
//		Utility.getListViewSize(listviewBuslineDepartures);
	}
	
}
