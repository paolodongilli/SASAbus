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

import java.util.ArrayList;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DistanceBusStationAdapter extends ArrayAdapter<DistanceBusStation>
{
   MainActivity mainActivity;

   public DistanceBusStationAdapter(MainActivity context, ArrayList<DistanceBusStation> objects)
   {
      super(context, 0, objects);
      this.mainActivity = context;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
      DistanceBusStation curr = this.getItem(position);
      if (convertView == null)
      {
         convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.listview_item_busstop_nearby,
                                                                      parent,
                                                                      false);
      }
      TextView nameTextView = (TextView) convertView.findViewById(R.id.textview_busstop);
      TextView distTextView = (TextView) convertView.findViewById(R.id.textview_distance);

      nameTextView.setText(this.mainActivity.getBusStationNameUsingAppLanguage(curr.busStation));

      distTextView.setText(String.format(Locale.ITALY, "%,.0fm", curr.distance));

      return convertView;
   }
}
