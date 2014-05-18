/*
 * SASAbus - Android app for SASA bus open data
 *
 * CustomDialog.java
 *
 * Created: Jan 3, 2014 11:29:26 AM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.sasabz.sasabus.logic;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BusStationArrayAdapter extends ArrayAdapter<BusTripBusStopTime>
{
	private final MainActivity context;
	private final BusDepartureItem item;

	public BusStationArrayAdapter(MainActivity context, BusDepartureItem item)
	{
		super(context, R.layout.trip_detail_row);
		this.context = context;
		this.item = item;
	}

	@Override
	public View getView(int position, View conView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View superView = inflater.inflate(R.layout.trip_detail_row, parent,
				false);

		ImageView imageView = (ImageView) superView
				.findViewById(R.id.image_route);

		BusTripBusStopTime element = this.getItem(position);

		/*
		 * The conditions to find out what image do we need for displaying
		 */
		if (position == this.item.getDeparture_index())
		{
			imageView.setImageResource(R.drawable.middle_bus);
		}
		else if (position == 0)
		{
			imageView.setImageResource(R.drawable.ab_punkt);
		}
		else if (position == this.getCount() - 1)
		{
			imageView.setImageResource(R.drawable.an_punkt);
		}
		else
		{
			imageView.setImageResource(R.drawable.middle_punkt);
		}

		TextView txt_time = (TextView) superView.findViewById(R.id.txt_time);
		txt_time.setText(DeparturesThread.formatSeconds(element.getSeconds()));

		TextView txt_busstopname = (TextView) superView
				.findViewById(R.id.txt_busstopname);
		String busStationName = "";
		try
		{
			busStationName = this.context
					.getBusStationNameUsingAppLanguage(this.context
							.getOpenDataStorage().getBusStations()
							.findBusStop(element.getBusStop()).getBusStation());
		}
		catch (Exception exxooo)
		{
			System.out.println("Do nothing");
		}
		txt_busstopname.setText(busStationName);

		/*
		 * Set Colors of the various busstops in the list
		 */

		if (position < this.item.getDeparture_index())
		{
			txt_busstopname.setTextColor(Color.GRAY);
			// txt_delay.setTextColor(Color.GRAY);
			txt_time.setTextColor(Color.GRAY);
		}
		if (position == this.item.getSelectedIndex())
		{
			superView.setBackgroundColor(Color.LTGRAY);
		}
		else
		{
			superView.setBackgroundColor(Color.WHITE);
		}

		return superView;
	}
}
