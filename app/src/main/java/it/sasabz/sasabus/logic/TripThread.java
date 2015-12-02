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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature.Properties;
import it.sasabz.sasabus.opendata.client.logic.BusTripCalculator;
import it.sasabz.sasabus.opendata.client.model.BusDayType;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartVariant;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class TripThread implements Runnable
{

   int              busLineId;
   String           busLineName;
   String           yyyyMMdd;
   int              seconds;
   int				tripId;
   int              busId;
   SasaApplication  mApplication;
   Feature			feature;
   BusDepartureItem busDepartureItem;
   Runnable         postExecute;
   int              dayType;

   public TripThread(int busLineId,
                     String busLineName,
                     int tripId,
                     int busId,
                     String yyyyMMdd,
                     int seconds,
                     SasaApplication application,
                     Feature currentFeature)
   {
      super();
      this.tripId = tripId;
      this.busLineId = busLineId;
      this.busLineName = busLineName;
      this.yyyyMMdd = yyyyMMdd;
      this.seconds = seconds;
      this.feature = currentFeature;
      this.mApplication = application;
      this.busId = busId;
   }

   @Override
   public void run()
   {
      try
      {

         AndroidOpenDataLocalStorage openDataStorage = mApplication.getOpenDataStorage();

         HashMap<String, Void> uniqueLineVariants = new HashMap<String, Void>();

         BusLineVariantTrip busLineVariantTrip = this.findBusTrip(uniqueLineVariants);

         BusTripBusStopTime[] stopTimes = BusTripCalculator.calculateBusStopTimes(busLineVariantTrip.busLineId,
                 busLineVariantTrip.variant.getVariantId(),
                 busLineVariantTrip.busTripStartTime,
                 openDataStorage);

         String destinationBusStationName = getBusStationNameUsingAppLanguage(openDataStorage.getBusStations().findBusStop(stopTimes[stopTimes.length - 1].getBusStop()).getBusStation());

         Properties properties = feature.getProperties();

         int delayStopFoundIndex = 9999;
         int delaySecondsRoundedToMin = 0;

         boolean isRealtime = false;

         int departure_index = 9999;

         long daySecondsFromMidnight = SASAbusTimeUtils.getDaySeconds();

         boolean gpsTimeGood = Math.abs(properties.getGpsDate().getTime() - new Date().getTime()) < 120000;

         if (properties != null)
         {
            for (int i = 0; i < stopTimes.length - 1; i++)
            {
               BusTripBusStopTime stop = stopTimes[i];
               if (gpsTimeGood && stop.getSeconds() > daySecondsFromMidnight - properties.getDelay() - 120 && stop.getBusStop() == properties.getNextStopNumber()
                       || !gpsTimeGood && stop.getSeconds() > daySecondsFromMidnight - properties.getDelay())
               {
                  departure_index = i;
                  delayStopFoundIndex = i;
                  delaySecondsRoundedToMin = convertDelayToMin(properties.getDelay()) * 60;
                  isRealtime = true;
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

         BusTripBusStopTime stop = stopTimes[0];

         String lineName = mApplication.getOpenDataStorage().getBusLines().findBusLine(busLineVariantTrip.busLineId).getShortName();

         busDepartureItem = new BusDepartureItem(formatSeconds(stop.getSeconds()), lineName,
                 destinationBusStationName,
                 stopTimes,
                 departure_index,
                 departure_index,
                 delaySecondsRoundedToMin / 60,
                 delayStopFoundIndex,
                 isRealtime);

         postExecute.run();




      }
      catch (Exception exxx)
      {
         exxx.printStackTrace();
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

   BusLineVariantTrip findBusTrip(HashMap<String, Void> uniqueLineVariants)
           throws IOException
   {
      BusDayType calendarDay = mApplication.getOpenDataStorage().getBusDayTypeList().findBusDayTypeByDay(this.yyyyMMdd);
      if (calendarDay == null)
      {
         // This day isn't in the calendar!
         return null;
      }
      dayType = calendarDay.getDayTypeId();

      BusTripStartVariant[] variants = mApplication.getOpenDataStorage().getBusTripStarts(busLineId,
              dayType);
      for (BusTripStartVariant busTripStartVariant : variants)
      {            ArrayList<Integer> ints = new ArrayList<>();

         BusTripStartTime[] times = busTripStartVariant.getTriplist();
         for (BusTripStartTime busTripStartTime : times)
         {
            ints.add(busTripStartTime.getId());
            if (busTripStartTime.getId() == tripId)
            {
               uniqueLineVariants.put(String.valueOf(busLineId)
                               + ":"
                               + String.valueOf(busTripStartVariant.getVariantId()),
                       null);
               BusLineVariantTrip busLineVariantTrip = new BusLineVariantTrip();
               busLineVariantTrip.busLineId = busLineId;
               busLineVariantTrip.variant = busTripStartVariant;
               busLineVariantTrip.busTripStartTime = busTripStartTime;
               return busLineVariantTrip;
            }
         }
         Collections.sort(ints);
      }
      return null;
   }

   public static void sortDeparturesByTime(ArrayList<BusDepartureItem> departures)
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

   public String getBusStationNameUsingAppLanguage(BusStation busStation) {
      if (mApplication.getApplicationContext().getString(R.string.bus_station_name_language).equals("de")) {
         return busStation.findName_de();
      } else {
         return busStation.findName_it();
      }
   }

   public BusDepartureItem getBusDepartureItem(){
      return busDepartureItem;
   }

   public void setPostExecute(Runnable postExecute) {
      this.postExecute = postExecute;
   }

   public int getDayType(){
      return dayType;
   }
}
