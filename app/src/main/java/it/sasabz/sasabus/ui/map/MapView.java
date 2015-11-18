/*
 * SASAbus - Android app for SASA bus open data
 *
 * MapView.java
 *
 * Created: Jan 3, 2014 11:29:26 AM
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

import it.sasabz.sasabus.ui.MainActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MapView
{
   WebView               webView;
   JavaScript2JavaBridge bridge;
   MainActivity          mainActivity;
   LocationListener      locationListener;

   public MapView(MainActivity mainActivity,
                  double initialLat,
                  double initialLon,
                  int initialZoom,
                  String selectMode,
                  OnSelectBusStation onSelectBusStation)
   {

      this.mainActivity = mainActivity;

      this.webView = new WebView(mainActivity);
      this.webView.setWebChromeClient(new WebChromeClient());
      //webView.setWebViewClient(new WebViewClient());

      // http://stackoverflow.com/questions/19379392/jquery-mobile-not-working-in-webview-when-loading-from-local-assets
      //webView.getSettings().setAllowFileAccessFromFileURLs(true);

      this.webView.getSettings().setJavaScriptEnabled(true);
      this.webView.getSettings().setDomStorageEnabled(true);
      //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

      this.bridge = new JavaScript2JavaBridge(mainActivity,
                                              initialLat,
                                              initialLon,
                                              initialZoom,
                                              selectMode,
                                              onSelectBusStation);

      this.bridge.setRequestLocationStatus("searching");

      this.webView.addJavascriptInterface(this.bridge,
                                          "it_sasabz_sasabus_webmap_client_GWTSASAbusOpenDataLocalStorage");

      this.webView.loadUrl("file:///android_asset/webmap/SASAbusWebMap.html");

   }

   public WebView getWebView()
   {
      return this.webView;
   }

   public void start()
   {
      this.locationListener = new LocationListener()
      {
         @Override
         public void onStatusChanged(String provider, int status, Bundle extras)
         {
         }

         @Override
         public void onProviderEnabled(String provider)
         {
         }

         @Override
         public void onProviderDisabled(String provider)
         {
         }

         @Override
         public void onLocationChanged(Location location)
         {
            MapView.this.bridge.setRequestLocationStatus(location.getLatitude() +
                                                         "," +
                                                         location.getLongitude() +
                                                         "," +
                                                         location.getAccuracy());
         }
      };
      this.mainActivity.getMainLocationManager().requestLocationUpdates(this.locationListener);
   }

   public void stop()
   {
      this.mainActivity.getMainLocationManager().removeUpdates(this.locationListener);
      this.bridge.setRequestLocationStatus("stop");
   }
}
