/*
 * SASAbus - Android app for SASA bus open data
 *
 * MainLocationManager.java
 *
 * Created: Feb 25, 2014 11:00:00 AM
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

package it.sasabz.sasabus.ui;

import java.util.ArrayList;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainLocationManager
{

   LocationManager             locationManager;

   ArrayList<LocationListener> listeners = new ArrayList<LocationListener>();

   boolean                     active;

   public MainLocationManager(Context context)
   {
      this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      this.active = false;
   }

   public synchronized void pause()
   {
      this.active = false;
      for (LocationListener locationListener : this.listeners)
      {
         this.locationManager.removeUpdates(locationListener);
      }
   }

   public synchronized void resume()
   {
      this.active = true;
      for (LocationListener locationListener : this.listeners)
      {
         this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
      }
   }

   public synchronized void requestLocationUpdates(LocationListener locationListener)
   {
      this.listeners.add(locationListener);
      if (this.active)
      {
         this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
      }
   }

   public synchronized void removeUpdates(LocationListener locationListener)
   {
      if (this.active)
      {
         this.locationManager.removeUpdates(locationListener);
      }

      for (int i = 0; i < this.listeners.size(); i++)
      {
         if (this.listeners.get(i) == locationListener)
         {
            this.listeners.remove(i);
            return;
         }
      }
      throw new IllegalStateException("locationListener not found!");
   }
}
