/*
 * SASAbus - Android app for SASA bus open data
 *
 * SearchInputField.java
 *
 * Created: Feb 13, 2014 15:29:00 AM
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

package it.sasabz.sasabus.ui.searchinputfield;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.map.MapView;
import it.sasabz.sasabus.ui.map.OnSelectBusStation;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.LinearLayout.LayoutParams;

public class SelectFromMap extends Dialog
{
   Location         location;
   Thread           pregps;
   LocationListener locationListener;

   public SelectFromMap(final BusStationAdvancedInputText autocompletetextviewInputfield2,
                        final MainActivity mainActivity) throws InterruptedException
   {
      super(mainActivity);
      this.setTitle(R.string.SelectFromMap_title);

      final LocationManager locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);

      this.pregps = new Thread()
      {
         @Override
         public void run()
         {
            if (SelectFromMap.this.locationListener != null)
            {
               try
               {
                  Thread.sleep(1500);
               }
               catch (InterruptedException e)
               {
                  // Nothing to do!
               }
               locationManager.removeUpdates(SelectFromMap.this.locationListener);
            }
            mainActivity.runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  double lat = 46.5624;
                  double lon = 11.27;
                  int zoom = 10;

                  if (SelectFromMap.this.location != null)
                  {
                     lat = SelectFromMap.this.location.getLatitude();
                     lon = SelectFromMap.this.location.getLongitude();
                     zoom = 15;
                  }

                  final MapView mapView = new MapView(mainActivity,
                                                      lat,
                                                      lon,
                                                      zoom,
                                                      mainActivity.getString(R.string.SelectFromMap_select_stop),
                                                      new OnSelectBusStation()
                                                      {

                                                         @Override
                                                         public void onSelected(final String busStationName)
                                                         {
                                                            mainActivity.runOnUiThread(new Runnable()
                                                            {
                                                               @Override
                                                               public void run()
                                                               {
                                                                  try
                                                                  {
                                                                     autocompletetextviewInputfield2.setInputTextFireChange(busStationName);
                                                                     SelectFromMap.this.dismiss();
                                                                  }
                                                                  catch (Exception exxx)
                                                                  {
                                                                     exxx.printStackTrace();
                                                                     mainActivity.handleApplicationException(exxx);
                                                                  }
                                                               }
                                                            });

                                                         }
                                                      });

                  SelectFromMap.this.setContentView(mapView.getWebView());
                  SelectFromMap.this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

                  mapView.start();

                  SelectFromMap.this.setOnDismissListener(new OnDismissListener()
                  {
                     @Override
                     public void onDismiss(DialogInterface arg0)
                     {
                        mapView.stop();
                     }
                  });
               }
            });
         };
      };
      if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
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
               SelectFromMap.this.location = location;
               SelectFromMap.this.pregps.interrupt();
            }
         };
         locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                100,
                                                0,
                                                this.locationListener);
      }
      this.pregps.start();

   }
}
