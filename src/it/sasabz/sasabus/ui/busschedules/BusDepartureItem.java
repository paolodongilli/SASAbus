/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusDepartureItem.java
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

import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;

public class BusDepartureItem
{
   private String       time;
   private String       busStopOrLineName;
   private String       destinationName;

   BusTripBusStopTime[] stopTimes;
   int                  selected_index;
   int                  departure_index;

   String               delay;
   int                  delay_index;

   public BusDepartureItem(String time,
                           String busStopOrLineName,
                           String destinationName,
                           BusTripBusStopTime[] stopTimes,
                           int selected_index,
                           int departure_index,
                           String delay,
                           int delay_index)
   {
      super();
      this.time = time;
      this.busStopOrLineName = busStopOrLineName;
      this.destinationName = destinationName;
      this.stopTimes = stopTimes;
      this.selected_index = selected_index;
      this.delay = delay;
      this.delay_index = delay_index;
      this.departure_index = departure_index;
   }

   public String getTime()
   {
      return this.time;
   }

   public String getBusStopOrLineName()
   {
      return this.busStopOrLineName;
   }

   public String getDestinationName()
   {
      return this.destinationName;
   }

   public BusTripBusStopTime[] getStopTimes()
   {
      return this.stopTimes;
   }

   public int getSelectedIndex()
   {
      return this.selected_index;
   }

   public int getDeparture_index()
   {
      return this.departure_index;
   }

}
