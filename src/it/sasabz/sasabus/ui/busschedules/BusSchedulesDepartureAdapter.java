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
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.TextureView;
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
	         convertView = li.inflate(R.layout.departures_detail_row, null);
	      }
	   
	   BusDepartureItem listitem = getItem(position);
	   
	   TextView txt_departuretime = (TextView)convertView.findViewById(R.id.txt_departuretime);
	   TextView txt_delay = (TextView)convertView.findViewById(R.id.txt_delay);
	   TextView txt_field1 = (TextView)convertView.findViewById(R.id.txt_field1);
	   TextView txt_laststop = (TextView)convertView.findViewById(R.id.txt_laststop);
	   
	   txt_departuretime.setText(listitem.getTime());
	   if(listitem.getSelectedIndex() < listitem.getDelay_index() || !listitem.isRealtime())
	   {
		   txt_delay.setText(R.string.no_realtime);
		   txt_delay.setTextColor(Color.BLACK);
		   txt_delay.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);
	   }
	   else
	   {
		   txt_delay.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
		   txt_delay.setTypeface(null, Typeface.BOLD);
		   txt_delay.setText(listitem.getDelay());
		   int delay = listitem.getDelayNumber();
		   if(delay < -2)
		   {
			   txt_delay.setTextColor(Color.CYAN);
		   }
		   else if(delay < 2)
		   {
			   txt_delay.setTextColor(Color.GREEN);
		   }
		   else if(delay < 4)
		   {
			   txt_delay.setTextColor(getContext().getResources().getColor(R.color.sasa_orange));
		   }
		   else
		   {
			   txt_delay.setTextColor(Color.RED);
		   }
	   }
	  
	   txt_field1.setText(listitem.getBusStopOrLineName());
	   txt_laststop.setText(getContext().getString(R.string.laststop, listitem.getDestinationName()));
	   return convertView;
   }

}
