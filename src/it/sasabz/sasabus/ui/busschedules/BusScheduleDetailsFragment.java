/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusScheduleDetailsFragment.java
 *
 * Created: Jan 27, 2014 10:55:00 AM
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

package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.BusStationArrayAdapter;
import it.sasabz.sasabus.ui.MainActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class BusScheduleDetailsFragment extends SherlockFragment
{

	String busLineShortName;
	BusDepartureItem item;

	public void setData(String busLineShortName, BusDepartureItem item)
	{
		this.busLineShortName = busLineShortName;
		this.item = item;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		ListView listview_line_course;

		MainActivity mainActivity = (MainActivity) this.getActivity();

		View ret = inflater.inflate(R.layout.fragment_busline_details,
				container, false);
		listview_line_course = (ListView) ret
				.findViewById(R.id.listview_line_course);

		BusStationArrayAdapter stops = new BusStationArrayAdapter(mainActivity,
				this.item);

		for (int i = 0; i < this.item.getStopTimes().length; ++i)
		{
			stops.add(this.item.getStopTimes()[i]);
		}

		listview_line_course.setAdapter(stops);
		int pos = this.item.getSelectedIndex();
		if (pos > 0)
		{
			pos--;
		}
		listview_line_course.setSelection(pos);
		TextView busLineNameView = (TextView) ret
				.findViewById(R.id.textview_busline_number);
		busLineNameView.setText(this.busLineShortName);

		TextView busStopNameView = (TextView) ret
				.findViewById(R.id.textview_busstop_name);
		busStopNameView.setText(""); // not in use

		/*
		 * Setting Delay better visible for the users!!!!!!
		 */
		TextView txt_delay = (TextView) ret.findViewById(R.id.txt_delay);
		if (this.item.isRealtime())
		{

			int delay = this.item.getDelayNumber();
			String delaytext = "";
			if (delay == 0)
			{
				delaytext = "font colo='green'>"
						+ mainActivity.getResources().getString(
								R.string.in_time) + "</font>";
			}
			else if (delay < 0)
			{
				String formatdelay = "";
				if (delay < -2)
				{
					formatdelay = "<font color='cyan'>" + this.item.getDelay()
							+ "</font>";
				}
				else
				{
					formatdelay = "<font color='green'>" + this.item.getDelay()
							+ "</font>";
				}
				delaytext = formatdelay
						+ " "
						+ mainActivity.getResources().getString(
								R.string.advance);
			}
			else
			{
				String formatdelay = "";
				if (delay < 2)
				{
					formatdelay = "<font color='green'>" + this.item.getDelay()
							+ "</font>";
				}
				else if (delay < 4)
				{
					formatdelay = "<font color='orange'>"
							+ this.item.getDelay() + "</font>";
				}
				else
				{
					formatdelay = "<font color='red'>" + this.item.getDelay()
							+ "</font>";
				}
				delaytext = formatdelay + " "
						+ mainActivity.getResources().getString(R.string.delay);

			}

			txt_delay.setText(Html.fromHtml(delaytext));
		}
		else
		{
			txt_delay.setText(R.string.no_realtime);
		}
		return ret;
	}
}
