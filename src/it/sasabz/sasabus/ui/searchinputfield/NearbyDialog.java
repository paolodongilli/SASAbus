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
import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStationList;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import bz.davide.dmweb.client.leaflet.DistanceCalculator;

public class NearbyDialog extends Dialog
{

   MainActivity   mainActivity;
   final TextView textViewSearching;

   public NearbyDialog(final MainActivity mainActivity, final BusStationAdvancedInputText searchInputField)
   {
      super(mainActivity);
      this.mainActivity = mainActivity;
      this.setTitle(mainActivity.getString(R.string.NearbyDialog_title));
      this.textViewSearching = (TextView) LayoutInflater.from(mainActivity).inflate(R.layout.nearbydialog_searching_textview,
                                                                                    null);

      this.textViewSearching.setText(mainActivity.getString(R.string.NearbyDialog_searching));
      this.setContentView(this.textViewSearching);
      this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

      final LocationListener locationListener = new LocationListener()
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
            mainActivity.getMainLocationManager().removeUpdates(this);
            NearbyDialog.this.setOnCancelListener(null);
            try
            {
               NearbyDialog.this.locationReceived(location,
                                                  mainActivity.getOpenDataStorage(),
                                                  NearbyDialog.this,
                                                  searchInputField);
            }
            catch (IOException e)
            {
               mainActivity.handleApplicationException(e);
            }
         }

      };
      mainActivity.getMainLocationManager().requestLocationUpdates(locationListener);

      this.setOnCancelListener(new OnCancelListener()
      {
         @Override
         public void onCancel(DialogInterface dialog)
         {
            mainActivity.getMainLocationManager().removeUpdates(locationListener);
         }
      });

   }

   private void locationReceived(Location location,
                                 AndroidOpenDataLocalStorage openDataLocalStorage,
                                 NearbyDialog dialog,
                                 final BusStationAdvancedInputText autocompletetextviewInputfield2) throws IOException
   {
      BusStationList busStationList = openDataLocalStorage.getBusStations();

      BusStation[] busStations = busStationList.getList();
      DistanceBusStation[] distanceBusStations = new DistanceBusStation[busStations.length];

      for (int i = 0; i < busStations.length; i++)
      {
         BusStation busStation = busStations[i];

         DistanceBusStation distanceBusStation = new DistanceBusStation();
         distanceBusStation.busStation = busStation;

         BusStop busStop;
         double d;
         for (int k = 0; k < busStation.getBusStops().length; k++)
         {
            busStop = busStation.getBusStops()[k];
            d = DistanceCalculator.distanceMeter(location.getLatitude(),
                                                 location.getLongitude(),
                                                 busStop.getLat(),
                                                 busStop.getLon());
            if (k == 0 || d < distanceBusStation.distance)
            {
               distanceBusStation.distance = d;
            }
         }
         distanceBusStations[i] = distanceBusStation;
      }

      Arrays.sort(distanceBusStations, new Comparator<DistanceBusStation>()
      {
         @Override
         public int compare(DistanceBusStation d1, DistanceBusStation d2)
         {
            int diff = d1.distance.compareTo(d2.distance);
            if (diff == 0)
            {
               diff = d1.busStation.getORT_NAME().compareTo(d2.busStation.getORT_NAME());
            }
            return diff;
         }
      });

      ArrayList<DistanceBusStation> within1km = new ArrayList<DistanceBusStation>();
      for (DistanceBusStation distanceBusStation : distanceBusStations)
      {

         within1km.add(distanceBusStation);
         if (distanceBusStation.distance.doubleValue() > 1000D)
         {
            break;
         }
      }

      final DistanceBusStationAdapter data = new DistanceBusStationAdapter(this.mainActivity, within1km);
      ListView listView = new ListView(this.getContext());
      listView.setAdapter(data);
      dialog.setContentView(listView);

      listView.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
         {
            autocompletetextviewInputfield2.setInputTextFireChange(NearbyDialog.this.mainActivity.getBusStationNameUsingAppLanguage(data.getItem(index).busStation));
            NearbyDialog.this.dismiss();
         }
      });

   }
}
