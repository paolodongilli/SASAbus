/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusScheduleDetailsFragment.java
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

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.DeparturesThread;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.ui.MainActivity;
import java.io.IOException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

public class BusScheduleDetailsFragment extends SherlockFragment
{

   String           busLineShortName;
   BusDepartureItem item;

   public void setData(String busLineShortName, BusDepartureItem item)
   {
      this.busLineShortName = busLineShortName;
      this.item = item;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {

      try
      {
         ListView listview_line_course;

         MainActivity mainActivity = (MainActivity) this.getActivity();

         View ret = inflater.inflate(R.layout.fragment_busline_details, container, false);
         listview_line_course = (ListView) ret.findViewById(R.id.listview_line_course);
         ArrayAdapter<String> stops = new ArrayAdapter<String>(this.getActivity(),
                                                               android.R.layout.simple_list_item_1)
         {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
               View superView = super.getView(position, convertView, parent);
               if (position == BusScheduleDetailsFragment.this.item.index)
               {
                  superView.setBackgroundColor(Color.LTGRAY);
               }
               else
               {
                  superView.setBackgroundColor(Color.WHITE);
               }
               return superView;
            }
         };
         for (int i = 0; i < this.item.stopTimes.length; i++)
         {
            BusTripBusStopTime stopTime = this.item.stopTimes[i];
            String busStationName = mainActivity.getBusStationNameUsingAppLanguage(mainActivity.getOpenDataStorage().getBusStations().findBusStop(stopTime.getBusStop()).getBusStation());
            stops.add(DeparturesThread.formatSeconds(stopTime.getSeconds())
                      + (i >= this.item.delay_index ? "  " + this.item.delay : "")
                      + " - "
                      + busStationName);
         }
         listview_line_course.setAdapter(stops);
         int pos = this.item.index;
         if (pos > 0)
         {
            pos--;
         }
         listview_line_course.setSelection(pos);
         TextView busLineNameView = (TextView) ret.findViewById(R.id.textview_busline_number);
         busLineNameView.setText(this.busLineShortName);
         TextView busStopNameView = (TextView) ret.findViewById(R.id.textview_busstop_name);
         busStopNameView.setText(""); // Not used
         return ret;
      }
      catch (IOException ioxxx)
      {
         throw new RuntimeException(ioxxx);
      }
   }
}
