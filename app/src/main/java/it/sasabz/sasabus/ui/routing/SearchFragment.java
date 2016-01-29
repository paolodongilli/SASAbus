/*
 * SASAbus - Android app for SASA bus open data
 *
 * SearchFragment.java
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

package it.sasabz.sasabus.ui.routing;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.searchinputfield.BusStationAdvancedInputText;
import it.sasabz.sasabus.ui.trips.MyTripsFragment;

import java.io.IOException;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;

public class SearchFragment extends SherlockFragment
{

   BusStationAdvancedInputText    autocompletetextviewDeparture;

   BusStationAdvancedInputText    autoCompleteTextViewArrival;

   private ImageButton imagebuttonSwitch;

   Button              dateButton;
   Button              timeButton;
   Button              buttonSearch;

   MainActivity        mainActivity;

   View                showTrips;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {

      this.mainActivity = (MainActivity) this.getActivity();
      final View view = inflater.inflate(R.layout.fragment_search, container, false);
      this.setHasOptionsMenu(true);      
      
      try
      {

         this.autocompletetextviewDeparture = (BusStationAdvancedInputText) view.findViewById(R.id.autocompletetextview_departure);
         this.autoCompleteTextViewArrival = (BusStationAdvancedInputText) view.findViewById(R.id.autocompletetextview_arrival);

         BusStation[] busStations = this.mainActivity.getOpenDataStorage().getBusStations().getList();
         this.autocompletetextviewDeparture.setBusStations(busStations);
         this.autoCompleteTextViewArrival.setBusStations(busStations);

      }
      catch (IOException ioxxx)
      {
         this.mainActivity.handleApplicationException(ioxxx);
      }
      
      if (this.autocompletetextviewDeparture.getSelectedBusStation() == null) {
    	  this.showBeaconBusStop();
      }
      
      this.buttonSearch = (Button) view.findViewById(R.id.button_search);
      this.buttonSearch.setOnClickListener(new SearchButtonClick(this));

      this.dateButton = (Button) view.findViewById(R.id.button_date);
      DateButton.init(this.dateButton);

      this.timeButton = (Button) view.findViewById(R.id.button_time);
      TimeButton.init(this.timeButton);

      this.imagebuttonSwitch = (ImageButton) view.findViewById(R.id.imagebutton_switch);
      this.imagebuttonSwitch.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View arg0)
         {
            SearchFragment.this.autocompletetextviewDeparture.swapText(SearchFragment.this.autoCompleteTextViewArrival);
         }
      });

      this.showTrips = view.findViewById(R.id.button_my_trips);
      this.showTrips.setOnClickListener(new OnClickListener() {
         long lastClick = 0;
         int clickCount = 0;
         @Override
         public void onClick(View v) {
            if(System.currentTimeMillis() - lastClick > 1000)
               clickCount = 0;
            if(++clickCount == 7){
               SherlockFragment fragmentToShow = (SherlockFragment) SherlockFragment.instantiate(mainActivity,
                       MyTripsFragment.class.getName());
               FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
               fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(MainActivity.HOME_FRAGMENT)
                          .commit();

               mainActivity.getSupportActionBar().setTitle("My Trips");
               clickCount = 0;
            }
            lastClick = System.currentTimeMillis();
         }
      });

      return view;
   }

   private void showBeaconBusStop(){
	   try {
		   SasaApplication application = (SasaApplication) this.getActivity().getApplication();
		   if (application.getSharedPreferenceManager().isBusStopDetectionEnabled()) {
		       Integer busStopId = application.getSharedPreferenceManager().getCurrentBusStop();
			   if (busStopId != null) {
				   String busStationName = this.mainActivity.getBusStationNameUsingAppLanguage(this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(busStopId).getBusStation());
				   this.autocompletetextviewDeparture.setInputTextFireChange(busStationName);
			   }
		   }
	   } catch (IOException e) {}
   }
   
   @Override
   public void onPrepareOptionsMenu(Menu menu)
   {
      /*
      MainActivity parentActivity = (MainActivity) getSherlockActivity();
      boolean drawerIsOpen = parentActivity.isDrawerOpen();
      //If the drawer is closed, show the menu related to the content
      if (!drawerIsOpen) {
         menu.clear();
         parentActivity.getSupportMenuInflater().inflate(R.menu.search_route_fragment, menu);
      }
      */
      super.onPrepareOptionsMenu(menu);
   }

}