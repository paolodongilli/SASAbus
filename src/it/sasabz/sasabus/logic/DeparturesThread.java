/*
 * SASAbus - Android app for SASA bus open data
 *
 * DeparturesThread.java
 *
 * Created: May 9, 2014 02:05:00 PM
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

import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.data.realtime.PositionsResponse;
import it.sasabz.sasabus.data.realtime.Properties;
import it.sasabz.sasabus.opendata.client.logic.BusTripCalculator;
import it.sasabz.sasabus.opendata.client.model.BusDayType;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartVariant;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesDepartureAdapter;
import it.sasabz.sasabus.ui.busschedules.SyncDelay;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import android.widget.ListView;

public class DeparturesThread implements Runnable
{
   private static final int BACK_TIME = 60 * 60 * 2;

   Integer[]                busLines;
   String                   yyyyMMdd;
   int                      seconds;
   BusStation               optionalBusStation;
   MainActivity             mainActivity;
   ListView                 listView;

   public DeparturesThread(Integer[] busLines,
                           String yyyyMMdd,
                           int seconds,
                           BusStation optionalBusStation,
                           MainActivity mainActivity,
                           ListView listView)
   {
      super();
      this.busLines = busLines;
      this.yyyyMMdd = yyyyMMdd;
      this.seconds = seconds;
      this.optionalBusStation = optionalBusStation;
      this.mainActivity = mainActivity;
      this.listView = listView;
   }

   @Override
   public void run()
   {
      try
      {
         ArrayList<BusDepartureItem> departures = new ArrayList<BusDepartureItem>();

         AndroidOpenDataLocalStorage openDataStorage = this.mainActivity.getOpenDataStorage();

         HashMap<String, Void> uniqueLineVariants = new HashMap<String, Void>();
         ArrayList<BusLineVariantTrip> busLineVariantTrips = new ArrayList<BusLineVariantTrip>();

         this.findAllBusTrips(uniqueLineVariants, busLineVariantTrips);

         PositionsResponse delayResponse = new SyncDelay().delay(uniqueLineVariants.keySet().toArray(new String[0]));

         for (BusLineVariantTrip busLineVariantTrip : busLineVariantTrips)
         {
            BusTripBusStopTime[] stopTimes = BusTripCalculator.calculateBusStopTimes(busLineVariantTrip.busLineId,
                                                                                     busLineVariantTrip.variant.getVariantId(),
                                                                                     busLineVariantTrip.busTripStartTime,
                                                                                     openDataStorage);

            String destinationBusStationName = this.mainActivity.getBusStationNameUsingAppLanguage(openDataStorage.getBusStations().findBusStop(stopTimes[stopTimes.length - 1].getBusStop()).getBusStation());

            Properties delayProperties = delayResponse.findPropertiesBy_frt_fid(busLineVariantTrip.busTripStartTime.getId());

            /*
            delayProperties = new Properties();
            delayProperties.setDelay_sec(120);
            if (delayProperties != null)
            {
               delayProperties.setOrt_nr(stopTimes[1].getBusStop());
            }

            //delayProperties = null;
             */

            String delayText = "#";
            int delayStopFoundIndex = 9999;
            int delaySecondsRoundedToMin = 0;

            int departure_index = 9999;

            long daySecondsFromMidnight = SASAbusTimeUtils.getDaySeconds();

            if (delayProperties != null)
            {
               for (int i = 0; i < stopTimes.length - 1; i++)
               {
                  BusTripBusStopTime stop = stopTimes[i];
                  if (stop.getSeconds() > daySecondsFromMidnight - delayProperties.getDelay_sec())
                  {
                     delayProperties.setOrt_nr(stopTimes[i].getBusStop());
                     departure_index = i;
                     // if (i > 0)
                     // {
                     //         i--;
                     // }
                     delayStopFoundIndex = i;
                     delaySecondsRoundedToMin = convertDelayToMin(delayProperties.getDelay_sec()) * 60;
                     delayText = String.valueOf(convertDelayToMin(delayProperties.getDelay_sec())) + "'";
                     break;
                  }
               }
            }
            else
            {
               for (int i = 0; i < stopTimes.length - 1; i++)
               {
                  BusTripBusStopTime stop = stopTimes[i];
                  if (stop.getSeconds() > daySecondsFromMidnight)
                  {
                     departure_index = i;
                     break;
                  }
               }
            }

            for (int i = 0; i < stopTimes.length - 1; i++)
            {
               BusTripBusStopTime stop = stopTimes[i];

               if (this.optionalBusStation != null
                   && busStationContainsStop(this.optionalBusStation, stop.getBusStop())
                   && stop.getSeconds() + (i >= delayStopFoundIndex ? delaySecondsRoundedToMin : 0) >= this.seconds
                   || this.optionalBusStation == null
                   && delayProperties != null
                   && delayProperties.getOrt_nr() == stop.getBusStop()
                   || this.optionalBusStation == null
                   && delayProperties == null
                   && stop.getSeconds() >= this.seconds)
               {
                  String busStationName = this.mainActivity.getBusStationNameUsingAppLanguage(this.mainActivity.getOpenDataStorage().getBusStations().findBusStop(stop.getBusStop()).getBusStation());
                  String lineName = this.mainActivity.getOpenDataStorage().getBusLines().findBusLine(busLineVariantTrip.busLineId).getShortName();
                  String text = this.optionalBusStation == null ? busStationName : lineName;
                  BusDepartureItem item = new BusDepartureItem(formatSeconds(stop.getSeconds()),
                                                               text
                                                                     + " [ "
                                                                     + busLineVariantTrip.busLineId
                                                                     + ":"
                                                                     + busLineVariantTrip.variant.getVariantId()
                                                                     + ", "
                                                                     + busLineVariantTrip.busTripStartTime.getId()
                                                                     + "]",
                                                               destinationBusStationName,
                                                               stopTimes,
                                                               i,
                                                               departure_index,
                                                               delayText,
                                                               delayStopFoundIndex);

                  departures.add(item);
                  break;
               }
            }

         }

         sortDeparturesByTime(departures);

         final BusSchedulesDepartureAdapter departuresAdapter = new BusSchedulesDepartureAdapter(this.mainActivity,
                                                                                                 departures);

         this.listView.post(new Runnable()
         {

            @Override
            public void run()
            {
               DeparturesThread.this.listView.setAdapter(departuresAdapter);
            }
         });

      }
      catch (Exception exxx)
      {
         this.mainActivity.handleApplicationException(exxx);
      }
   }

   static int convertDelayToMin(int delaySeconds)
   {
      if (delaySeconds < 0)
      {
         delaySeconds -= 59;
      }
      int delayMinute = delaySeconds / 60;
      return delayMinute;
   }

   static boolean busStationContainsStop(BusStation busStation, int stopId)
   {
      for (BusStop busStop : busStation.getBusStops())
      {
         if (busStop.getORT_NR() == stopId)
         {
            return true;
         }
      }
      return false;
   }

   void findAllBusTrips(HashMap<String, Void> uniqueLineVariants, ArrayList<BusLineVariantTrip> busLineVariantTrips)
                                                                                                                    throws IOException
   {
      BusDayType calendarDay = this.mainActivity.getOpenDataStorage().getBusDayTypeList().findBusDayTypeByDay(this.yyyyMMdd);
      if (calendarDay == null)
      {
         // This day isn't in the calendar!
         return;
      }
      int dayType = calendarDay.getDayTypeId();

      for (int busLineId : this.busLines)
      {
         BusTripStartVariant[] variants = this.mainActivity.getOpenDataStorage().getBusTripStarts(busLineId,
                                                                                                  dayType);
         for (BusTripStartVariant busTripStartVariant : variants)
         {
            BusTripStartTime[] times = busTripStartVariant.getTriplist();
            for (BusTripStartTime busTripStartTime : times)
            {
               if (busTripStartTime.getSeconds() > this.seconds - BACK_TIME)
               {
                  uniqueLineVariants.put(String.valueOf(busLineId)
                                               + ":"
                                               + String.valueOf(busTripStartVariant.getVariantId()),
                                         null);
                  BusLineVariantTrip busLineVariantTrip = new BusLineVariantTrip();
                  busLineVariantTrip.busLineId = busLineId;
                  busLineVariantTrip.variant = busTripStartVariant;
                  busLineVariantTrip.busTripStartTime = busTripStartTime;
                  busLineVariantTrips.add(busLineVariantTrip);
               }
            }
         }
      }
   }

   static void sortDeparturesByTime(ArrayList<BusDepartureItem> departures)
   {
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
   }

   public static String formatSeconds(long seconds)
   {
      long sec = seconds % 60;
      long min = seconds / 60 % 60;
      long hour = seconds / 3600;
      return "" + twoDigits(hour) + ":" + twoDigits(min);
   }

   public static String twoDigits(long num)
   {
      String ret = "00" + num;
      ret = ret.substring(ret.length() - 2);
      return ret;
   }
}
