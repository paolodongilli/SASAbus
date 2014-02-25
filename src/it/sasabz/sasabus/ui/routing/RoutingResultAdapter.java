/*
 * SASAbus - Android app for SASA bus open data
 *
 * RoutingResultAdapter.java
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

package it.sasabz.sasabus.ui.routing;

import it.bz.tis.sasabus.backend.shared.travelplanner.BasicStop;
import it.bz.tis.sasabus.backend.shared.travelplanner.ConSection;
import it.bz.tis.sasabus.backend.shared.travelplanner.Connection;
import it.sasabz.sasabus.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class RoutingResultAdapter extends BaseExpandableListAdapter
{

   Connection[] connections;
   Context      context;

   public RoutingResultAdapter(Connection[] connections, Context context)
   {
      super();
      this.connections = connections;
      this.context = context;
   }

   @Override
   public Object getChild(int groupPosition, int childPosition)
   {
      return 0;
   }

   @Override
   public long getChildId(int arg0, int arg1)
   {
      return 0;
   }

   @Override
   public View getChildView(int groupPosition,
                            int childPosition,
                            boolean isLastChild,
                            View convertView,
                            ViewGroup parent)
   {

      View view;
      if (convertView == null)
      {
         LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         view = inflater.inflate(R.layout.listview_item_search_result_detail, null);
      }
      else
      {
         view = convertView;
      }

      ConSection conSection = connections[groupPosition].getConSectionList().getConSections()[childPosition];

      TextView busNrTextView = (TextView) view.findViewById(R.id.textview_time_departure);
      TextView departureTimeTextView = (TextView) view.findViewById(R.id.textview_departure_time);
      TextView departureStationTextView = (TextView) view.findViewById(R.id.textview_departure_busstop);

      TextView arrivalTimeTextView = (TextView) view.findViewById(R.id.textview_arrival_time);
      TextView arrivalStationTextView = (TextView) view.findViewById(R.id.textview_arrival_busstop);

      if (conSection.getWalks().length > 0)
      {
         busNrTextView.setText("Walk");
         departureTimeTextView.setText("");
         arrivalTimeTextView.setText("");
         departureStationTextView.setText("");
         arrivalStationTextView.setText("");
      }
      else
      {
         final BasicStop[] basicStop = conSection.getJourneys()[0].getPassList().getBasicStops();

         String busNr = conSection.getJourneys()[0].getBusLineNumber();

         busNrTextView.setText(busNr);

         String startTime = "";
         if (basicStop[0].getDep() != null)
         {
            startTime = formatTime(basicStop[0].getDep().getTime());
         }

         departureTimeTextView.setText(startTime);

         String endTime = "";
         if (basicStop[basicStop.length - 1].getArr() != null)
         {
            endTime = formatTime(basicStop[basicStop.length - 1].getArr().getTime());
         }

         arrivalTimeTextView.setText(endTime);

         departureStationTextView.setText(basicStop[0].getStation().getName());

         arrivalStationTextView.setText(basicStop[basicStop.length - 1].getStation().getName());

      }

      return view;
   }

   @Override
   public int getChildrenCount(int groupPosition)
   {
      return connections[groupPosition].getConSectionList().getConSections().length;
   }

   @Override
   public Object getGroup(int groupPosition)
   {
      return null;
   }

   @Override
   public int getGroupCount()
   {
      return connections.length;
   }

   @Override
   public long getGroupId(int groupPosition)
   {
      return 0;
   }

   @Override
   public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
   {

      Connection connection = connections[groupPosition];

      View view;
      if (convertView == null)
      {
         LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         view = inflater.inflate(R.layout.listview_item_search_result, null);
      }
      else
      {
         view = convertView;
      }

      String startTime = formatTime(connection.getOverview().getDeparture().getBasicStop().getDep().getTime());
      String transfers = "Transfers: " + connection.getOverview().getTransfers();
      String duration = "Duration: " + formatTime(connection.getOverview().getDuration().getTime());
      String endTime = formatTime(connection.getOverview().getArrival().getBasicStop().getArr().getTime());

      TextView departureTime = (TextView) view.findViewById(R.id.textview_time_departure);
      departureTime.setText(startTime);

      TextView durationTextView = (TextView) view.findViewById(R.id.textview_duration);
      durationTextView.setText(duration);

      TextView transfersTextView = (TextView) view.findViewById(R.id.textview_changes);
      transfersTextView.setText(transfers);

      TextView arrivalTextView = (TextView) view.findViewById(R.id.textview_time_arrival);
      arrivalTextView.setText(endTime);

      return view;
   }

   @Override
   public boolean hasStableIds()
   {
      return true;
   }

   @Override
   public boolean isChildSelectable(int groupPosition, int childPosition)
   {
      return false;
   }

   static String formatTime(String time)
   {
      if (time.startsWith("00d"))
      {
         time = time.substring(3);
      }
      String[] timeParts = time.split(":");
      time = timeParts[0] + ":" + timeParts[1];
      return time;
   }

}
