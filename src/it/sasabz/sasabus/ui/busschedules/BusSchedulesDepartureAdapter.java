/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusSchedulesDepartureAdapter.java
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
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BusSchedulesDepartureAdapter extends ArrayAdapter<BusDepartureItem>
{

   public BusSchedulesDepartureAdapter(Context context, List<BusDepartureItem> objects)
   {
      super(context, 0, objects);
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
      if (convertView == null)
      {
         LayoutInflater li = LayoutInflater.from(this.getContext());
         convertView = li.inflate(R.layout.fragment_bus_schedules_departure_list_item, null);
      }
      TextView time = (TextView) convertView.findViewById(R.id.fragment_bus_schedules_departure_list_item_textViewTime);
      TextView busStopName = (TextView) convertView.findViewById(R.id.fragment_bus_schedules_departure_list_item_textViewBusStopName);
      TextView destinationName = (TextView) convertView.findViewById(R.id.fragment_bus_schedules_departure_list_item_textViewDestination);

      TextView delay = (TextView) convertView.findViewById(R.id.textViewDelay);
      delay.setText(this.getItem(position).getSelectedIndex() < this.getItem(position).getDelay_index()
                                                                                                       ? ""
                                                                                                       : this.getItem(position).getDelay());

      time.setText(this.getItem(position).getTime());
      busStopName.setText(this.getItem(position).getBusStopOrLineName());
      destinationName.setText(this.getItem(position).getDestinationName());

      return convertView;
   }

}
