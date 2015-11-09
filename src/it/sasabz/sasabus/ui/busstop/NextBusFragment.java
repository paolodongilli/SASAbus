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

package it.sasabz.sasabus.ui.busstop;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Fragment for the Tab with the next Bus
 */
public class NextBusFragment extends SherlockFragment
{

   private ListView            listviewNextBuses;

   Button                      currentDate;
   Button                      currentTime;
   MainActivity                mainActivity;

   BusStationAdvancedInputText searchInputField;

   BusStation[]                busStations;

   BusStation                  busStation;

   String                      initialBusStationName = "";
   
   LinearLayout                searchLines, selectStations;
   
   Hashtable<String, Boolean>  lines;
   
   ArrayList<BusDepartureItem> departures;
   
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {

      this.mainActivity = (MainActivity) this.getActivity();

      View view = inflater.inflate(R.layout.fragment_next_bus, container, false);

      Button selectAll = (Button)view.findViewById(R.id.selectAllLines);
      Button deselectAll = (Button)view.findViewById(R.id.deSelectAllLines);
      
      this.searchLines = (LinearLayout) view.findViewById(R.id.search_lines);
      
      this.selectStations = (LinearLayout) view.findViewById(R.id.selectStations);
      
      Calendar now = Calendar.getInstance();
      
      if (this.initialBusStationName == null || this.initialBusStationName.equals("")) {
    	  this.showBeaconBusStop();
      }
      
      this.currentDate = (Button) view.findViewById(R.id.currentDate);
      DateButton.init(this.currentDate);

      this.currentTime = (Button) view.findViewById(R.id.currentTime);
      TimeButton.init(this.currentTime);

      this.listviewNextBuses = (ListView) view.findViewById(R.id.listview_next_buses);

      this.listviewNextBuses.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
         {
            BusDepartureItem busDepartureItem = (BusDepartureItem) NextBusFragment.this.listviewNextBuses.getAdapter().getItem(position);

            BusScheduleDetailsFragment fragmentToShow = (BusScheduleDetailsFragment) SherlockFragment.instantiate(NextBusFragment.this.getActivity(),
                                                                                                                  BusScheduleDetailsFragment.class.getName());
            fragmentToShow.setData(busDepartureItem.getBusStopOrLineName(), busDepartureItem);
            FragmentManager fragmentManager = NextBusFragment.this.getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(null).commit();
         }
      });

      this.searchInputField = (BusStationAdvancedInputText) view.findViewById(R.id.searchInputField);

      try
      {
         this.busStations = this.mainActivity.getOpenDataStorage().getBusStations().getList();

         this.searchInputField.setBusStations(this.busStations);

         this.searchInputField.setOnChangeListener(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  NextBusFragment.this.newStationFound();
               }
               catch (Exception e)
               {
                  NextBusFragment.this.mainActivity.handleApplicationException(e);
                  e.printStackTrace();
               }

            }
         });

         Button recalculate = (Button) view.findViewById(R.id.recalculateDepartures);
         recalculate.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View arg0)
            {

               try
               {
                  NextBusFragment.this.calculateDepartures();
               }
               catch (Exception e)
               {
                  NextBusFragment.this.mainActivity.handleApplicationException(e);
                  e.printStackTrace();
               }

            }
         });

         this.searchInputField.setInputTextFireChange(this.initialBusStationName);

         selectAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Enumeration<String> e = lines.keys();
				while(e.hasMoreElements())
					lines.put(e.nextElement(), true);

					final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(
							NextBusFragment.this.mainActivity,
							NextBusFragment.this.departures);

					for(int i = 0; i < searchLines.getChildCount(); i++)
						((CheckBox)searchLines.getChildAt(i)).setChecked(true);
					
					NextBusFragment.this.listviewNextBuses.post(new Runnable()
					{
					
						@Override
						public void run()
						{
							NextBusFragment.this.listviewNextBuses.setAdapter(departuresAdapter);
						}
					});

			}
		});

         deselectAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Enumeration<String> e = lines.keys();
				while(e.hasMoreElements())
					lines.put(e.nextElement(), false);					
				final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(
						NextBusFragment.this.mainActivity,
						new ArrayList<BusDepartureItem>());

				for(int i = 0; i < searchLines.getChildCount(); i++)
					((CheckBox)searchLines.getChildAt(i)).setChecked(false);
				
				NextBusFragment.this.listviewNextBuses.post(new Runnable()
				{
					
					@Override
					public void run()
					{
						NextBusFragment.this.listviewNextBuses.setAdapter(departuresAdapter);
					}
				});
			}
		});
         
         SasaApplication application = (SasaApplication) this.getActivity().getApplication();
         application.getTracker().track("NextBus");
         
         return view;
      }
      catch (Exception ioxxx)
      {
         this.mainActivity.handleApplicationException(ioxxx);
         throw new RuntimeException(ioxxx);
      }
   }
   
   private void showBeaconBusStop(){
	   try {
		   SasaApplication application = (SasaApplication) this.getActivity().getApplication();
		   if (application.getSharedPreferenceManager().isBusStopDetectionEnabled()) {
		       Integer busStopId = application.getSharedPreferenceManager().getCurrentBusStop();
			   if (busStopId != null) {
				   String busStationName = this.mainActivity.getBusStationNameUsingAppLanguage(this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(busStopId).getBusStation());
				   this.setInitialBusStationName(busStationName);
			   }
		   }
	   } catch (IOException e) {}
   }

   public void setInitialBusStationName(String name)
   {
      this.initialBusStationName = name;
   }

   void newStationFound() throws IOException, ParseException
   {
      this.busStation = this.searchInputField.getSelectedBusStation();

      if (this.busStation != null)
      {
    	 selectStations.setVisibility(View.VISIBLE);
         calculateDepartures();
         searchLines.removeAllViews();
         this.departures = null;
         this.lines = new Hashtable<String, Boolean>();
         
         for(Integer i: NextBusFragment.this.busStation.getBusLines()){
        	 final String name = this.mainActivity.getOpenDataStorage().getBusLines().findBusLine(i).getShortName();
             this.lines.put(name, true);
        	 CheckBox line = new CheckBox(getActivity());
        	 line.setChecked(true);
             String lineName = this.mainActivity.getOpenDataStorage().getBusLines().findBusLine(i).getShortName();
        	 line.setText(lineName);
        	 searchLines.addView(line);
        	 line.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					NextBusFragment.this.lines.put(name, ((CheckBox)v).isChecked());
					ArrayList<BusDepartureItem> newDeparture = new ArrayList<BusDepartureItem>();
					for(BusDepartureItem item: NextBusFragment.this.departures){
						if(NextBusFragment.this.lines.get(item.getBusStopOrLineName()))
							newDeparture.add(item);
					}         
					final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(
							NextBusFragment.this.mainActivity,
                            newDeparture);

					NextBusFragment.this.listviewNextBuses.post(new Runnable()
					{
					
						@Override
						public void run()
						{
							NextBusFragment.this.listviewNextBuses.setAdapter(departuresAdapter);
						}
					});
				}
			});
         }
      }
   }

   void calculateDepartures() throws ParseException{
	   SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

       final String day = yyyyMMdd.format(DatePicker.simpleDateFormat.parse(this.currentDate.getText().toString()));
       String[] hh_mm = NextBusFragment.this.currentTime.getText().toString().split(":");
       int seconds = (Integer.parseInt(hh_mm[0]) * 60 + Integer.parseInt(hh_mm[1])) * 60;

       InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
       inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(),
                                            InputMethodManager.HIDE_NOT_ALWAYS);

       ArrayAdapter<String> loadingAdapter = new ArrayAdapter<String>(this.getActivity(),
                                                                      android.R.layout.simple_list_item_1);
       loadingAdapter.add(this.getString(R.string.NextBusFragment_searching));
       this.listviewNextBuses.setAdapter(loadingAdapter);
       final DeparturesThread departuresThread = new DeparturesThread(NextBusFragment.this.busStation.getBusLines(),
               day,
               seconds,
               this.busStation,
               this.mainActivity,
               this.listviewNextBuses);
       new Thread(){
    	   public void run(){
    		   Thread thread = new Thread(departuresThread);
				try {
					thread.start();
					thread.join();
					NextBusFragment.this.departures = departuresThread.getDepartures();ArrayList<BusDepartureItem> newDeparture = new ArrayList<BusDepartureItem>();
					for(BusDepartureItem item: NextBusFragment.this.departures){
						if(NextBusFragment.this.lines.get(item.getBusStopOrLineName()))
							newDeparture.add(item);
					}         
					final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(
							NextBusFragment.this.mainActivity,
                            newDeparture);

					NextBusFragment.this.listviewNextBuses.post(new Runnable()
					{
					
						@Override
						public void run()
						{
							NextBusFragment.this.listviewNextBuses.setAdapter(departuresAdapter);
						}
					});

				} catch (InterruptedException exxx) {
					exxx.printStackTrace();
					NextBusFragment.this.mainActivity.handleApplicationException(exxx);
				}
    	   }
       }.start();

   }
   
}
