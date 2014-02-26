/*
 * SASAbus - Android app for SASA bus open data
 *
 * JavaScript2JavaBridge.java
 *
 * Created: Jan 4, 2014 5:12:54 PM
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

package it.sasabz.sasabus.ui.map;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.ui.MainActivity;

import java.io.IOException;

import android.webkit.JavascriptInterface;

public class JavaScript2JavaBridge
{
   MainActivity       mainActivity;

   private String     requestLocationStatus = "idle";

   double             initialLat;
   double             initialLon;
   int                initialZoom;

   String             selectButtonText;

   OnSelectBusStation onSelectBusStation;

   public JavaScript2JavaBridge(MainActivity mainActivity,
                                double initialLat,
                                double initialLon,
                                int initialZoom,
                                String selectButtonText,
                                OnSelectBusStation onSelectBusStation)
   {
      super();
      this.mainActivity = mainActivity;

      this.initialLat = initialLat;
      this.initialLon = initialLon;
      this.initialZoom = initialZoom;
      this.selectButtonText = selectButtonText;

      this.onSelectBusStation = onSelectBusStation;
   }

   @JavascriptInterface
   public String getData(String key) throws IOException
   {
      return this.mainActivity.getOpenDataStorage().getData(key);
   }

   @JavascriptInterface
   public String getMapTilesRootUrl()
   {
      String ret = "file://" +
                   this.mainActivity.getOpenDataStorage().getMapTilesRootFolder().getAbsolutePath();
      return ret;
   }

   @JavascriptInterface
   public void showDepartures(String busStationName)
   {
      this.onSelectBusStation.onSelected(busStationName);
   }

   @JavascriptInterface
   public String initialParameters()
   {
      return "" +
             this.initialLat +
             ", " +
             this.initialLon +
             ", " +
             this.initialZoom +
             ", " +
             this.selectButtonText +
             ", " +
             this.mainActivity.getString(R.string.bus_station_name_language);
   }

   @JavascriptInterface
   public synchronized String getRequestLocationStatus()
   {
      return this.requestLocationStatus;
   }

   public synchronized void setRequestLocationStatus(String requestLocationStatus)
   {
      this.requestLocationStatus = requestLocationStatus;
   }

}
