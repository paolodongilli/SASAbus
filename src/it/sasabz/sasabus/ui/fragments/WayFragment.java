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

package it.sasabz.sasabus.ui.fragments;

import java.util.ArrayList;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.Area;
import it.sasabz.sasabus.data.models.BusLine;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.data.models.Itinerary;
import it.sasabz.sasabus.data.orm.AreaList;
import it.sasabz.sasabus.data.orm.BusLineList;
import it.sasabz.sasabus.data.orm.BusStopList;
import it.sasabz.sasabus.data.orm.ItineraryList;
import it.sasabz.sasabus.ui.adapter.MyWayListAdapter;
import it.sasabz.sasabus.ui.map.MapViewActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class WayFragment extends Fragment {


	
	//provides the linea for this object
	private Itinerary orario;
	
	//provides the list for this object of all passages during the actual day
	private ArrayList<Itinerary> list = null;
	
	//provides the lineaid for this object
	private BusLine linea;
	
	/*
	 * is the position of the most actual bus-stop, where the bus at
	 * the moment is when he is in time :)
	 */
	private int pos;
	
	private Area bacino = null;
	
	private BusStop arrival = null;
	
	private BusStop departure = null;

	private WayFragment() {
	
	}

	public WayFragment(Area bacino, BusLine linea, BusStop arrival, Itinerary orario) {
		this();
		this.bacino = bacino;
		this.linea = linea;
		this.arrival = arrival;
		this.orario = orario;
		departure = BusStopList.getBusStopById(orario.getBusStopId());
	}
	
	public WayFragment(String line, String from, String to, String orario_part, String orario_arr) throws Exception {
		String lang = "it";
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
			lang = "de";
		Log.v("SHOW-WAY-ACTIVITY", "Partenza: " + from);
		Log.v("SHOW-WAY-ACTIVITY", "Arrivo: " + to);
		departure = BusStopList.getBusStopTranslation(from.trim(), lang);
		arrival = BusStopList.getBusStopTranslation(to.trim(), lang);
		if(departure == null || arrival == null)
		{
			throw new Exception();
		}
		bacino = AreaList.getArea(departure.getName_de(), arrival.getName_de(), line);
		if(bacino == null)
		{
			throw new Exception();
		}
		linea = BusLineList.getBusLineByLineCode(line, bacino.getTable_prefix());
		if(linea == null)
		{
			throw new Exception();
		}
		orario = ItineraryList.getPassaggio(linea.getId(), departure.getName_de(), arrival.getName_de(), 
				orario_part, orario_arr, bacino.getTable_prefix());
		if (orario == null)
		{
			throw new Exception();
		}
	}
	

	@Override
	public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.way_listview_layout, container, false);
		
		TextView titel = (TextView)result.findViewById(R.id.untertitel);
		titel.setText(R.string.show_way);
		
		Resources res = getResources();
		
		TextView lineat = (TextView)result.findViewById(R.id.line);
        TextView from = (TextView)result.findViewById(R.id.from);
        TextView to = (TextView)result.findViewById(R.id.to);
        
        lineat.setText(res.getString(R.string.line_txt) + " " + linea.toString());
        from.setText(res.getString(R.string.from) + " " + departure.toString());
        to.setText(res.getString(R.string.to) + " " + arrival.toString());
		
		fillData(result);
		if (pos != -1) {
			((ListView)result.findViewById(android.R.id.list)).setSelection(pos);
		}
		
		Button mapButton = (Button)result.findViewById(R.id.mapview);
		
		mapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent mapview = new Intent(getActivity(), MapViewActivity.class);
				
				Itinerary part = ItineraryList.getById(orario.getId(), bacino.getTable_prefix());
				Itinerary dest = ItineraryList.getWayEndpoint(orario.getId(), arrival.getName_de(), bacino.getTable_prefix());
				
				mapview.putExtra("partenza", part.getBusStopId());
				mapview.putExtra("destinazione", dest.getBusStopId());
				mapview.putExtra("line", linea.getId());
				mapview.putExtra("orarioId", orario.getId());
				mapview.putExtra("position", pos);
				mapview.putExtra("bacino", bacino.getId());
				startActivity(mapview);
			}
		});

		return result;
	}
	
	
	public void createErrorDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(false);
		builder.setMessage(R.string.error_connection);
		builder.setTitle(R.string.error);
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
			}
		});
		builder.create();
		builder.show();
		getFragmentManager().popBackStack();
		return;	
	}
	
	/**
	 * fills the listview with the timetable
	 * @return a cursor to the time table
	 */
	private void fillData(View result) {
		list = ItineraryList.getWay(orario.getId(), arrival.getName_de(), bacino.getTable_prefix());
		if (list == null){
			createErrorDialog();
			return;
		}
		pos = getNextTimePosition(list);
        MyWayListAdapter way = new MyWayListAdapter(getActivity(), list, pos);
        ListView listview = (ListView)result.findViewById(android.R.id.list);
        listview.setAdapter(way);
        listview.setDividerHeight(0);
        listview.setDivider(null);
	}

	/**
	 * This method gets the next departure time and returns the
	 * index of this element
	 * @param c is the cursor to the list_view
	 * @return the index of the next departure time
	 */
	private int getNextTimePosition(ArrayList<Itinerary> list2) {
		int count = 0;
		if (list2 != null) {
			count = list2.size();
		}
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
				sasaTime = list2.get(i).getTime();
				sasaTimeNext = list2.get(i + 1).getTime();

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
	
	
	
}
