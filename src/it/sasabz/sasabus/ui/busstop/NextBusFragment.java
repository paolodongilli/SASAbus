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
import it.sasabz.sasabus.data.realtime.PositionsResponse;
import it.sasabz.sasabus.opendata.client.logic.BusTripCalculator;
import it.sasabz.sasabus.opendata.client.model.BusDayType;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartVariant;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusScheduleDetailsFragment;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesDepartureAdapter;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesFragment;
import it.sasabz.sasabus.ui.busschedules.SyncDelay;
import it.sasabz.sasabus.ui.routing.DateButton;
import it.sasabz.sasabus.ui.routing.DatePicker;
import it.sasabz.sasabus.ui.routing.TimeButton;
import it.sasabz.sasabus.ui.searchinputfield.BusStationAdvancedInputText;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {

      this.mainActivity = (MainActivity) this.getActivity();

      View view = inflater.inflate(R.layout.fragment_next_bus, container, false);

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
            fragmentToShow.setData(busDepartureItem.getIndex(),
                                   busDepartureItem.getStopTimes(),
                                   busDepartureItem.getBusStopOrLineName());
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

         SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

         final String day = yyyyMMdd.format(DatePicker.simpleDateFormat.parse(this.currentDate.getText().toString()));

         InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
         inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(),
                                              InputMethodManager.HIDE_NOT_ALWAYS);

         final long startCalc = System.currentTimeMillis();

         ArrayAdapter<String> loadingAdapter = new ArrayAdapter<String>(this.getActivity(),
                                                                        android.R.layout.simple_list_item_1);
         loadingAdapter.add(this.getString(R.string.NextBusFragment_searching));
         this.listviewNextBuses.setAdapter(loadingAdapter);

         new Thread(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {

                  SyncDelay syncDelay = new SyncDelay();

                  long count = 0;
                  long countLoadTimeTables = 0;

                  BusDayType calendarDay = NextBusFragment.this.mainActivity.getOpenDataStorage().getBusDayTypeList().findBusDayTypeByDay(day);
                  if (calendarDay == null)
                  {
                     // This day isn't in the calendar!
                     return;
                  }

                  ArrayList<BusDepartureItem> departures = new ArrayList<BusDepartureItem>();

                  final int dayType = calendarDay.getDayTypeId();
                  String[] hh_mm = NextBusFragment.this.currentTime.getText().toString().split(":");
                  int seconds = (Integer.parseInt(hh_mm[0]) * 60 + Integer.parseInt(hh_mm[1])) * 60;

                  for (int busLineId : NextBusFragment.this.busStation.getBusLines())
                  {

                     long startTimeTables = System.currentTimeMillis();
                     BusTripStartVariant[] variants = NextBusFragment.this.mainActivity.getOpenDataStorage().getBusTripStarts(busLineId,
                                                                                                                              dayType);
                     long stopTimeTables = System.currentTimeMillis();
                     countLoadTimeTables += stopTimeTables - startTimeTables;
                     for (BusTripStartVariant busTripStartVariant : variants)
                     {

                        PositionsResponse delayResponse = syncDelay.delay(busLineId,
                                                                          busTripStartVariant.getVariantId());

                        BusTripStartTime[] times = busTripStartVariant.getTriplist();
                        for (BusTripStartTime busTripStartTime : times)
                        {
                           if (busTripStartTime.getSeconds() > seconds - 60 * 60 * 2 /*&& busTripStartTime.getSeconds() <= seconds + 60 * 60 * 30*/)
                           {
                              BusTripBusStopTime[] stopTimes = BusTripCalculator.calculateBusStopTimes(busLineId,
                                                                                                       busTripStartVariant.getVariantId(),
                                                                                                       busTripStartTime,
                                                                                                       NextBusFragment.this.mainActivity.getOpenDataStorage());

                              String destinationBusStationName = NextBusFragment.this.mainActivity.getBusStationNameUsingAppLanguage(NextBusFragment.this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(stopTimes[stopTimes.length - 1].getBusStop()).getBusStation());

                              loopbusstops: for (BusStop busStop : NextBusFragment.this.busStation.getBusStops())
                              {
                                 for (int i = 0; i < stopTimes.length - 1 /* last stop isn't show because we show only departures */; i++)
                                 {

                                    BusTripBusStopTime stopTime = stopTimes[i];

                                    count++;

                                    if (stopTime.getBusStop() == busStop.getORT_NR()
                                        && stopTime.getSeconds() >= seconds)
                                    {
                                       BusDepartureItem item = new BusDepartureItem(BusSchedulesFragment.formatSeconds(stopTime.getSeconds()),
                                                                                    NextBusFragment.this.mainActivity.getOpenDataStorage().getBusLines().findBusLine(busLineId).getShortName()
                                                                                          + " [ "
                                                                                          + busLineId
                                                                                          + ":"
                                                                                          + busTripStartVariant.getVariantId()
                                                                                          + ", "
                                                                                          + busTripStartTime.getId()
                                                                                          + "]",
                                                                                    destinationBusStationName,
                                                                                    stopTimes,
                                                                                    i);

                                       departures.add(item);
                                       break loopbusstops;
                                    }
                                 }
                              }

                           }
                        }
                     }

                  }

                  Collections.sort(departures, new Comparator<BusDepartureItem>()
                  {
                     @Override
                     public int compare(BusDepartureItem i1, BusDepartureItem i2)
                     {
                        int diff = i1.getTime().compareTo(i2.getTime());
                        if (diff == 0)
                        {
                           i1.getBusStopOrLineName().compareTo(i2.getBusStopOrLineName());
                        }
                        if (diff == 0)
                        {
                           i1.getDestinationName().compareTo(i2.getDestinationName());
                        }
                        return diff;
                     }
                  });
                  final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(NextBusFragment.this.getSherlockActivity(),
                                                                                                          departures);
                  final long parsing = countLoadTimeTables;
                  NextBusFragment.this.listviewNextBuses.post(new Runnable()
                  {

                     @Override
                     public void run()
                     {

                        NextBusFragment.this.listviewNextBuses.setAdapter(departuresAdapter);
                        long stopCalc = System.currentTimeMillis();

                     }
                  });

               }
               catch (Exception ioxxx)
               {
                  NextBusFragment.this.mainActivity.handleApplicationException(ioxxx);
               }

            }

         }).start();
      }
   }

}
