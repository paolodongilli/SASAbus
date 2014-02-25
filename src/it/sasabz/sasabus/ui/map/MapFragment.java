/*
 * SASAbus - Android app for SASA bus open data
 *
 * MapFragment.java
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

package it.sasabz.sasabus.ui.map;

import it.sasabz.sasabus.R;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busstop.NextBusFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class MapFragment extends SherlockFragment
{
   MapView mapView;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      final MainActivity mainActivity = (MainActivity) this.getActivity();
      this.mapView = new MapView(mainActivity,
                                 46.5624,
                                 11.27,
                                 10,
                                 "Show departures",
                                 new OnSelectBusStation()
                                 {

                                    @Override
                                    public void onSelected(String busStationName)
                                    {
                                       NextBusFragment fragmentToShow = (NextBusFragment) SherlockFragment.instantiate(mainActivity,
                                                                                                                       NextBusFragment.class.getName());

                                       fragmentToShow.setInitialBusStationName(busStationName);

                                       FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                                       fragmentManager.beginTransaction().add(R.id.content_frame,
                                                                              fragmentToShow).addToBackStack(null).commit();
                                    }
                                 });
      this.mapView.start();
      return this.mapView.getWebView();
   }

   @Override
   public void onDestroy()
   {
      super.onDestroy();
      this.mapView.stop();
   }
}
