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
import it.sasabz.sasabus.logic.DeparturesThread;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusScheduleDetailsFragment;
import it.sasabz.sasabus.ui.routing.DateButton;
import it.sasabz.sasabus.ui.routing.DatePicker;
import it.sasabz.sasabus.ui.routing.TimeButton;
import it.sasabz.sasabus.ui.searchinputfield.BusStationAdvancedInputText;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

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
   
   LinearLayout                searchLines;
   
   Hashtable<Integer, Boolean> lines;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {

      this.mainActivity = (MainActivity) this.getActivity();

      View view = inflater.inflate(R.layout.fragment_next_bus, container, false);

      Button selectAll = (Button)view.findViewById(R.id.selectAllLines);
      Button deselectAll = (Button)view.findViewById(R.id.deSelectAllLines);
      
      this.searchLines = (LinearLayout) view.findViewById(R.id.search_lines);
      
      Calendar now = Calendar.getInstance();

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
                  NextBusFragment.this.calculateDepartures();
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
				Enumeration<Integer> e = lines.keys();
				while(e.hasMoreElements())
					lines.put(e.nextElement(), true);
				for(int i = 0; i < searchLines.getChildCount(); i++)
					((CheckBox)searchLines.getChildAt(i)).setChecked(true);
				try {
					calculateDepartures(NextBusFragment.this.busStation.getBusLines());
				} catch (ParseException exxx) {
					mainActivity.handleApplicationException(exxx);
				}
			}
		});

         deselectAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Enumeration<Integer> e = lines.keys();
				while(e.hasMoreElements())
					lines.put(e.nextElement(), false);
				for(int i = 0; i < searchLines.getChildCount(); i++)
					((CheckBox)searchLines.getChildAt(i)).setChecked(false);
				try{
					calculateDepartures(new Integer[0]);
				} catch (ParseException exxx) {
					mainActivity.handleApplicationException(exxx);
				}
			}
		});
         
         return view;
      }
      catch (Exception ioxxx)
      {
         this.mainActivity.handleApplicationException(ioxxx);
         throw new RuntimeException(ioxxx);
      }
   }

   public void setInitialBusStationName(String name)
   {
      this.initialBusStationName = name;
   }

   void calculateDepartures() throws IOException, ParseException
   {
      this.busStation = this.searchInputField.getSelectedBusStation();

      if (this.busStation != null)
      {

         calculateDepartures(NextBusFragment.this.busStation.getBusLines());
         searchLines.removeAllViews();
         this.lines = new Hashtable<Integer, Boolean>();
         
         for(int i: NextBusFragment.this.busStation.getBusLines()){
             this.lines.put(i, true);
        	 CheckBox line = new CheckBox(getActivity());
        	 line.setChecked(true);
             String lineName = this.mainActivity.getOpenDataStorage().getBusLines().findBusLine(i).getShortName();
        	 line.setText(lineName);
        	 searchLines.addView(line);
        	 final int number = i;
        	 line.setOnClickListener(new OnClickListener() {
				int line = number;
				@Override
				public void onClick(View v) {
					NextBusFragment.this.lines.put(number, ((CheckBox)v).isChecked());
					int length = 0;
					Log.d("Hashtable", NextBusFragment.this.lines.toString());
					for(int i = 0; i < NextBusFragment.this.busStation.getBusLines().length && NextBusFragment.this.lines != null; i++)
						if(NextBusFragment.this.lines.get(NextBusFragment.this.busStation.getBusLines()[i]))
							length++;
					{
						Integer[] lines = new Integer[length];
						int j = 0;
						for(int i: NextBusFragment.this.busStation.getBusLines())
							if(NextBusFragment.this.lines.get(i))
								lines[j++] = i;
						try {
							calculateDepartures(lines);
						} catch (ParseException exxx) {
							NextBusFragment.this.mainActivity.handleApplicationException(exxx);
						}
					}
				}
			});
         }
      }
   }

   void calculateDepartures(Integer[] lines) throws ParseException{
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
       new Thread(new DeparturesThread(lines,
                                       day,
                                       seconds,
                                       this.busStation,
                                       this.mainActivity,
                                       this.listviewNextBuses)).start();

   }
   
}
