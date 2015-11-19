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
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.logic.BusStationArrayAdapter;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class BusScheduleDetailsFragment extends SherlockFragment {

	String busLineShortName;
	BusDepartureItem item;
	BroadcastReceiver itemUpdateBroadcastReceiver;
	ListView listview_line_course;

	public void setData(String busLineShortName, BusDepartureItem item) {
		this.busLineShortName = busLineShortName;
		this.item = item;
	}

	public void setData(String busLineShortName, CurentTrip curentTrip) {
		setData(busLineShortName, curentTrip.getBusDepartureItem());
		itemUpdateBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferenceManager mSharedPreferenceManager = ((SasaApplication) getActivity().getApplication())
						.getSharedPreferenceManager();
				if (mSharedPreferenceManager.hasCurrentTrip()) {
					BusScheduleDetailsFragment.this.item = mSharedPreferenceManager.getCurrentTrip()
							.getBusDepartureItem();
					setupView(getView());
				}
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View ret = inflater.inflate(R.layout.fragment_busline_details, container, false);
		listview_line_course = (ListView) ret.findViewById(R.id.listview_line_course);
		setupView(ret);
		if (itemUpdateBroadcastReceiver != null)
			getActivity().registerReceiver(itemUpdateBroadcastReceiver,
					new IntentFilter(BusDepartureItem.class.getName()));
		return ret;
	}

	public void setupView(View ret) {

		MainActivity mainActivity = (MainActivity) this.getActivity();

		BusStationArrayAdapter stops = new BusStationArrayAdapter(mainActivity, this.item);

		for (int i = 0; i < this.item.getStopTimes().length; ++i) {
			stops.add(this.item.getStopTimes()[i]);
		}
		listview_line_course.setAdapter(stops);
		int pos = this.item.getSelectedIndex();
		if (pos > 0) {
			pos--;
		}
		listview_line_course.setSelection(pos);
		TextView busLineNameView = (TextView) ret.findViewById(R.id.textview_busline_number);
		busLineNameView.setText(this.busLineShortName);

		TextView busStopNameView = (TextView) ret.findViewById(R.id.textview_busstop_name);
		busStopNameView.setText(""); // not in use

		/*
		 * Setting Delay better visible for the users!!!!!!
		 */
		TextView txt_delay_text = (TextView) ret.findViewById(R.id.txt_delay_text);
		TextView txt_delay = (TextView) ret.findViewById(R.id.txt_delay);

		if (this.item.isRealtime()) {
			txt_delay.setText(this.item.getDelay());

			int delay = this.item.getDelayNumber();
			if (delay == 0) {
				txt_delay.setTextColor(Color.GREEN);
				txt_delay_text.setText(R.string.in_time);
			} else if (delay < 0) {
				if (delay < -2) {
					txt_delay.setTextColor(Color.CYAN);
				} else {
					txt_delay.setTextColor(Color.GREEN);
				}
				txt_delay_text.setText(R.string.advance);
			} else {
				if (delay < 2) {
					txt_delay.setTextColor(Color.GREEN);
				} else if (delay < 4) {
					txt_delay.setTextColor(mainActivity.getResources().getColor(R.color.sasa_orange));
				} else {
					txt_delay.setTextColor(Color.RED);
				}
				txt_delay_text.setText(R.string.delay);

			}

		} else {
			txt_delay.setText("");
			txt_delay_text.setText(R.string.no_realtime);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (itemUpdateBroadcastReceiver != null)
			getActivity().unregisterReceiver(itemUpdateBroadcastReceiver);
	}
}
