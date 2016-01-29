/*
 * SASAbus - Android app for SASA bus open data
 *
 * NextBusFragment.java
 *
 * Created: Jun 19, 2013 09:41:00 AM
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

package it.sasabz.sasabus.ui.trips;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.data.trips.FinishedTrip;
import it.sasabz.sasabus.data.trips.TripsSQLiteOpenHelper;
import it.sasabz.sasabus.logic.DeparturesThread;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusScheduleDetailsFragment;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesDepartureAdapter;
import it.sasabz.sasabus.ui.routing.DateButton;
import it.sasabz.sasabus.ui.routing.DatePicker;
import it.sasabz.sasabus.ui.routing.TimeButton;
import it.sasabz.sasabus.ui.searchinputfield.BusStationAdvancedInputText;

/**
 * Fragment for the Tab with the next Bus
 */
public class MyTripsFragment extends SherlockFragment
{

    private ListView            listView;

    MainActivity                mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        this.mainActivity = (MainActivity) this.getActivity();

        View ret = inflater.inflate(R.layout.fragment_my_trips,
                container, false);

        listView = (ListView) ret.findViewById(R.id.triplist);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mainActivity, android.R.layout.simple_list_item_1);
        List<FinishedTrip> finishedTrips = TripsSQLiteOpenHelper.getInstance(getActivity()).getFinishedTrips();
        if(finishedTrips.size() == 0) {
            adapter.add("No trip saved");
            listView.setAdapter(adapter);
        }else {
            FinishedTripsAdapter finishedTripsAdapter = new FinishedTripsAdapter(getActivity(), finishedTrips);
            listView.setAdapter(finishedTripsAdapter);
        }
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        View headerView = new View(getActivity());
        View footerView = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (5 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));
        headerView.setLayoutParams(params);
        footerView.setLayoutParams(params);
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);
        return ret;
    }

}