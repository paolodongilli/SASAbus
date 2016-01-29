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

import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconInfo;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
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

public class TripThread {

   BusBeaconInfo beaconInfo;
   SasaApplication mApplication;
   Feature feature;

   public TripThread(BusBeaconInfo beaconInfo,
                     SasaApplication application,
                     Feature currentFeature) {
      super();
      this.beaconInfo = beaconInfo;
      this.feature = currentFeature;
      this.mApplication = application;
   }

   static int convertDelayToMin(int delaySeconds) {
      if (delaySeconds < 0) {
         delaySeconds -= 59;
      }
      int delayMinute = delaySeconds / 60;
      return delayMinute;
   }

   BusLineVariantTrip findBusTrip(HashMap<String, Void> uniqueLineVariants)
           throws IOException {

      BusDayType calendarDay = mApplication.getOpenDataStorage().getBusDayTypeList().findBusDayTypeByDay(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
      if (calendarDay == null) {
         // This day isn't in the calendar!
         return null;
      }
      int dayType = calendarDay.getDayTypeId();

      BusTripStartVariant[] variants = mApplication.getOpenDataStorage().getBusTripStarts(beaconInfo.getLineId(),
              dayType);
      int tripId = beaconInfo.getTripId();
      for (BusTripStartVariant busTripStartVariant : variants) {
         ArrayList<Integer> ints = new ArrayList<>();

         BusTripStartTime[] times = busTripStartVariant.getTriplist();
         for (BusTripStartTime busTripStartTime : times) {
            ints.add(busTripStartTime.getId());
            if (busTripStartTime.getId() == tripId) {
               uniqueLineVariants.put(String.valueOf(beaconInfo.getLineId())
                               + ":"
                               + String.valueOf(busTripStartVariant.getVariantId()),
                       null);
               BusLineVariantTrip busLineVariantTrip = new BusLineVariantTrip();
               busLineVariantTrip.busLineId = beaconInfo.getLineId();
               busLineVariantTrip.variant = busTripStartVariant;
               busLineVariantTrip.busTripStartTime = busTripStartTime;
               return busLineVariantTrip;
            }
         }
         Collections.sort(ints);
      }
      return null;
   }

   public static String formatSeconds(long seconds) {
      long min = seconds / 60 % 60;
      long hour = seconds / 3600;
      return "" + twoDigits(hour) + ":" + twoDigits(min);
   }

   public static String twoDigits(long num) {
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

   public BusDepartureItem getBusDepartureItem() {
      try {

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

         int departure_index = stopTimes.length - 1;

         long daySecondsFromMidnight = SASAbusTimeUtils.getDaySeconds();

         BusTripBusStopTime stop = null;
         delaySecondsRoundedToMin = convertDelayToMin(properties.getDelay()) * 60;
         boolean gpsTimeGood = Math.abs(properties.getGpsDate().getTime() - new Date().getTime()) < 90000  &&
                 mApplication.isOnline() ||
                 Math.abs(properties.getGpsDate().getTime() - new Date().getTime()) < 30000;

         CurentTrip curentTrip = mApplication.getSharedPreferenceManager().getCurrentTrip();
         if (!gpsTimeGood && curentTrip != null && mApplication.getSharedPreferenceManager().getCurrentTrip().getBeaconInfo().getMajor() == beaconInfo.getMajor()) {
            properties = curentTrip.getVirtualFeature().getProperties();
            if (curentTrip.getVirtualFeature().getProperties().getGpsDate().getTime() > properties.getGpsDate().getTime()) {
               gpsTimeGood = Math.abs(properties.getGpsDate().getTime() - new Date().getTime()) < 60000;
            }
         }

         for (int i = 0; i < stopTimes.length - 1; i++) {
            stop = stopTimes[i];
            if (gpsTimeGood && stop.getSeconds() > daySecondsFromMidnight - properties.getDelay() - 120 && stop.getBusStop() == properties.getNextStopNumber()
                    || !gpsTimeGood && stop.getSeconds() > daySecondsFromMidnight - properties.getDelay()) {
               departure_index = i;
               delayStopFoundIndex = i;
               break;
            }
         }


         String lineName = mApplication.getOpenDataStorage().getBusLines().findBusLine(busLineVariantTrip.busLineId).getShortName();

         return new BusDepartureItem(formatSeconds(stop.getSeconds()), lineName,
                 destinationBusStationName,
                 stopTimes,
                 departure_index,
                 departure_index,
                 delaySecondsRoundedToMin / 60,
                 delayStopFoundIndex,
                 true);
      } catch (Exception exxx) {
         exxx.printStackTrace();
      }
      return null;
   }
}
