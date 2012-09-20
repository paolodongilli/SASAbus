/**
 * 
 *
 * MyListAdapter.java
 * 
 * Created: 17.12.2011 15:10:39
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
 * 
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
package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.SASAbus;
import it.sasabz.android.sasabus.hafas.XMLConnection;
import it.sasabz.android.sasabus.hafas.XMLConnectionRequest;
import it.sasabz.android.sasabus.hafas.XMLJourney;
import it.sasabz.android.sasabus.hafas.XMLWalk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class MyXMLConnectionAdapter extends BaseAdapter {
	private final Vector<XMLConnection> list;

	
	/**
	 * This constructor creates an object with the following parameters
	 * @param context is the context to work with
	 * @param whereId is the resource id where to place the string
	 * @param layoutId is the layout id of the list_view
	 * @param list is the list of dbobject's which are to putting in the list_view
	 */
	public MyXMLConnectionAdapter(Vector<XMLConnection> list) {
		this.list = list;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) 
		{
              LayoutInflater vi = (LayoutInflater)SASAbus.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              v = vi.inflate(R.layout.con_transfer_row, null);
		}
		if (list != null)
		{
			TextView departure = (TextView) v.findViewById(R.id.departure);
			TextView arrival = (TextView) v.findViewById(R.id.arrival);
			TextView departuretime = (TextView) v.findViewById(R.id.deptime);
			TextView arrivaltime = (TextView) v.findViewById(R.id.arrtime);
			TextView transfers = (TextView) v.findViewById(R.id.transfers);
			XMLConnection conreq = list.get(position);
			if(conreq != null)
			{
				SimpleDateFormat simple = new SimpleDateFormat("HH:mm");
				departure.setText(conreq.getDeparture().getStation().getHaltestelle());
				departuretime.setText(simple.format(conreq.getDeparture().getArrtime()));
				String transfertext = "";
				if(conreq instanceof XMLWalk)
				{
					ImageView image = (ImageView)v.findViewById(R.id.image);
					int random = (int)(Math.random() * 10) % 2;
					if(random == 1)
					{
						image.setImageResource(R.drawable.glyphicons_339_rabbit);
					}
					else
					{
						image.setImageResource(R.drawable.glyphicons_338_turtle);
					}
				}
				else if(conreq instanceof XMLJourney)
				{
					ImageView image = (ImageView)v.findViewById(R.id.image);
					
					image.setImageResource(R.drawable.glyphicons_031_bus);
					
					transfertext = SASAbus.getContext().getResources().getString(R.id.line);
					transfertext += (" " + ((XMLJourney)conreq).getAttribut("NUMBER"));
					
					
				}
				transfers.setText(simple.format(conreq.getDuration()) + " | " + transfertext);
				arrival.setText(conreq.getArrival().getStation().getHaltestelle());
				arrivaltime.setText(simple.format(conreq.getArrival().getArrtime()));
				
			}
		}
		
		return v;
	}

	@Override
	public int getCount() {
		if(list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list == null)
			return null;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (list == null)
			return -1;
		return position;
	}

}
