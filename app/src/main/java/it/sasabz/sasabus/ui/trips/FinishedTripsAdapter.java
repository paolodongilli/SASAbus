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

package it.sasabz.sasabus.ui.trips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import it.bz.tis.sasabus.backend.shared.travelplanner.BasicStop;
import it.bz.tis.sasabus.backend.shared.travelplanner.ConSection;
import it.bz.tis.sasabus.backend.shared.travelplanner.Connection;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.data.trips.FinishedTrip;
import it.sasabz.sasabus.opendata.client.model.BusLine;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class FinishedTripsAdapter extends ArrayAdapter<FinishedTrip>
{

   public FinishedTripsAdapter(Context context, List<FinishedTrip> objects)
   {
      super(context, 0, objects);
   }



   @Override
   public View getView(int position, View convertView, ViewGroup parent) {


      if (convertView == null) {
         LayoutInflater li = LayoutInflater.from(this.getContext());
         convertView = li.inflate(R.layout.item_finished_trips, null);
      }

      TextView date = (TextView) convertView.findViewById(R.id.textview_date);
      TextView line = (TextView) convertView.findViewById(R.id.textView_line);
      TextView duration = (TextView) convertView.findViewById(R.id.textview_duration);
      TextView departureTime = (TextView) convertView.findViewById(R.id.textview_time_departure);
      TextView arrivalTime = (TextView) convertView.findViewById(R.id.textview_time_arrival);
      TextView departure = (TextView) convertView.findViewById(R.id.textview_departure);
      TextView arrival = (TextView) convertView.findViewById(R.id.textview_arrival);

      FinishedTrip finishedTrip = getItem(position);
      line.setText(getLineName(finishedTrip.getLineId()));
      date.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(finishedTrip.getStartTime()));
      departureTime.setText(new SimpleDateFormat("HH:mm:ss").format(finishedTrip.getStartTime()));
      arrivalTime.setText(new SimpleDateFormat("HH:mm:ss").format(finishedTrip.getFinishTime()));
      departure.setText(getBusStopName(finishedTrip.getStartOrt()));
      arrival.setText(getBusStopName(finishedTrip.getFinishOrt()));
      duration.setText(finishedTrip.getDuration() + "h");
      return convertView;

   }

   public String getBusStopName(int ortId){
      try {
         return ((MainActivity) getContext()).getBusStationNameUsingAppLanguage(
                 ((MainActivity) getContext()).getOpenDataStorage().getBusStations().findBusStop(ortId).getBusStation());
      } catch (Exception e) {
         e.printStackTrace();
      }
      return "";
   }

   public String getLineName(int lineId){
      try {
         BusLine[] busLines = ((MainActivity)getContext()).getOpenDataStorage().getBusLines().getList();
      for (BusLine busLine : busLines)
      {
         if(lineId == busLine.getLI_NR())
            return busLine.getShortName();
      }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "";
   }


}
