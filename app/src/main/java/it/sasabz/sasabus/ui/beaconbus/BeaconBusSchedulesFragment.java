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

package it.sasabz.sasabus.ui.beaconbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusScheduleDetailsFragment;

/**
 * Fragment for the Tab with the next Bus
 */
public class BeaconBusSchedulesFragment extends SherlockFragment
{

   private ListView            listviewNextBuses;

   MainActivity                mainActivity;

    BroadcastReceiver          beaconBusstopReceiver = null;


   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       this.mainActivity = (MainActivity) this.getActivity();

       View view = inflater.inflate(R.layout.fragment_beacon_buses, container, false);

       this.listviewNextBuses = (ListView) view.findViewById(R.id.listview_next_buses);
       this.listviewNextBuses.setOnItemClickListener(new OnItemClickListener()
       {
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
           {
               BusDepartureItem busDepartureItem = (BusDepartureItem) BeaconBusSchedulesFragment.this.listviewNextBuses.getAdapter().getItem(position);

               BusScheduleDetailsFragment fragmentToShow = (BusScheduleDetailsFragment) SherlockFragment.instantiate(BeaconBusSchedulesFragment.this.getActivity(),
                       BusScheduleDetailsFragment.class.getName());
               fragmentToShow.setData(busDepartureItem.getBusStopOrLineName(), busDepartureItem);
               FragmentManager fragmentManager = BeaconBusSchedulesFragment.this.getActivity().getSupportFragmentManager();
               fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(null).commit();
           }
       });

       this.listviewNextBuses.setOnItemClickListener(new OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
               BusDepartureItem busDepartureItem = (BusDepartureItem) BeaconBusSchedulesFragment.this.listviewNextBuses.getAdapter().getItem(position);

               BusScheduleDetailsFragment fragmentToShow = (BusScheduleDetailsFragment) SherlockFragment.instantiate(BeaconBusSchedulesFragment.this.getActivity(),
                       BusScheduleDetailsFragment.class.getName());
               fragmentToShow.setData(busDepartureItem.getBusStopOrLineName(), busDepartureItem);
               FragmentManager fragmentManager = BeaconBusSchedulesFragment.this.getActivity().getSupportFragmentManager();
               fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(null).commit();
           }
       });

       this.beaconBusstopReceiver = new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {

               boolean post = BeaconBusSchedulesFragment.this.listviewNextBuses.post(new Runnable() {

                   @Override
                   public void run() {
                       BeaconBusSchedulesFragment.this.listviewNextBuses.setAdapter(BusBeaconHandler
                               .getDepartureAdapter((SasaApplication)getActivity().getApplication()));
                   }
               });
           }
       };

       return view;
   }
  
   @Override
   public void onActivityCreated(Bundle savedInstanceState){
	   super.onActivityCreated(savedInstanceState);
   }
   
   @Override
   public void onResume(){
	   super.onResume();
       beaconBusstopReceiver.onReceive(getActivity(),null);
       getActivity().registerReceiver(beaconBusstopReceiver, new IntentFilter(BusDepartureItem.class.getName()));
   }
   
   @Override
   public void onPause(){
	   super.onStop();
	   getActivity().unregisterReceiver(beaconBusstopReceiver);
   }
   
}