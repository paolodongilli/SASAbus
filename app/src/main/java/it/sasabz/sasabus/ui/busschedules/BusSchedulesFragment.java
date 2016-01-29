/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusSchedulesFragment.java
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
import it.sasabz.sasabus.logic.DeparturesThread;
import it.sasabz.sasabus.opendata.client.model.BusLine;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.routing.DateButton;
import it.sasabz.sasabus.ui.routing.DatePicker;
import it.sasabz.sasabus.ui.routing.TimeButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

public class BusSchedulesFragment extends SherlockFragment
{

   private static final String    BZ                     = "BZ";
   private static final String    ME                     = "ME";
   private static final String    OTHER                  = "OTHER";

   private Spinner                spinnerArea;
   private Spinner                spinnerBusLine;
   private ListView               listviewBuslineDepartures;

   MainActivity                   mainActivity;

   Button                         currentDate;
   Button                         currentTime;

   BusLine                        lastBusLine;

   String                         currentArea;
   

   static BusLineGroupException[] busLineGroupExceptions = new BusLineGroupException[] { new BusLineGroupException(201,
                                                                                                                   new String[] { OTHER }),
         new BusLineGroupException(248, new String[] { OTHER }),
         new BusLineGroupException(300, new String[] { OTHER }),
         new BusLineGroupException(227, new String[] { ME }),
         new BusLineGroupException(5000, new String[] { OTHER }), };

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      try
      {
         this.mainActivity = (MainActivity) this.getActivity();

         final View view = inflater.inflate(R.layout.fragment_bus_schedules, container, false);

         this.currentDate = (Button) view.findViewById(R.id.currentDate);
         DateButton.init(this.currentDate);

         this.currentTime = (Button) view.findViewById(R.id.currentTime);
         TimeButton.init(this.currentTime);

         this.listviewBuslineDepartures = (ListView) view.findViewById(R.id.listview_busline_departures);

         Button recalculateDepartures = (Button) view.findViewById(R.id.recalculateDepartures);
         recalculateDepartures.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View v)
            {
               try
               {
                  BusSchedulesFragment.this.addAdapterToListView(BusSchedulesFragment.this.lastBusLine);
               }
               catch (Exception e)
               {
                  BusSchedulesFragment.this.mainActivity.handleApplicationException(e);
                  e.printStackTrace();
               }
            }
         });

         //Spinners
         this.addEntriesToSpinnerArea(view);

         this.addEntriesToSpinnerBusline(view);

         //ListView
         this.addOnItemSelectedListenerToListView();
         
         SasaApplication application = (SasaApplication) this.getActivity().getApplication();
         application.getTracker().track("BusSchedules");
         
         if(((SasaApplication) getActivity().getApplication()).getSharedPreferenceManager().hasCurrentTrip()){
             CurentTrip curentTrip = ((SasaApplication) getActivity().getApplication()).
            		 getSharedPreferenceManager().getCurrentTrip();
        	 String beaconLine = curentTrip.getBeaconInfo().getBusDepartureItem().getBusStopOrLineName();
        	 int spinnerPosition = ((ArrayAdapter<String>) spinnerBusLine.getAdapter()).getPosition(beaconLine);
             spinnerBusLine.setSelection(spinnerPosition);
             
             BusScheduleDetailsFragment fragmentToShow = (BusScheduleDetailsFragment) SherlockFragment.instantiate(BusSchedulesFragment.this.getActivity(),
                     BusScheduleDetailsFragment.class.getName());
             fragmentToShow.setData(beaconLine, curentTrip, (SasaApplication) getActivity().getApplication());
             FragmentManager fragmentManager = BusSchedulesFragment.this.getActivity().getSupportFragmentManager();
             fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(null).commit();
         }

         return view;
      }
      catch (Exception ioxxx)
      {
         this.mainActivity.handleApplicationException(ioxxx);
         throw new RuntimeException(ioxxx);
      }
   }

   private void addEntriesToSpinnerArea(final View view)
   {
      this.spinnerArea = (Spinner) view.findViewById(R.id.spinner_area);
      ArrayList<String> areas = new ArrayList<String>();
      areas.add(this.mainActivity.getString(R.string.BusSchedulesFragment_area_all));
      areas.add(this.mainActivity.getString(R.string.BusSchedulesFragment_area_bz));
      areas.add(this.mainActivity.getString(R.string.BusSchedulesFragment_area_me));
      areas.add(this.mainActivity.getString(R.string.BusSchedulesFragment_area_other));

      ArrayAdapter<String> adapterArea = new ArrayAdapter<String>(this.getSherlockActivity(),
                                                                  android.R.layout.simple_spinner_item,
                                                                  areas);
      adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      this.spinnerArea.setAdapter(adapterArea);

      this.spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
         {
            int index = arg2;
            int preIndex = 0;
            if(currentArea == BZ)
            	preIndex = 1;
            else if(currentArea == ME)
            	preIndex = 2;
            else if(currentArea == OTHER)
            	preIndex = 3;
            if(preIndex != index){
	            switch (index)
	            {
	               case 0:
	                  BusSchedulesFragment.this.currentArea = null;
	               break;
	               case 1:
	                  BusSchedulesFragment.this.currentArea = BZ;
	               break;
	               case 2:
	                  BusSchedulesFragment.this.currentArea = ME;
	               break;
	               case 3:
	                  BusSchedulesFragment.this.currentArea = OTHER;
	               break;
	            }
	            try
	            {
	               BusSchedulesFragment.this.addEntriesToSpinnerBusline(view);
	            }
	            catch (IOException e)
	            {
	               BusSchedulesFragment.this.mainActivity.handleApplicationException(e);
	            }
	         }
         }

         @Override
         public void onNothingSelected(AdapterView<?> arg0)
         {

         }
      });


   }

   private boolean isBusLineInArea(BusLine busLine, String area)
   {
      if (area == null)
      {
         return true;
      }
      for (BusLineGroupException exception : busLineGroupExceptions)
      {
         if (exception.LI_NR == busLine.getLI_NR())
         {
            for (String exceptionArea : exception.areas)
            {
               if (exceptionArea.equals(area))
               {
                  return true;
               }
            }
            return false;
         }
      }
      boolean isAreaInName = busLine.getShortName().endsWith(" " + area);
      return isAreaInName;
   }

   private void addEntriesToSpinnerBusline(View view) throws IOException
   {
      this.spinnerBusLine = (Spinner) view.findViewById(R.id.spinner_busline);

      ArrayList<String> busLinesNames = new ArrayList<String>();
      final ArrayList<Integer> busLineIds = new ArrayList<Integer>();
      busLinesNames.add(this.mainActivity.getString(R.string.BusSchedulesFragment_select_line));
      busLineIds.add(null);
      final BusLine[] busLines = this.mainActivity.getOpenDataStorage().getBusLines().getList();
      for (BusLine busLine : busLines)
      {
         int lineId = busLine.getLI_NR();
         if (this.isBusLineInArea(busLine, this.currentArea))
         {
            busLinesNames.add(busLine.getShortName()/* + " (" + lineId + ")"*/);
            busLineIds.add(lineId);
         }
      }

      ArrayAdapter<String> adapterBusLines = new ArrayAdapter<String>(this.getSherlockActivity(),
                                                                      android.R.layout.simple_spinner_item,
                                                                      busLinesNames);
      adapterBusLines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      this.spinnerBusLine.setAdapter(adapterBusLines);
      this.spinnerBusLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
         {
            try
            {
               int index = arg2;
               BusLine busLine = null;
               if (index > 0)
               {
                  Integer lineid = busLineIds.get(index);
                  busLine = BusSchedulesFragment.this.mainActivity.getOpenDataStorage().getBusLines().findBusLine(lineid);
               }

               BusSchedulesFragment.this.addAdapterToListView(busLine);
            }
            catch (Exception e)
            {
               BusSchedulesFragment.this.mainActivity.handleApplicationException(e);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> arg0)
         {

         }
      });

   }

   private void addAdapterToListView(final BusLine busLine) throws IOException, ParseException
   {
      this.lastBusLine = busLine;

      if (busLine != null)
      {

         ArrayAdapter<String> loadingAdapter = new ArrayAdapter<String>(this.getActivity(),
                                                                        android.R.layout.simple_list_item_1);
         loadingAdapter.add(this.mainActivity.getString(R.string.BusSchedulesFragment_searching));
         this.listviewBuslineDepartures.setAdapter(loadingAdapter);

         SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

         final String day = yyyyMMdd.format(DatePicker.simpleDateFormat.parse(this.currentDate.getText().toString()));
         String[] hh_mm = BusSchedulesFragment.this.currentTime.getText().toString().split(":");
         int seconds = (Integer.parseInt(hh_mm[0]) * 60 + Integer.parseInt(hh_mm[1])) * 60;

         new Thread(new DeparturesThread(new Integer[] { busLine.getLI_NR() },
                                         day,
                                         seconds,
                                         null,
                                         this.mainActivity,
                                         this.listviewBuslineDepartures)).start();
         /*
         new Thread(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {

                  SyncDelay syncDelay = new SyncDelay();

                  ArrayList<BusDepartureItem> departures = new ArrayList<BusDepartureItem>();

                  BusDayType calendarDay = BusSchedulesFragment.this.mainActivity.getOpenDataStorage().getBusDayTypeList().findBusDayTypeByDay(day);
                  if (calendarDay == null)
                  {
                     // This day isn't in the calendar!
                     return;
                  }
                  int dayType = calendarDay.getDayTypeId();
                  String[] hh_mm = BusSchedulesFragment.this.currentTime.getText().toString().split(":");
                  int seconds = (Integer.parseInt(hh_mm[0]) * 60 + Integer.parseInt(hh_mm[1])) * 60;

                  BusTripStartVariant[] variants = BusSchedulesFragment.this.mainActivity.getOpenDataStorage().getBusTripStarts(busLine.getLI_NR(),
                                                                                                                                dayType);

                  int searchStartTime = 60 * 60 * 2;

                  HashMap<String, Void> uniqueLineVariants = new HashMap<String, Void>();

                  ForEachBusTrip.visit(new int[] { busLine.getLI_NR() },
                                       dayType,
                                       seconds,
                                       BusSchedulesFragment.this.mainActivity.getOpenDataStorage(),
                                       null);

                  for (BusTripStartVariant busTripStartVariant : variants)
                  {

                     BusTripStartTime[] times = busTripStartVariant.getTriplist();
                     for (BusTripStartTime busTripStartTime : times)
                     {
                        if (busTripStartTime.getSeconds() > seconds - searchStartTime /* && busTripStartTime.getSeconds() <= seconds* /)
                        {
                           uniqueLineVariants.put(String.valueOf(busLine.getLI_NR())
                                                        + ":"
                                                        + String.valueOf(busTripStartVariant.getVariantId()),
                                                  null);
                        }
                     }
                  }

                  PositionsResponse delayResponse = syncDelay.delay(uniqueLineVariants.keySet().toArray(new String[0]));

                  for (BusTripStartVariant busTripStartVariant : variants)
                  {

                     BusTripStartTime[] times = busTripStartVariant.getTriplist();
                     for (BusTripStartTime busTripStartTime : times)
                     {
                        if (busTripStartTime.getSeconds() > seconds - searchStartTime /* && busTripStartTime.getSeconds() <= seconds* /)
                        {
                           Properties delayProperties = delayResponse.findPropertiesBy_frt_fid(busTripStartTime.getId());

                           BusTripBusStopTime[] stopTimes = BusTripCalculator.calculateBusStopTimes(busLine.getLI_NR(),
                                                                                                    busTripStartVariant.getVariantId(),
                                                                                                    busTripStartTime,
                                                                                                    BusSchedulesFragment.this.mainActivity.getOpenDataStorage());

                           String destinationBusStationName = BusSchedulesFragment.this.mainActivity.getBusStationNameUsingAppLanguage(BusSchedulesFragment.this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(stopTimes[stopTimes.length - 1].getBusStop()).getBusStation());

                           if (delayProperties != null) // I have realtime data
                           {
                              int delaySeconds = delayProperties.getDelay_sec();
                              if (delaySeconds < 0)
                              {
                                 delaySeconds -= 59;
                              }
                              int delayMinute = delaySeconds / 60;
                              int lastBusStopId = delayProperties.getOrt_nr();
                              boolean found = false;
                              for (int i = 0; i < stopTimes.length; i++)
                              {
                                 BusTripBusStopTime stopTimeLast = stopTimes[i];

                                 if (stopTimeLast.getBusStop() == lastBusStopId)
                                 {
                                    System.out.println("=== " + i + " < " + stopTimes.length);
                                    if (i < stopTimes.length - 2)
                                    {
                                       i = i + 1;
                                       BusTripBusStopTime stopTime = stopTimes[i];

                                       String busStationName = BusSchedulesFragment.this.mainActivity.getBusStationNameUsingAppLanguage(BusSchedulesFragment.this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(stopTime.getBusStop()).getBusStation());
                                       BusDepartureItem item = new BusDepartureItem(formatSeconds(stopTime.getSeconds()),
                                                                                    busStationName
                                                                                          + " [ "
                                                                                          + busLine.getLI_NR()
                                                                                          + ":"
                                                                                          + busTripStartVariant.getVariantId()
                                                                                          + ", "
                                                                                          + busTripStartTime.getId()
                                                                                          + "]",
                                                                                    destinationBusStationName,
                                                                                    stopTimes,
                                                                                    i);

                                       if (delayMinute == 0)
                                       {
                                          item.delay = "ok";
                                       }
                                       else
                                       {
                                          item.delay = "" + delayMinute + "'";
                                       }
                                       departures.add(item);

                                    }

                                    found = true;
                                    break;
                                 }
                              }
                              if (!found)
                              {
                                 throw new IllegalStateException("Realtime bus stop not found in the timetable");
                              }

                           }
                           else
                           // I don't have realtime data, use timetables
                           {
                              for (int i = 0; i < stopTimes.length - 1 /* last stop isn't show because we show only departures * /; i++)
                              {
                                 BusTripBusStopTime stopTime = stopTimes[i];
                                 if (stopTime.getSeconds() >= seconds)
                                 {
                                    String busStationName = BusSchedulesFragment.this.mainActivity.getBusStationNameUsingAppLanguage(BusSchedulesFragment.this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(stopTime.getBusStop()).getBusStation());
                                    BusDepartureItem item = new BusDepartureItem(formatSeconds(stopTime.getSeconds()),
                                                                                 busStationName
                                                                                       + " [ "
                                                                                       + busLine.getLI_NR()
                                                                                       + ":"
                                                                                       + busTripStartVariant.getVariantId()
                                                                                       + ", "
                                                                                       + busTripStartTime.getId()
                                                                                       + "]",
                                                                                 destinationBusStationName,
                                                                                 stopTimes,
                                                                                 i);

                                    departures.add(item);
                                    break;
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

                  final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(BusSchedulesFragment.this.getSherlockActivity(),
                                                                                                          departures);

                  BusSchedulesFragment.this.listviewBuslineDepartures.post(new Runnable()
                  {

                     @Override
                     public void run()
                     {
                        BusSchedulesFragment.this.listviewBuslineDepartures.setAdapter(departuresAdapter);

                        long stopCalc = System.currentTimeMillis();

                     }
                  });

               }
               catch (Exception ioxxx)
               {
                  BusSchedulesFragment.this.mainActivity.handleApplicationException(ioxxx);
               }
            }
         }).start();
         */

      }

   }

   private void addOnItemSelectedListenerToListView()
   {
      this.listviewBuslineDepartures.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {

            BusDepartureItem busDepartureItem = (BusDepartureItem) BusSchedulesFragment.this.listviewBuslineDepartures.getAdapter().getItem(position);

            BusScheduleDetailsFragment fragmentToShow = (BusScheduleDetailsFragment) SherlockFragment.instantiate(BusSchedulesFragment.this.getActivity(),
                                                                                                                  BusScheduleDetailsFragment.class.getName());
            fragmentToShow.setData(BusSchedulesFragment.this.lastBusLine.getShortName(), busDepartureItem);
            FragmentManager fragmentManager = BusSchedulesFragment.this.getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(null).commit();

         }
      });
   }

}
